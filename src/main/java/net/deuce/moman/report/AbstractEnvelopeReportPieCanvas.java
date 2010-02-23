package net.deuce.moman.report;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.report.EnvelopeDataSetResult.EnvelopeResult;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.DataDateRange;

import org.eclipse.birt.chart.device.ICallBackNotifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;

public abstract class AbstractEnvelopeReportPieCanvas extends
		AbstractReportCanvas implements EntityListener<InternalTransaction>,
		ICallBackNotifier {

	private AccountService accountService;
	private TransactionService transactionService;
	private EnvelopeService envelopeService;
	private Stack<EnvelopeSource> envelopeStack = new Stack<EnvelopeSource>();
	private EnvelopeDataSetResult result;
	private EnvelopeSource topEnvelopeSource;

	public AbstractEnvelopeReportPieCanvas(Composite parent,
			final DateRangeCombo combo, int style) {
		super(parent, combo, style);

		accountService = ServiceNeeder.instance().getAccountService();
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		transactionService = ServiceNeeder.instance().getTransactionService();
		transactionService.addEntityListener(this);
		
		topEnvelopeSource = new EnvelopeSource(null, envelopeService.getAllEnvelopes(), "Top");
		System.out.println("ZZZ all envelopes: " + envelopeService.getAllEnvelopes());
		envelopeStack.push(topEnvelopeSource);
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

	protected DataSetResult createDataSet(boolean expense) {
		double maxSum = 0.0;
		double minSum = Double.MAX_VALUE;
		List<EnvelopeResult> dataSet = new LinkedList<EnvelopeResult>();
		List<Account> accounts = accountService.getSelectedAccounts();
		
		EnvelopeSource envelopeSource = envelopeStack.peek();
		List<Envelope> envelopes = new LinkedList<Envelope>();
		if (envelopeSource.getEnvelope() != null) {
			envelopes.add(envelopeSource.getEnvelope());
		}
		envelopes.addAll(envelopeSource.getAvailableEnvelopes());

		for (Envelope env : envelopes) {
			if (env.getTransactions().size() == 0) continue;
			double sum = 0.0;
			for (DataDateRange ddr : getDateRange().dataDateRanges()) {
				for (Account account : accounts) {
					for (InternalTransaction it : env.getAccountTransactions(account)) {
						if (it.isEnvelopeTransfer()) continue;
						
						Double splitAmount = it.getSplitAmount(env);
						if ((expense && splitAmount <= 0.0) || (!expense && splitAmount > 0.0)) {
							if (CalendarUtil.dateInRange(it.getDate(), ddr)) {
								sum += expense ? -splitAmount : splitAmount;
							}
						}
					}
				}
				if (sum > maxSum) {
					maxSum = sum;
				}
				if (sum < minSum) {
					minSum = sum;
				}
			}
			dataSet.add(new EnvelopeResult(env, sum));
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
		}

		result = new EnvelopeDataSetResult(dataSet, allOtherTotal, minSum, maxSum);
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

	@Override
	public void entityAdded(EntityEvent<InternalTransaction> event) {
	}

	@Override
	public void entityChanged(EntityEvent<InternalTransaction> event) {
	}

	@Override
	public void entityRemoved(EntityEvent<InternalTransaction> event) {
	}
	
	protected static class EnvelopeSource {
		private Envelope envelope;
		private List<Envelope> availableEnvelopes = new LinkedList<Envelope>();
		private List<Envelope> topEnvelopes = new LinkedList<Envelope>();
		private String label;
		
		public EnvelopeSource(Envelope envelope, List<Envelope> availableEnvelopes, String label) {
			super();
			this.envelope = envelope;
			this.availableEnvelopes = availableEnvelopes;
			this.label = label;
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