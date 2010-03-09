package net.deuce.moman.account.ui;

import java.util.List;

import net.deuce.moman.account.command.ImportExecuter;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.fi.model.FinancialInstitution;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.ui.RegisterView;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

public class NewAccountWizard extends Wizard {
	
	private UsernamePasswordPage usernamePasswordPage;
	private FinancialInstitutionPage financialInstitutionPage;
	private AvailableAccountsPage availableAccountsPage;
	private String username;
	private String password;
	private FinancialInstitution financialInstitution;
	private List<Account> availableAccounts;
	private AccountService accountService;
	private EnvelopeService envelopeService;
	
	public NewAccountWizard() {
		super();
		setNeedsProgressMonitor(true);
		accountService = ServiceNeeder.instance().getAccountService();
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
	}
	
	@Override
	public void addPages() {
		usernamePasswordPage = new UsernamePasswordPage();
		financialInstitutionPage = new FinancialInstitutionPage();
		availableAccountsPage = new AvailableAccountsPage();
		addPage(financialInstitutionPage);
		addPage(usernamePasswordPage);
		addPage(availableAccountsPage);
	}

	public List<Account> getAvailableAccounts() {
		return availableAccounts;
	}

	public void setAvailableAccounts(List<Account> availableAccounts) {
		this.availableAccounts = availableAccounts;
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean performFinish() {
		for (Account account : availableAccounts) {
			if (!account.isSelected()) continue;
			if (accountService.doesAccountExist(account)) {
				String message = "An account already exists with the same routing and account numbers (" + account.getBankId() + " / " + account.getAccountId() + "). Proceed?";
				MessageDialog dialog = new MessageDialog(getShell(), "Duplicate account?", null, message,
						MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL }, 1);
				if (dialog.open() != 0) {
					continue;
				}
			}
			account.setFinancialInstitution(financialInstitution);
			accountService.addEntity(account);
			account.setSelected(true);
			if (accountService.getEntities().size() == 1) {
				List<String> ids = ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
				try {
					envelopeService.importDefaultEnvelopes();
				} finally {
					ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications(ids);
				}
			}
			
//			serviceContainer.setMonitoring(false);
			try {
				new ImportExecuter(Display.getCurrent().getActiveShell(), account, true, RegisterView.ID).execute();
			} catch (Throwable e) {
				MessageDialog.openError(getShell(), "Transactions Download Error", 
						"Failed downloading transactions from " + financialInstitution.getName());
//			} finally {
//				serviceContainer.setMonitoring(true);
//				transactionService.notifyEntityListenersOfAdditions();
//				envelopeService.notifyEntityListenersOfAdditions();
			}
		}
		return true;
	}

}
