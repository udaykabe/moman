package net.deuce.moman.entity.service.transaction;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.model.transaction.TransactionFactory;
import net.deuce.moman.entity.model.transaction.TransactionStatus;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.preference.PreferenceService;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractTransactionProcessor implements TransactionProcessor {
	
	private Exception exception = null;
	private TransactionFetchResult result;
	private Account account;
	private IProgressMonitor progressMonitor;
	private boolean force = false;
	private Set<Envelope> modifiedEnvelopes = new HashSet<Envelope>();
//	private File f =  new File("/Users/nbolton/src/personal/moman/importedTransactions.xml");
	private List<InternalTransaction> processedTransactions = null;
	
	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();
	
	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();
	
	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();
	
	private ImportService importService = ServiceProvider.instance().getImportService();
	
	private TransactionFactory transactionFactory = ServiceProvider.instance().getTransactionFactory();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private TransactionRuleService transactionRuleService = ServiceProvider.instance().getTransactionRuleService();

	private PreferenceService preferenceService = ServiceProvider.instance().getPreferenceService();

	public AbstractTransactionProcessor(Account account, boolean force) {
		this.account = account;
		this.force = force;
	}
	
	protected abstract TransactionFetchResult fetchTransactions()
	throws Exception;
	
	
	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public TransactionFetchResult getResult() {
		return result;
	}

	public void setResult(TransactionFetchResult result) {
		this.result = result;
	}

	public List<InternalTransaction> getProcessedTransactions() {
		return processedTransactions;
	}

	public void setProcessedTransactions(
			List<InternalTransaction> processedTransactions) {
		this.processedTransactions = processedTransactions;
	}

	private void clear() {
		exception = null;
		result = null;
		modifiedEnvelopes.clear();
	}
	
	public Account getAccount() {
		return account;
	}
	
	public Date getLastDownloadedDate() {
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
		importService.clearEntities();
		
		try {
			return doImport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected abstract void doFetchTransactions();
	protected abstract void doHandleException(Exception e);
	protected abstract void doProcessTransactions();
	protected abstract void finishUp();
	
	private boolean doImport() {
		
		//bankTransactions = Registry.instance().loadImportedTransactions(f);
		
		if (result == null) {
			doFetchTransactions();
		}

		if (exception != null) {
			doHandleException(exception);
		} else {
			
			List<String> ids = serviceManager.startQueuingNotifications();

			try {
				if (result != null) {
					doProcessTransactions();
				}
			} finally {
				for (Envelope env : modifiedEnvelopes) {
					env.resetBalance();
				}
				serviceManager.stopQueuingNotifications(ids);
			}
			
			finishUp();
		}
		clear();
		return exception == null;
	}
	
	protected List<InternalTransaction> processTransactions(Account account,
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
				InternalTransaction t = transactionFactory.newEntity(
						bt.getId(), bt.getAmount(), null, bt.getDatePosted(),
						bt.getName(), bt.getMemo(), bt.getCheckNumber(),
						bt.getReferenceNumber(), null, TransactionStatus.cleared, account);
				
				t.setImported(true);
				
				if (t.getAmount() > 0) {
					t.clearSplit();
					t.addSplit(envelopeService.getAvailableEnvelope(), t.getAmount());
				} else {
					t.addSplit(envelopeService.getUnassignedEnvelope(), t.getAmount());
				}
				
				if (t.getType() == null) {
					if (bt.getTransactionType() == TransactionType.OTHER) {
						if (t.getCheck() != null && t.getCheck().length() > 0) {
							t.setType(TransactionType.CHECK);
						} else if (t.getAmount() >= 0) {
							t.setType(TransactionType.CREDIT);
						} else {
							t.setType(TransactionType.DEBIT);
						}
					} else {
						t.setType(bt.getTransactionType());
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
				
				// transfer to as many negative envelopes as possible
				envelopeService.distributeToNegativeEnvelopes(account, envelopeFactory.createTopLevelEnvelope(), envelopeService.getAvailableEnvelope().getBalance());
			}
		}
		importService.setEntities(transactions);
//		serviceManager.saveImportTransactions(f);
		
		return transactions;
	}
	
	private void applyRules(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		for (InternalTransaction t : transactions) {
			if (t.getSplit().size() == 1 && t.getSplit().get(0).getEnvelope() == envelopeService.getUnassignedEnvelope()) {
				for (Rule rule : transactionRuleService.getEntities()) {
					if (rule.isEnabled() && rule.evaluate(t.getDescription()) &&
							(rule.getAmount() == null || rule.amountEquals(t.getAmount()))) {
						if (rule.getConversion() != null && rule.getConversion().length() > 0) {
							t.setDescription(rule.getConversion());
						}
						t.clearSplit();
						t.addSplit(rule.getEnvelope(), t.getAmount());
					}
				}
				for (Split item : t.getSplit()) {
					modifiedEnvelopes.add(item.getEnvelope());
				}
			}
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
						null, balance, TransactionType.OTHER, initialBalanceDate, "Initial Balance",
						null, null, null, null, TransactionStatus.reconciled, account);
					transaction.setInitialBalance(true);
					transaction.addSplit(envelopeService.getAvailableEnvelope(), transaction.getAmount());
					transactionService.addEntity(transaction);
				} else {
					transaction.setAmount(balance, null);
					transaction.setDate(initialBalanceDate);
				}
			}
		}
	}
	
	private void addUnmatchedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		for (InternalTransaction t : transactions) {
			if (!t.isMatched()) {
				transactionService.addEntity(t, false);
			}
			monitor.worked(1);
		}
	}
	
	private void matchPreviouslyDownloadedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		for (InternalTransaction t : transactions) {
			InternalTransaction existingTransaction =
				transactionService.findTransactionByExternalId(t.getExternalId());
			System.out.println("ZZZ checking downlaoded " + t);
			System.out.println("ZZZ existing " + existingTransaction);
			if (existingTransaction != null && t.getAmount().doubleValue() == existingTransaction.getAmount().doubleValue()) {
				System.out.println("ZZZ matched " + t);
				t.setMatchedTransaction(existingTransaction);
				t.setSplit(existingTransaction.getSplit());
			}
			monitor.worked(1);
		}
	}

	private void findMatchedTransactions(List<InternalTransaction> transactions, IProgressMonitor monitor) {
		
		int threshold = preferenceService.getInt("ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD");
		
		List<InternalTransaction> register = transactionService.getAccountTransactions(account, true);
		for (InternalTransaction importedTransaction : transactions) {
			
			System.out.println("ZZZ checking import " + importedTransaction);
			
			if (!importedTransaction.isMatched()) {
				
				Calendar lowerBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
				lowerBound.add(Calendar.DATE, -threshold);
				Calendar upperBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
				upperBound.add(Calendar.DATE, threshold);
				
				System.out.println("ZZZ lowerBound " + Constants.SHORT_DATE_FORMAT.format(lowerBound.getTime()));
				System.out.println("ZZZ upperBound " + Constants.SHORT_DATE_FORMAT.format(upperBound.getTime()));
				for (InternalTransaction t : register) {
					if (!t.isExternal()) {
						System.out.println("ZZZ against " + t);
						if (t.getDate().before(lowerBound.getTime()) || t.getDate().after(upperBound.getTime())) {
						System.out.println("ZZZ out of range - imported: " + Constants.SHORT_DATE_FORMAT.format(importedTransaction.getDate())
								+ " existing: " + Constants.SHORT_DATE_FORMAT.format(t.getDate()));
							break;
						}
						
						if (t.getAmount().doubleValue() == importedTransaction.getAmount().doubleValue() && !t.isEnvelopeTransfer()) {
							importedTransaction.setMatchedTransaction(t);
							importedTransaction.setSplit(t.getSplit(), false);
							t.setExternalId(importedTransaction.getExternalId());
							transactionService.addExternalTransactionReference(t);
							t.setStatus(TransactionStatus.cleared);
						}
					}
				}
			}
			monitor.worked(1);
		}
	}
	
}
