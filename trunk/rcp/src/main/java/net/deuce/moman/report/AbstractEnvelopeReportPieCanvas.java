package net.deuce.moman.report;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.report.EnvelopeDataSetResult.EnvelopeResult;
import net.deuce.moman.util.DataDateRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEnvelopeReportPieCanvas extends
		AbstractReportCanvas implements EntityListener<InternalTransaction> {

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private Stack<EnvelopeSource> envelopeStack = new Stack<EnvelopeSource>();
	private EnvelopeDataSetResult result;
	private EnvelopeSource topEnvelopeSource;
	private boolean deepEnvelopeTransactions = false;

	public AbstractEnvelopeReportPieCanvas(Composite parent,
			final DateRangeCombo combo, int style) {
		super(parent, combo, style);

		transactionService.addEntityListener(this);

		topEnvelopeSource = getInitialEnvelopeSource();
		envelopeStack.push(topEnvelopeSource);

		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					if (envelopeStack.size() > 1) {
						popSourceEnvelope();
						regenerateChart();
					}
				}
			}
		});
	}

	public boolean isDeepEnvelopeTransactions() {
		return deepEnvelopeTransactions;
	}

	public void setDeepEnvelopeTransactions(boolean deepEnvelopeTransactions) {
		this.deepEnvelopeTransactions = deepEnvelopeTransactions;
	}

	protected EnvelopeSource getInitialEnvelopeSource() {
		return new EnvelopeSource(null, null,
				envelopeService.getAllEnvelopes(), "Top");
	}

	public EnvelopeSource getTopEnvelopeSource() {
		return topEnvelopeSource;
	}

	public EnvelopeDataSetResult getResult() {
		return result;
	}

	public void setResult(EnvelopeDataSetResult result) {
		this.result = result;
	}

	public EnvelopeSource popSourceEnvelope() {
		return envelopeStack.pop();
	}

	public EnvelopeSource peekSourceEnvelope() {
		return envelopeStack.peek();
	}

	public void pushSourceEnvelope(EnvelopeSource sourceEnvelope) {
		this.envelopeStack.push(sourceEnvelope);
	}

	public boolean hasSourceEnvelopes() {
		return envelopeStack.size() > 0;
	}

	public List<EnvelopeSource> getSourceEnvelopes() {
		return new LinkedList<EnvelopeSource>(envelopeStack);
	}

	private EnvelopeResult calculateResult(Envelope env, boolean expense,
			boolean recursive, List<InternalTransaction> allTransactions) {
		double sum = 0.0;
		List<Account> accounts = accountService.getSelectedAccounts();

		for (DataDateRange ddr : getDateRange().dataDateRanges()) {
			for (Account account : accounts) {
				for (InternalTransaction it : env.getAccountTransactions(
						account, ddr, deepEnvelopeTransactions && recursive)) {
					if (it.isEnvelopeTransfer())
						continue;

					Double splitAmount = 0.0;

					for (Split s : it.getSplit()) {
						if (env == s.getEnvelope()
								|| env.contains(s.getEnvelope(), true)) {
							splitAmount += s.getAmount();
						}
					}

					if ((expense && splitAmount <= 0.0)
							|| (!expense && splitAmount > 0.0)) {
						allTransactions.add(it);
						sum += expense ? -splitAmount : splitAmount;
					}
				}
			}
		}
		return new EnvelopeResult(env, sum);
	}

	protected DataSetResult createDataSet(boolean expense) {
		double maxSum = 0.0;
		double minSum = Double.MAX_VALUE;
		List<EnvelopeResult> dataSet = new LinkedList<EnvelopeResult>();

		EnvelopeSource envelopeSource = envelopeStack.peek();
		List<Envelope> envelopes = new LinkedList<Envelope>();
		/*
		 * if (envelopeSource.getEnvelope() != null) {
		 * envelopes.add(envelopeSource.getEnvelope()); }
		 */
		envelopes.addAll(envelopeSource.getAvailableEnvelopes());

		List<InternalTransaction> allTransactions = new LinkedList<InternalTransaction>();

		if (envelopeSource.getEnvelope() != null) {
			EnvelopeResult result = calculateResult(envelopeSource
					.getEnvelope(), expense, false, allTransactions);
			maxSum = Math.max(result.getValue(), maxSum);
			minSum = Math.min(result.getValue(), minSum);
			dataSet.add(result);
		}

		for (Envelope env : envelopes) {
			if (env != envelopeSource.getEnvelope()) {
				EnvelopeResult result = calculateResult(env, expense, true,
						allTransactions);
				maxSum = Math.max(result.getValue(), maxSum);
				minSum = Math.min(result.getValue(), minSum);
				dataSet.add(result);
			}
		}

		Collections.sort(dataSet);

		double allOtherTotal = 0.0;

		EnvelopeResult remaining = null;
		int count = 0;

		for (int i = dataSet.size() - 1; i >= 5; i--) {
			EnvelopeResult result = dataSet.get(i);
			if (result.getValue() > 0) {
				count++;
				remaining = result;
			}
			allOtherTotal += result.getValue();
			dataSet.remove(i);
		}

		if (count == 1) {
			dataSet.add(remaining);
			allOtherTotal = 0.0;
		}

		// remove any zero valued categories
		for (int i = dataSet.size() - 1; i >= 0; i--) {
			EnvelopeResult result = dataSet.get(i);
			if (result.getValue() == 0.0) {
				dataSet.remove(i);
			}
		}

		transactionService.setCustomTransactionList(allTransactions);

		result = new EnvelopeDataSetResult(dataSet, allOtherTotal, minSum,
				maxSum);
		return result;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public EnvelopeService getEnvelopeService() {
		return envelopeService;
	}

	public TransactionService getTransactionService() {
		return transactionService;
	}

	public void entityAdded(EntityEvent<InternalTransaction> event) {
	}

	public void entityChanged(EntityEvent<InternalTransaction> event) {
	}

	public void entityRemoved(EntityEvent<InternalTransaction> event) {
	}

	protected static class EnvelopeSource {
		private Envelope envelope;
		private EnvelopeSource parentSource;
		private List<Envelope> availableEnvelopes = new LinkedList<Envelope>();
		private List<Envelope> topEnvelopes = new LinkedList<Envelope>();
		private String label;

		public EnvelopeSource(EnvelopeSource parentSource, Envelope envelope,
				List<Envelope> availableEnvelopes, String label) {
			super();
			this.parentSource = parentSource;
			this.envelope = envelope;
			this.availableEnvelopes = availableEnvelopes;
			this.label = label;
		}

		public EnvelopeSource getParentSource() {
			return parentSource;
		}

		public void setParentSource(EnvelopeSource parentSource) {
			this.parentSource = parentSource;
		}

		public List<Envelope> getTopEnvelopes() {
			return topEnvelopes;
		}

		public void setTopEnvelopes(List<Envelope> topEnvelopes) {
			this.topEnvelopes = topEnvelopes;
		}

		public Envelope getEnvelope() {
			return envelope;
		}

		public void setEnvelope(Envelope envelope) {
			this.envelope = envelope;
		}

		public List<Envelope> getAvailableEnvelopes() {
			return new LinkedList<Envelope>(availableEnvelopes);
		}

		public void setAvailableEnvelopes(List<Envelope> availableEnvelopes) {
			this.availableEnvelopes = availableEnvelopes;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}
}
