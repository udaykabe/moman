package net.deuce.moman.account.ui;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.account.AccountFactory;
import net.deuce.moman.ui.AbstractModelDialog;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AccountDialog extends AbstractModelDialog<Account> {

	private Text bankIdText;
	private Text accountIdText;
	private Text usernameText;
	private Text passwordText;
	private Text initialBalanceText;
	private Text nicknameText;
	private Account account;
	private boolean online = true;

	private AccountFactory accountFactory = ServiceProvider.instance().getAccountFactory();

	public AccountDialog(Shell shell, boolean online) {
		super(shell);
		this.online = online;
	}

	public AccountDialog(Shell shell) {
		super(shell);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	protected void createTextFields(Composite parent, GridData gridData) {
		nicknameText = createTextField(parent, gridData, "Nickname", false);
		bankIdText = createTextField(parent, gridData, "Routing #", false);
		accountIdText = createTextField(parent, gridData, "Account #", false);
		usernameText = createTextField(parent, gridData, "Username", false);
		passwordText = createTextField(parent, gridData, "Password", true);
		initialBalanceText = createTextField(parent, gridData,
				"Initial Balance", false);

		if (account != null) {
			nicknameText.setText(account.getNickname());
			bankIdText.setText(account.getBankId());
			accountIdText.setText(account.getAccountId());
			usernameText.setText(account.getUsername());
			passwordText.setText(account.getPassword());
			initialBalanceText
					.setText(account.getInitialBalance() != null ? RcpConstants.CURRENCY_VALIDATOR
							.format(account.getInitialBalance())
							: "");
		}
	}

	protected boolean isValidInput() {
		if (!online && nicknameText.getText().length() == 0) {
			setErrorMessage("Please enter a nickname");
			return false;
		}

		if (online && bankIdText.getText().length() == 0) {
			setErrorMessage("Please enter a routing #");
			return false;
		}

		if (online && accountIdText.getText().length() == 0) {
			setErrorMessage("Please enter an account #");
			return false;
		}
		if (online && usernameText.getText().length() == 0) {
			setErrorMessage("Please enter a username");
			return false;
		}
		if (online && passwordText.getText().length() == 0) {
			setErrorMessage("Please enter a password");
			return false;
		}
		if (initialBalanceText.getText().length() > 0
				&& !RcpConstants.CURRENCY_VALIDATOR.isValid(initialBalanceText
						.getText())) {
			setErrorMessage("Invalid initial balance value");
			return false;
		}
		return true;
	}

	private String getFieldText(Text field) {
		return field.getText() != null ? field.getText() : "";
	}

	protected void saveInput() {
		try {
			if (account == null) {
				Double initialBalance = null;

				if (initialBalanceText.getText().length() > 0) {
					initialBalance = RcpConstants.CURRENCY_VALIDATOR.validate(
							initialBalanceText.getText()).doubleValue();
				}
				account = accountFactory.newEntity(true, null, null, null,
						null, null, null, false, 0.0, 0.0, 0.0);
				if (initialBalance != null) {
					account.setBalance(initialBalance);
				}
			}
			account.setAccountId(getFieldText(accountIdText));
			account.setBankId(getFieldText(bankIdText));
			account.setNickname(getFieldText(nicknameText));
			account.setUsername(getFieldText(usernameText));
			account.setPassword(getFieldText(passwordText));
			if (initialBalanceText.getText().length() > 0) {
				account.setInitialBalance(RcpConstants.CURRENCY_VALIDATOR
						.validate(initialBalanceText.getText()).doubleValue());
			} else {
				account.setInitialBalance(null);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
