package net.deuce.moman.rcp.account;

import net.deuce.moman.model.account.Account;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AccountInfoPage extends WizardPage {
	
	private Text bankIdText;
	private Text accountIdText;
	private Text usernameText;
	private Text passwordText;
	private Text nicknameText;
	private Account account;

	public AccountInfoPage() {
		super("Account Info");
		setTitle("Account Info");
	}
	
	public Account getAccount() {
		return account;
	}
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		
		// layout.horizontalAlignment = GridData.FILL;
	
		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		nicknameText = createTextField(container, gridData, "Nickname", false);
		bankIdText = createTextField(container, gridData, "Routing #", false);
		bankIdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkFields();
			}
		});
		accountIdText = createTextField(container, gridData, "Account #", false);
		accountIdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkFields();
			}
		});
		usernameText = createTextField(container, gridData, "Username", false);
		usernameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkFields();
			}
		});
		passwordText = createTextField(container, gridData, "Password", true);
		passwordText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkFields();
			}
		});
		
		setControl(container);
		setPageComplete(false);
	}
	
	protected Text createTextField(Composite parent, GridData gridData, String text, boolean password) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		int style = SWT.BORDER;
		if (password) {
			style |= SWT.PASSWORD;
		}
		Text textField = new Text(parent, style);
		textField.setLayoutData(gridData);
		return textField;
	}

	protected void checkFields() {
		if (	bankIdText.getText().length() > 0 &&
				accountIdText.getText().length() > 0 &&
				usernameText.getText().length() > 0 &&
				passwordText.getText().length() > 0) {
			
			account = new Account();
			account.setAccountId(accountIdText.getText());
			account.setBankId(bankIdText.getText());
			account.setNickname(nicknameText.getText());
			account.setUsername(usernameText.getText());
			account.setPassword(passwordText.getText());
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

}
