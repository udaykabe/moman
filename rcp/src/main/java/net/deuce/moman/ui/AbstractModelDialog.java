package net.deuce.moman.ui;

import java.lang.reflect.ParameterizedType;

import net.deuce.moman.model.AbstractEntity;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("unchecked")
public abstract class AbstractModelDialog<E extends AbstractEntity> extends TitleAreaDialog {
	
	public AbstractModelDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle(((Class<E>)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName() + " Dialog");
		// Set the message
//		setMessage("This is a TitleAreaDialog", IMessageProvider.INFORMATION);

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
	
	protected void createTextFields(Composite parent, GridData gridData) {
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);

		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		createTextFields(parent, gridData);
	
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "OK", true);
		// Add a SelectionListener

		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}

	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	protected abstract boolean isValidInput();

	// We allow the user to resize this dialog
	@Override
	protected boolean isResizable() {
		return true;
	}

	protected abstract void saveInput();

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

}
