package net.deuce.moman.rcp.account;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.account.Account;

import org.eclipse.jface.wizard.Wizard;

public class NewAccountWizard extends Wizard {
	
	private FinancialInstitutionPage financialInstitutionPage;
	private AccountInfoPage accountInfoPage;
	
	public NewAccountWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		financialInstitutionPage = new FinancialInstitutionPage();
		accountInfoPage = new AccountInfoPage();
		addPage(financialInstitutionPage);
		addPage(accountInfoPage);
	}


	@Override
	public boolean performFinish() {
		Account account = accountInfoPage.getAccount();
		account.setFinancialInstitution(financialInstitutionPage.getFinancialInstitution());
		Registry.instance().addAccount(account);
		return true;
	}

}
