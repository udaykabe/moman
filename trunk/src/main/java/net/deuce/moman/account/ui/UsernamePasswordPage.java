package net.deuce.moman.account.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UsernamePasswordPage extends WizardPage {
	
	private Text usernameText;
	private Text passwordText;

	public UsernamePasswordPage() {
		super("Username/Password");
		setTitle("Username/Password");
	}
	
	public String getUsername() {
		return usernameText.getText();
	}
	
	public String getPassword() {
		return passwordText.getText();
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
		
		usernameText.setText("fruptichase");
		passwordText.setText("cqopklm7");
		
		setControl(container);
		setPageComplete(true);
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
		if (usernameText.getText().length() > 0 &&
				passwordText.getText().length() > 0) {
			
			((NewAccountWizard)getWizard()).setUsername(usernameText.getText());
			((NewAccountWizard)getWizard()).setPassword(passwordText.getText());
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

}
