package net.deuce.moman.account.command;

import java.util.Date;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.service.TransactionFetchResult;
import net.deuce.moman.transaction.service.TransactionProcessor;

import org.eclipse.swt.widgets.Shell;

public class ImportExecuter extends TransactionProcessor {
	
	public ImportExecuter(Shell shell, Account account, boolean force, String focusedView) {
		super(shell, account, force, focusedView);
	}
	
	protected TransactionFetchResult fetchTransactions(Account account) throws Exception {
		Date startDate = getLastDownloadedDate();
		Date endDate = new Date();
		return ServiceNeeder.instance().getFinancialInstitutionService()
			.fetchTransactions(account, startDate, endDate);
	}
	
}
