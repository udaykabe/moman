package net.deuce.moman.rcp.account;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.deuce.moman.model.account.Account;
import net.sf.ofx4j.client.AccountStatement;
import net.sf.ofx4j.client.BankAccount;
import net.sf.ofx4j.client.FinancialInstitutionService;
import net.sf.ofx4j.client.context.DefaultApplicationContext;
import net.sf.ofx4j.client.context.OFXApplicationContextHolder;
import net.sf.ofx4j.client.impl.BaseFinancialInstitutionData;
import net.sf.ofx4j.client.impl.FinancialInstitutionServiceImpl;
import net.sf.ofx4j.domain.data.banking.AccountType;
import net.sf.ofx4j.domain.data.banking.BankAccountDetails;
import net.sf.ofx4j.domain.data.common.Transaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class BankDownloadingPage extends WizardPage {

	private Account account;

	protected BankDownloadingPage(Account account) {
		super("Bank Downloading");
		setTitle("Downloading Bank Transactions");
		this.account = account;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Contacting "
				+ account.getFinancialInstitution().getName());

		ProgressBar progressBar = new ProgressBar(container, SWT.INDETERMINATE);

		GridData fd = new GridData(GridData.FILL_HORIZONTAL);
		progressBar.setLayoutData(fd);
		
	    try {
			new URL(account.getFinancialInstitution().getUrl());
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask("fetchTransactions", 1);
						fetchTransactions();
						monitor.done();
					} catch (Exception e) {
						monitor.setCanceled(true);
						throw new InvocationTargetException(e);
					}
				}

		    });
		} catch (MalformedURLException e) {
			setErrorMessage("Invalid financial institution URL: " + account.getFinancialInstitution().getUrl());
		} catch (Throwable t) {
			progressBar.setState(SWT.ERROR);
			t.printStackTrace();
			setErrorMessage("Failed communicating with bank: " + t.getCause().getMessage());
		}

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public void fetchTransactions() throws Exception {
		SimpleDateFormat datef = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");

		Date startDate = null;
		if (account.getLastDownloadDate() != null) {
			startDate = account.getLastDownloadDate();
		} else {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, -1);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.set(Calendar.HOUR_OF_DAY, 17);
			startDate = c.getTime();
		}
		Date endDate = new Date();

		OFXApplicationContextHolder.setCurrentContext(new DefaultApplicationContext("QWIN", "1700"));

		BaseFinancialInstitutionData data = new BaseFinancialInstitutionData(account.getFinancialInstitution().getFinancialInstitutionId());
		data.setOFXURL(new URL(account.getFinancialInstitution().getUrl()));
		data.setOrganization(account.getFinancialInstitution().getOrganization());
		data.setFinancialInstitutionId(account.getFinancialInstitution().getFinancialInstitutionId());
		FinancialInstitutionService service = new FinancialInstitutionServiceImpl();
		net.sf.ofx4j.client.FinancialInstitution fi = service.getFinancialInstitution(data);

		// read the fi profile (note: not all institutions
		// support this, and you normally don't need it.)
		// FinancialInstitutionProfile profile = fi.readProfile();

		// get a reference to a specific bank account at your FI
		BankAccountDetails bankAccountDetails = new BankAccountDetails();

		// routing number to the bank.
		bankAccountDetails.setRoutingNumber(account.getBankId());
		// bank account number.
		bankAccountDetails.setAccountNumber(account.getAccountId());
		// it's a checking account
		bankAccountDetails.setAccountType(AccountType.CHECKING);

		BankAccount bankAccount = fi.loadBankAccount(bankAccountDetails, account.getUsername(), account.getPassword());

		AccountStatement statement = null;
		statement = bankAccount.readStatement(startDate, endDate);
		account.setLastDownloadDate(endDate);

		List<Transaction> bankTransactions = statement.getTransactionList().getTransactions();

		if (bankTransactions != null) {
			for (Transaction t : bankTransactions) {
				System.out.println("Bank transaction : " + t.getId()
						+ " - " + t.getDatePosted() + " - "
						+ t.getDateInitiated() + " - "
						+ t.getDateAvailable() + " - " + t.getName());
			}
		}
		setPageComplete(true);
	}

}
