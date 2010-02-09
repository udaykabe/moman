package net.deuce.moman.rcp.account;

import net.deuce.moman.model.account.Account;
import net.deuce.moman.rcp.AbstractModelDialog;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AccountDialog extends AbstractModelDialog<Account> {
	
	private Text bankIdText;
	private Text accountIdText;
	private Text usernameText;
	private Text passwordText;
	private Text nicknameText;
	private Account account;

	public AccountDialog(Shell shell) {
		super(shell);
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	protected void createTextFields(Composite parent, GridData gridData) {
		nicknameText = createTextField(parent, gridData, "Nickname", false);
		bankIdText = createTextField(parent, gridData, "Routing #", false);
		accountIdText = createTextField(parent, gridData, "Account #", false);
		usernameText = createTextField(parent, gridData, "Username", false);
		passwordText = createTextField(parent, gridData, "Password", true);
		
		if (account != null) {
			nicknameText.setText(account.getNickname());
			bankIdText.setText(account.getBankId());
			accountIdText.setText(account.getAccountId());
			usernameText.setText(account.getUsername());
			passwordText.setText(account.getPassword());
		}
	}

	@Override
	protected boolean isValidInput() {
		if (bankIdText.getText().length() == 0) {
			setErrorMessage("Please enter a routing #");
			return false;
		}
		
		if (accountIdText.getText().length() == 0) {
			setErrorMessage("Please enter an account #");
			return false;
		}
		if (usernameText.getText().length() == 0) {
			setErrorMessage("Please enter a username");
			return false;
		}
		if (passwordText.getText().length() == 0) {
			setErrorMessage("Please enter a password");
			return false;
		}
		return true;
	}

	@Override
	protected void saveInput() {
		account.setAccountId(accountIdText.getText());
		account.setBankId(bankIdText.getText());
		account.setNickname(nicknameText.getText());
		account.setUsername(usernameText.getText());
		account.setPassword(passwordText.getText());
	}

}
