package net.deuce.moman.report;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.DataDateRange;

import org.eclipse.birt.chart.device.ICallBackNotifier;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTransactionReportCanvas extends
		AbstractReportCanvas implements EntityListener<InternalTransaction>,
		ICallBackNotifier {

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public AbstractTransactionReportCanvas(Composite parent,
			final DateRangeCombo combo, int style) {
		super(parent, combo, style);

		transactionService.addEntityListener(this);
	}

	protected DataSetResult createDataSet(boolean expense) {
		double maxSum = 0.0;
		double minSum = Double.MAX_VALUE;
		List<Double> dataSet = new LinkedList<Double>();
		List<Account> accounts = accountService.getSelectedAccounts();

		List<List<InternalTransaction>> dataPointTransactionList = new LinkedList<List<InternalTransaction>>();

		Map<Account, List<InternalTransaction>> accountTransactions = new HashMap<Account, List<InternalTransaction>>();

		for (DataDateRange ddr : getDateRange().dataDateRanges()) {
			double sum = 0.0;
			List<InternalTransaction> dataPointTransactions = new LinkedList<InternalTransaction>();
			for (Account account : accounts) {
				List<InternalTransaction> transactions = accountTransactions
						.get(account);
				if (transactions == null) {
					transactions = transactionService.getAccountTransactions(
							account, false);
					accountTransactions.put(account, transactions);
				}

				for (InternalTransaction it : transactions) {
					if (!it.isEnvelopeTransfer()
							&& ((expense && it.getAmount() <= 0.0) || (!expense && it
									.getAmount() > 0.0))) {
						if (CalendarUtil.dateInRange(it.getDate(), ddr)) {
							sum += expense ? -it.getAmount() : it.getAmount();
							dataPointTransactions.add(it);
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

			dataPointTransactionList.add(dataPointTransactions);
			dataSet.add(sum);
		}

		return new DataSetResult(dataPointTransactionList, dataSet, minSum,
				maxSum);
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
}
