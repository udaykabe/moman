package net.deuce.moman.command.account;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.account.Account;
import net.deuce.moman.model.rules.Rule;
import net.deuce.moman.rcp.transaction.TransactionImportView;
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
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class Import extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.command.account.import";

	private Exception exception = null;
	private Date lastDownloadedDate;
	private List<Transaction> bankTransactions;
	private double statementBalance;
	private Account account;

	private void clear() {
		exception = null;
		lastDownloadedDate = null;
		bankTransactions = null;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	protected Date getLastDownloadedDate() {
		Date date;
		if (account.getLastDownloadDate() != null) {
			date = account.getLastDownloadDate();
		} else {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, -1);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.set(Calendar.HOUR_OF_DAY, 17);
			date = c.getTime();
		}
		return date;
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		File f =  new File("/Users/nbolton/src/personal/moman-rcp/importedTransactions.xml");

		clear();
		
		account = getAccount(window);
		
		if (account == null) return null;
		
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(window.getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Downloading transactions from " + account.getFinancialInstitution().getName(), 2);
					monitor.worked(1);
					try {
						fetchTransactions(account, monitor);
						monitor.done();
					} catch (Exception e) {
						e.printStackTrace();
						exception = e;
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (exception != null) {
			String msg;
			if (exception.getCause() != null) {
				msg = exception.getCause().getMessage();
			} else {
				msg = exception.getMessage();
			}
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Error communicating with the bank: " + msg);
		} else {
			account.setLastDownloadDate(lastDownloadedDate);
			
			if (bankTransactions != null) {
				List<net.deuce.moman.model.transaction.InternalTransaction> transactions = new LinkedList<net.deuce.moman.model.transaction.InternalTransaction>();
				net.deuce.moman.model.transaction.InternalTransaction maxTrans = null;
				for (Transaction bt : bankTransactions) {
					net.deuce.moman.model.transaction.InternalTransaction t = new net.deuce.moman.model.transaction.InternalTransaction();
					t.setAccount(account);
					t.setAmount(bt.getAmount());
					t.setCheck(bt.getCheckNumber());
					t.setDate(bt.getDatePosted());
					t.setDescription(bt.getName());
					t.setExternalId(bt.getId());
					t.setMemo(bt.getMemo());
					t.setRef(bt.getReferenceNumber());
					
					t.addSplit(Registry.instance().getAvailableEnvelope());
					
					if (t.getExternalId().equals("200909220")) {
						System.out.println();
					}
					
					for (Rule rule : Registry.instance().getTransactionRules()) {
						if (rule.evaluate(t.getDescription())) {
							if (rule.getConversion() != null && rule.getConversion().length() > 0) {
								t.setDescription(rule.getConversion());
							}
							t.clearSplit();
							t.addSplit(rule.getEnvelope());
						}
					}
					
					if (bt.getTransactionType() == TransactionType.OTHER) {
						if (t.getCheck() != null && t.getCheck().length() > 0) {
							t.setType(TransactionType.CHECK.name());
						} else if (t.getAmount() >= 0) {
							t.setType(TransactionType.CREDIT.name());
						} else {
							t.setType(TransactionType.DEBIT.name());
						}
					} else {
						t.setType(bt.getTransactionType().name());
					}
					
					transactions.add(t);
					
					if (maxTrans == null || maxTrans.getExternalId().compareTo(t.getExternalId()) < 0) {
						maxTrans = t;
					}
				}
				
				if (maxTrans != null) {
					maxTrans.setBalance(statementBalance);
				}
				
				Registry.instance().setImportedTransactions(transactions);
				Registry.instance().saveImportTransactions(f);
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(TransactionImportView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
				} catch (PartInitException e) {
					e.printStackTrace();
				}

			}
		}
		clear();
		return null;
	}

	public void fetchTransactions(Account account, IProgressMonitor monitor) throws Exception {
		SimpleDateFormat datef = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");

		Date startDate = getLastDownloadedDate();
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
		statementBalance = statement.getLedgerBalance().getAmount();
		lastDownloadedDate = endDate;

		bankTransactions = statement.getTransactionList().getTransactions();

		if (bankTransactions != null) {
			for (Transaction t : bankTransactions) {
				System.out.println("Bank transaction : " + t.getId()
						+ " - " + t.getDatePosted() + " - "
						+ t.getDateInitiated() + " - "
						+ t.getDateAvailable() + " - " + t.getName());
			}
		}
	}

}
