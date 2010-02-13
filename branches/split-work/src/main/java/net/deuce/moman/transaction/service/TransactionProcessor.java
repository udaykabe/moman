package net.deuce.moman.transaction.service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.preference.AccountPage;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.TransactionFactory;
import net.deuce.moman.ui.Activator;
import net.deuce.moman.util.CalendarUtil;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class TransactionProcessor implements Runnable {
	
	private Exception exception = null;
	private TransactionFetchResult result;
	private Account account;
	private Shell shell;
	private IProgressMonitor progressMonitor;
	private boolean force = false;
	private Set<Envelope> modifiedEnvelopes = new HashSet<Envelope>();
	private File f =  new File("/Users/nbolton/src/personal/moman/importedTransactions.xml");
	private List<InternalTransaction> processedTransactions = null;
	private String focusedView = null;
	private EnvelopeService envelopeService;
	private EnvelopeFactory envelopeFactory;
	private TransactionService transactionService;
	private TransactionFactory transactionFactory;

	public TransactionProcessor(Shell shell, Account account, boolean force, String focusedView) {
		this.shell = shell;
		this.account = account;
		this.force = force;
		this.focusedView = focusedView;
		this.envelopeService = ServiceNeeder.instance().getEnvelopeService();
		this.envelopeFactory = ServiceNeeder.instance().getEnvelopeFactory();
		this.transactionService = ServiceNeeder.instance().getTransactionService();
		this.transactionFactory = ServiceNeeder.instance().getTransactionFactory();
	}
	
	protected abstract TransactionFetchResult fetchTransactions(Account account)
	throws Exception;
	
	private void clear() {
		exception = null;
		result = null;
		modifiedEnvelopes.clear();
	}
	
	public Account getAccount() {
		return account;
	}
	
	protected Date getLastDownloadedDate() {
		Date date;
		if (!force && account.getLastDownloadDate() != null) {
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
	
	public boolean execute() {
		clear();
		ServiceNeeder.instance().getImportService().clearEntities();
		
		try {
			return doImport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean doImport() {
		
		//bankTransactions = Registry.instance().loadImportedTransactions(f);
		
		if (result == null) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			try {
				dialog.run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) {
						monitor.beginTask("Downloading transactions from " + account.getFinancialInstitution().getName(), 100);
						monitor.worked(1);
						try {
							progressMonitor = monitor;
							Thread progressWorker = new Thread(TransactionProcessor.this);
							progressWorker.start();
							result = fetchTransactions(account);
							monitor.done();
						} catch (Exception e) {
							e.printStackTrace();
							exception = e;
							monitor.done();
						} finally {
							progressMonitor = null;
						}
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			
			ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();

			try {
				if (result != null) {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
					try {
						dialog.run(true, true, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) {
								try {
									processedTransactions = processTransactions(account, result, monitor);
									monitor.done();
								} catch (Exception e) {
									e.printStackTrace();
									exception = e;
									monitor.done();
								}
							}
						});
						
						account.setLastDownloadDate(result.getLastDownloadedDate());
						account.setBalance(result.getStatementBalance());
						if (processedTransactions != null && processedTransactions.size() > 0) {
							InternalTransaction firstTransaction = processedTransactions.get(processedTransactions.size()-1);
							if (firstTransaction.isMatched()) {
								firstTransaction = firstTransaction.getMatchedTransaction();
							}
							ServiceNeeder.instance().getTransactionService().adjustBalances(firstTransaction, false);
						}
			
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				for (Envelope env : modifiedEnvelopes) {
					env.clearBalance();
				}
				ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
			}
			
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(focusedView,null,IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		clear();
		return exception == null;
	}
	
	private List<InternalTransaction> processTransactions(Account account,
			TransactionFetchResult result,
			IProgressMonitor monitor) {
		
		List<Transaction> bankTransactions = result.getBankTransactions();
		Double statementBalance = result.getStatementBalance();
		
		int taskCount = 5;
		if (account.getLastDownloadDate() == null) {
			taskCount++;
		}
		
		int importCount = 0;
		if (bankTransactions != null) {
			importCount = bankTransactions.size();
		}
		monitor.beginTask("Processing transactions from " +
				account.getFinancialInstitution().getName(), importCount*taskCount);
		
		List<InternalTransaction> transactions = new LinkedList<InternalTransaction>();
		
		if (bankTransactions != null) {
			InternalTransaction maxTrans = null;
			for (Transaction bt : bankTransactions) {
				InternalTransaction t = ServiceNeeder.instance().getTransactionFactory().newEntity(
						bt.getId(), bt.getAmount(), null, bt.getDatePosted(),
						bt.getName(), bt.getMemo(), bt.getCheckNumber(),
						bt.getReferenceNumber(), null, account);
				
				if (t.getAmount() > 0) {
					t.clearSplit();
					t.addSplit(envelopeService.getAvailableEnvelope());
				} else {
					t.addSplit(envelopeService.getUnassignedEnvelope());
				}
				
				if (t.getType() == null) {
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
				}
				
				transactions.add(t);
				
				if (maxTrans == null || maxTrans.compareTo(t) < 0) {
					maxTrans = t;
				}
				
				monitor.worked(1);
			}
			
			if (maxTrans != null) {
				maxTrans.setBalance(statementBalance);
			}
			
			if (transactions.size() > 0) {
				Collections.sort(transactions, transactions.get(0).getReverseComparator());
			
				initialDownloadCheck(transactions, monitor);
				matchPreviouslyDownloadedTransactions(transactions, monitor);
				findMatchedTransactions(transactions, monitor);
				applyRules(transactions, monitor);
				addUnmatchedTransactions(transactions, monitor);
			}
		}
		ServiceNeeder.instance().getImportService().setEntities(transactions);
		ServiceNeeder.instance().getServiceContainer().saveImportTransactions(f);
		
		return transactions;
	}
	
	private void applyRules(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		for (InternalTransaction t : transactions) {
			for (Rule rule : ServiceNeeder.instance().getTransactionRuleService().getEntities()) {
				if (rule.getExpression().contains("LOANSERVICING AUTOMATIC") && t.getDescription().contains("LOANSERVICING AUTOMATIC")) {
					System.out.println("");
				}
				if (rule.isEnabled() && rule.evaluate(t.getDescription()) &&
						(rule.getAmount() == null || rule.amountEquals(t.getAmount()))) {
					if (rule.getConversion() != null && rule.getConversion().length() > 0) {
						t.setDescription(rule.getConversion());
					}
					t.clearSplit();
					t.addSplit(rule.getEnvelope(), !t.isMatched());
				}
			}
			modifiedEnvelopes.addAll(t.getSplit());
		}
	}
	
	private void initialDownloadCheck(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		if (account.getInitialBalance() == null) {
			
			Double balance = null;
			Date initialBalanceDate = null;
			for (InternalTransaction it : transactions) {
				if (balance == null && it.getBalance() != null) {
					balance = it.getBalance();
				} else {
					it.setBalance(balance);
				}
				balance -= it.getAmount();
				monitor.worked(1);
				if (initialBalanceDate == null || it.getDate().before(initialBalanceDate)) {
					initialBalanceDate = it.getDate();
				}
			}
			if (balance != null) {
				account.setInitialBalance(balance);
				initialBalanceDate = new Date(initialBalanceDate.getTime()-1);
				InternalTransaction transaction = transactionService.getInitialBalanceTransaction(account);
				if (transaction == null) {
					transaction = transactionFactory.newEntity(
						null, balance, "OTHER", initialBalanceDate, "Initial Balance",
						null, null, null, null, account);
					transaction.setInitialBalance(true);
					transaction.addSplit(envelopeService.getAvailableEnvelope());
					transactionService.addEntity(transaction);
				} else {
					transaction.setAmount(balance);
					transaction.setDate(initialBalanceDate);
				}
				
				// transfer to as many negative envelopes as possible
				distributeToNegativeEnvelopes(envelopeFactory.createTopLevelEnvelope(), envelopeService.getAvailableEnvelope().getBalance());
			}
		}
	}
	
	private double distributeToNegativeEnvelopes(Envelope env, double balance) {
		if (!env.isAvailable()) {
			if (balance > 0) {
				if (!env.hasChildren()) {
					if (env.getBalance() < 0) {
						double transferAmount = 0;
						if (balance > -env.getBalance()) {
							transferAmount = -env.getBalance();
						} else {
							transferAmount = balance;
						}
						envelopeService.transfer(account, account,
								envelopeService.getAvailableEnvelope(), env, transferAmount);
						return balance-transferAmount;
					}
				} else {
					for (Envelope child : env.getChildren()) {
						balance = distributeToNegativeEnvelopes(child, balance);
					}
				}
			}
		}
		return balance;
	}
	
	private void addUnmatchedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		for (InternalTransaction t : transactions) {
			if (!t.isMatched()) {
				ServiceNeeder.instance().getTransactionService().addEntity(t, false);
			}
			monitor.worked(1);
		}
	}
	
	private void matchPreviouslyDownloadedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		for (InternalTransaction t : transactions) {
			InternalTransaction existingTransaction =
				ServiceNeeder.instance().getTransactionService().findTransactionByExternalId(t.getExternalId());
			System.out.println("ZZZ checking downlaoded " + t);
			System.out.println("ZZZ existing " + existingTransaction);
			if (existingTransaction != null && t.getAmount().doubleValue() == existingTransaction.getAmount().doubleValue()) {
				System.out.println("ZZZ matched " + t);
				t.setMatchedTransaction(existingTransaction);
			}
			monitor.worked(1);
		}
	}

	private void findMatchedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		int threshold = Activator.getDefault().getPreferenceStore().getInt(
				AccountPage.ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD);
		
		List<InternalTransaction> register = transactionService.getAccountTransactions(account, true);
		for (InternalTransaction importedTransaction : transactions) {
			
			System.out.println("ZZZ checking import " + importedTransaction);
			
			if (!importedTransaction.isMatched() && !importedTransaction.isExternal()) {
				
				Calendar lowerBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
				lowerBound.add(Calendar.DATE, -threshold);
				Calendar upperBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
				upperBound.add(Calendar.DATE, threshold);
				
				System.out.println("ZZZ lowerBound " + Constants.SHORT_DATE_FORMAT.format(lowerBound.getTime()));
				System.out.println("ZZZ upperBound " + Constants.SHORT_DATE_FORMAT.format(upperBound.getTime()));
				for (InternalTransaction t : register) {
				System.out.println("ZZZ against " + t);
					if (t.getDate().before(lowerBound.getTime()) || t.getDate().after(upperBound.getTime())) {
					System.out.println("ZZZ out of range - imported: " + Constants.SHORT_DATE_FORMAT.format(importedTransaction.getDate())
							+ " existing: " + Constants.SHORT_DATE_FORMAT.format(t.getDate()));
						break;
					}
					
					if (t.getAmount().doubleValue() == importedTransaction.getAmount().doubleValue() && !t.isEnvelopeTransfer()) {
						importedTransaction.setMatchedTransaction(t);
						t.setExternalId(importedTransaction.getExternalId());
						transactionService.addExternalTransactionReference(t);
					}
				}
			}
			monitor.worked(1);
		}
	}
	
	@Override
	public void run() {
		try {
			while (progressMonitor != null) {
				progressMonitor.worked(1);
					Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
		}
	}
}
