package net.deuce.moman.account.ui;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DownloadTransactionsPage extends WizardPage {

	public DownloadTransactionsPage() {
		super("Fetching Available Accounts");
		setTitle("Fetching available accounts");
	}

	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);

		// layout.horizontalAlignment = GridData.FILL;

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(gridData);

		setControl(label);

		((WizardDialog) getWizard().getContainer())
				.addPageChangedListener(new IPageChangedListener() {

					public void pageChanged(PageChangedEvent event) {
						if (event.getSelectedPage() == DownloadTransactionsPage.this) {
							downloadAccounts();
						}
					}
				});

		setPageComplete(false);
	}

	private void downloadAccounts() {

	}

	protected Text createTextField(Composite parent, GridData gridData,
			String text, boolean password) {
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

}
