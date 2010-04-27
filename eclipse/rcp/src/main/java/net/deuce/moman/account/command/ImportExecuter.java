package net.deuce.moman.account.command;

import java.util.Date;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.fi.FinancialInstitutionService;
import net.deuce.moman.entity.service.transaction.TransactionFetchResult;
import net.deuce.moman.transaction.ui.AbstractSwtTransactionProcessor;

import org.eclipse.swt.widgets.Shell;

public class ImportExecuter extends AbstractSwtTransactionProcessor {

	private FinancialInstitutionService financialInstitutionService = ServiceProvider.instance().getFinancialInstitutionService();

	public ImportExecuter(Shell shell, Account account, boolean force,
			String focusedView) {
		super(shell, account, force, focusedView);
	}

	protected TransactionFetchResult fetchTransactions() throws Exception {
		Date startDate = getLastDownloadedDate();
		Date endDate = new Date();
		return financialInstitutionService.fetchTransactions(getAccount(),
				startDate, endDate);
	}

}
