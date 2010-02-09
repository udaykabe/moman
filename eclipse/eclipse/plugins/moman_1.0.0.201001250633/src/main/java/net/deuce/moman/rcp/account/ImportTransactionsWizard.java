package net.deuce.moman.rcp.account;

import net.deuce.moman.model.account.Account;

import org.eclipse.jface.wizard.Wizard;

public class ImportTransactionsWizard extends Wizard {
	
	private Account account;
	private BankDownloadingPage bankDownloadingPage;
	
	public ImportTransactionsWizard(Account account) {
		super();
		setNeedsProgressMonitor(true);
		this.account = account;
	}
	
	@Override
	public void addPages() {
		bankDownloadingPage = new BankDownloadingPage(account);
		addPage(bankDownloadingPage);
	}


	@Override
	public boolean performFinish() {
		return true;
	}

}
