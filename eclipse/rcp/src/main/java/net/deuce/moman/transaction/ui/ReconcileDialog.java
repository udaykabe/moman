package net.deuce.moman.transaction.ui;

import java.util.Date;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.account.ui.AccountEntityLabelProvider;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.TransactionStatus;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.ui.DateSelectionDialog;
import net.deuce.moman.util.Utils;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.EntityCombo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;

public class ReconcileDialog extends TitleAreaDialog implements
		EntityListener<InternalTransaction> {

	private EntityCombo<Account> accountCombo;
	private ReconcileComposite register;
	private Date endingDate;
	private Text endingDateText;
	private Double endingBalance;
	private Text endingBalanceText;
    private Text differenceText;
	private Button balanceButton;

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	public ReconcileDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Reconcile Account");
		transactionService.addEntityListener(this);
	}

	protected Control createContents(Composite parent) {
		// create the top level composite for the dialog
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createForm(composite);
		createRegister(composite);

		register.setAccount(accountCombo.getEntity());
		return composite;
	}

	private Composite createForm(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		Label label = new Label(composite, SWT.NONE);
		label.setText("Account");
		accountCombo = new EntityCombo<Account>(composite, accountService,
				new AccountEntityLabelProvider());
		accountCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				register.setAccount(accountCombo.getEntity());
				updateDifference();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(composite, SWT.NONE);

		endingDate = new Date();
		label = new Label(composite, SWT.NONE);
		label.setText("Ending Date");
		endingDateText = new Text(composite, SWT.NONE);
		endingDateText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		endingDateText.setText(RcpConstants.SHORT_DATE_FORMAT
				.format(endingDate));
		endingDateText.addMouseListener(new MouseAdapter() {

			public void mouseDoubleClick(MouseEvent e) {
				DateSelectionDialog dialog = new DateSelectionDialog(
						ReconcileDialog.this.getShell(), endingDate);
				dialog.open();
				Date date = dialog.getDate();
				if (date != null) {
					endingDate = date;
					endingDateText.setText(RcpConstants.SHORT_DATE_FORMAT
							.format(endingDate));

					List<String> ids = serviceManager
							.startQueuingNotifications();

					try {
						for (InternalTransaction it : transactionService
								.getUnreconciledTransactions(accountCombo
										.getEntity(), false)) {
							if (it.getDate().after(endingDate)) {
								it.setStatus(TransactionStatus.open);
							} else {
								it.setStatus(TransactionStatus.cleared);
							}
						}
					} finally {
						serviceManager.stopQueuingNotifications(ids);
					}
				}
			}
		});
		new Label(composite, SWT.NONE);

		endingBalance = accountCombo.getEntity().getBalance();
		label = new Label(composite, SWT.NONE);
		label.setText("Ending Balance");
		endingBalanceText = new Text(composite, SWT.NONE);
		endingBalanceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		endingBalanceText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(endingBalance));
		endingBalanceText.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				StringBuffer newValue = new StringBuffer(endingBalanceText
						.getText());
				newValue.replace(e.start, e.end, "");
				newValue.insert(e.start, e.text);
				e.doit = Utils.validateCurrency(newValue.toString());
			}
		});
		endingBalanceText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				endingBalance = Utils
						.parseCurrency(endingBalanceText.getText());
				updateDifference();
			}
		});
		new Label(composite, SWT.NONE);

		label = new Label(composite, SWT.NONE);
		label.setText("Difference");
		differenceText = new Text(composite, SWT.NONE);
		differenceText.setEnabled(false);
		differenceText.setEditable(false);
		differenceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		balanceButton = new Button(composite, SWT.PUSH);
		balanceButton.setText("Balance");
		balanceButton.setEnabled(false);
		balanceButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				reconcile();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		updateDifference();

		return composite;
	}

	private void reconcile() {
		List<String> ids = serviceManager.startQueuingNotifications();
		try {
			for (InternalTransaction it : transactionService
					.getUnreconciledTransactions(accountCombo.getEntity(),
							false)) {
				if (it.getStatus() == TransactionStatus.cleared) {
					it.setStatus(TransactionStatus.reconciled);
				}
				accountCombo.getEntity().setLastReconciledDate(endingDate);
				accountCombo.getEntity().setLastReconciledEndingBalance(
						endingBalance);
			}
		} finally {
			serviceManager.stopQueuingNotifications(ids);
		}
	}

	private void updateDifference() {
        Double difference = endingBalance
                - accountCombo.getEntity().getLastReconciledEndingBalance();
		for (InternalTransaction it : transactionService
				.getUnreconciledTransactions(accountCombo.getEntity(), false)) {
			if (it.getStatus() == TransactionStatus.cleared) {
				difference -= it.getAmount();
			}
		}
		differenceText.setText(RcpConstants.CURRENCY_VALIDATOR
				.format(difference));

		balanceButton.setEnabled(Math.abs(Utils.round(difference)) < 0.1);
	}

	private void createRegister(Composite parent) {
		register = new ReconcileComposite(parent, SWT.NONE);
		register.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void entityAdded(EntityEvent<InternalTransaction> event) {
	}

	public void entityChanged(EntityEvent<InternalTransaction> event) {
		updateDifference();
	}

	public void entityRemoved(EntityEvent<InternalTransaction> event) {
	}

}
