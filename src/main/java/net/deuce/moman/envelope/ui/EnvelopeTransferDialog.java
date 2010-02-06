package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.Activator;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EnvelopeTransferDialog extends TitleAreaDialog {
	
	private static final Image ARROW = Activator.getImageDescriptor(
		"icons/RightArrowIcon.tiff").createImage();
	
	private Combo sourceAccountCombo;
	private Combo targetAccountCombo;
	private Text sourceEnvelopeText;
	private Text targetEnvelopeText;
	private Text amountText;
	private Account sourceAccount;
	private Account targetAccount;
	private Envelope sourceEnvelope;
	private Envelope targetEnvelope;
	private AccountService accountService;
	private EnvelopeService envelopeService;

	public EnvelopeTransferDialog(Shell shell, Account sourceAccount,
			Account targetAccount, Envelope sourceEnvelope, Envelope targetEnvelope) {
		super(shell);
		
		accountService = ServiceNeeder.instance().getAccountService();
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		this.sourceAccount = sourceAccount;
		this.targetAccount = targetAccount;
		this.sourceEnvelope = sourceEnvelope;
		this.targetEnvelope = targetEnvelope;
	}
	
	public Account getSourceAccount() {
		return sourceAccount;
	}

	public Account getTargetAccount() {
		return targetAccount;
	}

	public Envelope getSourceEnvelope() {
		return sourceEnvelope;
	}

	public Envelope getTargetEnvelope() {
		return targetEnvelope;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle("Envelope Transfer");
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
	
	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
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
		gridData.horizontalSpan = 4;
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

	// We allow the user to resize this dialog
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	private Combo buildCombo(Composite parent, GridData gridData, Account account, List<Account> accounts) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);
		combo.setFont(Constants.STANDARD_FONT);
//		combo.setSize(100, 20);
		
		int index = 0;
		int i = 0;
		for (Account item : accounts) {
			combo.add(item.getNickname());
			if (item == account) {
				index = i;
			}
			i++;
		}
		
		combo.select(index);
		
		return combo;
	}

	protected void createTextFields(final Composite parent, GridData gridData) {
		
		final List<Account> accounts = accountService.getOrderedEntities(false);
		
		if (sourceAccount == null && accounts.size() > 0) {
			sourceAccount = accounts.get(0);
		}
		
		if (targetAccount == null && accounts.size() > 0) {
			targetAccount = accounts.get(0);
		}
		
		if (sourceEnvelope == null) {
			sourceEnvelope = envelopeService.getAvailableEnvelope();
		}
		
		if (targetEnvelope == null) {
			targetEnvelope = envelopeService.getAvailableEnvelope();
		}
		
		sourceAccountCombo = buildCombo(parent, gridData, sourceAccount, accounts);
		sourceAccountCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sourceAccount = accounts.get(sourceAccountCombo.getSelectionIndex());
			}
		});
		
		
		Label arrowLabel = new Label(parent, SWT.NONE);
		arrowLabel.setImage(ARROW);
		arrowLabel.setSize(32, 32);
		//arrowLabel.setLayoutData(gridData);
		
		targetAccountCombo = buildCombo(parent, gridData, targetAccount, accounts);
		targetAccountCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetAccount = accounts.get(targetAccountCombo.getSelectionIndex());
			}
		});
		
		sourceEnvelopeText = new Text(parent, SWT.BORDER);
		sourceEnvelopeText.setLayoutData(gridData);
		sourceEnvelopeText.setEditable(false);
		sourceEnvelopeText.setText(sourceEnvelope.getName());
//		sourceEnvelopeText.setSize(100, 20);
		sourceEnvelopeText.setFont(Constants.STANDARD_FONT);
		sourceEnvelopeText.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {

				EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(parent.getDisplay().getActiveShell(), sourceEnvelope);
				
				dialog.setAllowBills(true);
				dialog.create();
				dialog.open();
				if (sourceEnvelope != dialog.getEnvelope()) {
					sourceEnvelope = dialog.getEnvelope();
					sourceEnvelopeText.setText(sourceEnvelope.getName());
				}
			}
		});
		
		arrowLabel = new Label(parent, SWT.NONE);
		arrowLabel.setImage(ARROW);
		arrowLabel.setSize(32, 32);
		//arrowLabel.setLayoutData(gridData);
		
		targetEnvelopeText = new Text(parent, SWT.BORDER);
		targetEnvelopeText.setLayoutData(gridData);
		targetEnvelopeText.setEditable(false);
		targetEnvelopeText.setText(targetEnvelope.getName());
//		targetEnvelopeText.setSize(100, 20);
		targetEnvelopeText.setFont(Constants.STANDARD_FONT);
		targetEnvelopeText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(parent.getDisplay().getActiveShell(), targetEnvelope);
				
				dialog.setAllowBills(true);
				dialog.create();
				dialog.open();
				if (targetEnvelope != dialog.getEnvelope()) {
					targetEnvelope = dialog.getEnvelope();
					targetEnvelopeText.setText(targetEnvelope.getName());
				}
			}
		});
		
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(gridData);
		label.setFont(Constants.STANDARD_FONT);
		label.setText("Amount");
		
		amountText = new Text(parent, SWT.BORDER);
		amountText.setLayoutData(gridData);
		amountText.setFont(Constants.STANDARD_FONT);
		
		// spacer
//		label = new Label(parent, SWT.NONE);
//		label.setLayoutData(gridData);
		
		if (targetEnvelope.getBalance() < 0 && sourceEnvelope.getBalance() >= Math.abs(targetEnvelope.getBalance())) {
			amountText.setText(Constants.CURRENCY_VALIDATOR.format(Math.abs(targetEnvelope.getBalance())));
		} else {
			amountText.setText(Constants.CURRENCY_VALIDATOR.format(0.0));
		}
		
	}

	protected boolean isValidInput() {
		if (amountText.getText().length() > 0 &&
				!Constants.CURRENCY_VALIDATOR.isValid(amountText.getText())) {
			setErrorMessage("Invalid amount value");
			return false;
		}
		double amount = Constants.CURRENCY_VALIDATOR.validate(amountText.getText()).doubleValue();
		if (amount < 0) {
			setErrorMessage("Please enter positive values only");
			return false;
		}

		return true;
	}

	protected void saveInput() {
		double amount = Constants.CURRENCY_VALIDATOR.validate(amountText.getText()).doubleValue();
		if (amount > 0 && sourceEnvelope != targetEnvelope) {
			envelopeService.transfer(sourceAccount, targetAccount,
					sourceEnvelope, targetEnvelope, amount);
		}
	}

}
