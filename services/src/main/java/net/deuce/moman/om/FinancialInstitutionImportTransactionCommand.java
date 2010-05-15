package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class FinancialInstitutionImportTransactionCommand extends AbstractCommand implements ImportTransactionCommand {

  private Account account;
  private int matchedDayThreshold = 7;
  private boolean force = false;
  private Set<Envelope> modifiedEnvelopes = new HashSet<Envelope>();
  //	private File f =  new File("/Users/nbolton/src/personal/moman/importedTransactions.xml");
  private List<InternalTransaction> processedTransactions = null;

  @Autowired
  private AccountService accountService;

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private FinancialInstitutionService financialInstitutionService;

  @Autowired
  private UserService userService;

//  @Autowired
//	private PreferenceService preferenceService;

  public FinancialInstitutionImportTransactionCommand() {
    super("TransactionProcessor", true);
  }

  public int getMatchedDayThreshold() {
    return matchedDayThreshold;
  }

  public void setMatchedDayThreshold(int matchedDayThreshold) {
    this.matchedDayThreshold = matchedDayThreshold;
  }

  public boolean isForce() {
    return force;
  }

  public void setForce(boolean force) {
    this.force = force;
  }

  public EnvelopeService getEnvelopeService() {
    return envelopeService;
  }

  public void setEnvelopeService(EnvelopeService envelopeService) {
    this.envelopeService = envelopeService;
  }

  public TransactionService getTransactionService() {
    return transactionService;
  }

  public void setTransactionService(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  public RuleService getRuleService() {
    return ruleService;
  }

  public void setRuleService(RuleService ruleService) {
    this.ruleService = ruleService;
  }

  public FinancialInstitutionService getFinancialInstitutionService() {
    return financialInstitutionService;
  }

  public void setFinancialInstitutionService(FinancialInstitutionService financialInstitutionService) {
    this.financialInstitutionService = financialInstitutionService;
  }

  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }

  public List<InternalTransaction> getProcessedTransactions() {
    return processedTransactions;
  }

  public void setProcessedTransactions(
      List<InternalTransaction> processedTransactions) {
    this.processedTransactions = processedTransactions;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
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


  protected TransactionFetchResult fetchTransactions() throws Exception {
    Date startDate = getLastDownloadedDate();
    Date endDate = new Date();
    return financialInstitutionService.fetchTransactions(getAccount(),
        startDate, endDate);
  }

  protected List<InternalTransaction> processTransactions(TransactionFetchResult result) {

    List<InternalTransaction> transactions = doProcessTransactions(account, result);

    account.setLastDownloadDate(result.getLastDownloadedDate());
    account.setOnlineBalance(result.getStatementBalance());
    accountService.saveOrUpdate(account);
    if (transactions != null && transactions.size() > 0) {
      InternalTransaction firstTransaction = transactions.get(transactions.size() - 1);
      if (firstTransaction.isMatched()) {
        firstTransaction = firstTransaction.getMatchedTransaction();
      }
      transactionService.adjustBalances(firstTransaction, false, null);
    }
    return transactions;
  }

  public void doExecute() throws Exception {

    //bankTransactions = Registry.instance().loadImportedTransactions(f);

    TransactionFetchResult result = fetchTransactions();

    if (result != null) {
      List<InternalTransaction> transactions = processTransactions(result);

      for (Envelope env : modifiedEnvelopes) {
        envelopeService.resetBalance(env);
      }

      List<Element> list = new LinkedList<Element>();
      Element element = DocumentHelper.createElement(transactionService.getRootElementName());
      list.add(element);

      for (InternalTransaction trans : transactions) {
        transactionService.toXml(trans, element);
      }

      element = DocumentHelper.createElement(envelopeService.getRootElementName());
      list.add(element);

      for (Envelope env : envelopeService.getEntities(userService.getDefaultUser())) {
        envelopeService.toXml(env, element);
      }

      setResult(list);
    }
  }

  protected List<InternalTransaction> doProcessTransactions(Account account,
                                                            TransactionFetchResult result) {

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

    List<InternalTransaction> transactions = new LinkedList<InternalTransaction>();

    transactionService.clearCustomTransactions(account.getUser());

    if (bankTransactions != null) {
      InternalTransaction maxTrans = null;
      for (Transaction bt : bankTransactions) {

        InternalTransaction t = new InternalTransaction();
        t.setUuid(transactionService.createUuid());
        t.setExternalId(bt.getId());
        t.setCheckNo(bt.getCheckNumber());
        t.setAmount(bt.getAmount());
        t.setDate(bt.getDatePosted());
        t.setDescription(bt.getName());
        t.setMemo(bt.getMemo());
        t.setRef(bt.getReferenceNumber());
        t.setStatus(TransactionStatus.cleared);
        t.setAccount(account);
        t.setImported(true);
        t.setCustom(true);
        t.setInitialBalance(false);

        if (t.getType() == null) {
          if (bt.getTransactionType() == TransactionType.OTHER) {
            t.determineAndSetType();
          } else {
            t.setType(bt.getTransactionType());
          }
        }

        transactionService.saveOrUpdate(t);

        transactions.add(t);

        if (maxTrans == null || maxTrans.compareTo(t) < 0) {
          maxTrans = t;
        }

      }

      if (maxTrans != null) {
        maxTrans.setBalance(statementBalance);
      }

      if (transactions.size() > 0) {
        Collections.sort(transactions, transactions.get(0).getReverseComparator());

        initialDownloadCheck(transactions);
        matchPreviouslyDownloadedTransactions(transactions);
        findMatchedTransactions(transactions);
        addUnmatchedTransactions(transactions);
        applyRules(transactions);

        // set default split if none was set
        for (InternalTransaction t : transactions) {
          if (!t.isMatched()) {
            if (t.getSplit().size() == 0) {
              if (t.getAmount() > 0) {
                transactionService.addSplit(t, envelopeService.getAvailableEnvelope(account.getUser()), t.getAmount());
              } else {
                transactionService.addSplit(t, envelopeService.getUnassignedEnvelope(account.getUser()), t.getAmount());
              }
            }
          }
        }

        // transfer to as many negative envelopes as possible
        //envelopeService.distributeToNegativeEnvelopes(account, envelopeService.getRootEnvelope(account.getUser()), envelopeService.getAvailableEnvelope(account.getUser()).getBalance());
      }
    }

    if (transactions.size() > 0) {
      transactionService.clearQueryCache();
    }

    return transactions;
  }

  private void applyRules(List<InternalTransaction> transactions) {
    for (InternalTransaction t : transactions) {
      if (t.getSplit().size() == 1 && t.getSplit().iterator().next().getEnvelope() == envelopeService.getUnassignedEnvelope(t.getAccount().getUser())) {
        for (Rule rule : ruleService.getEntities(t.getAccount().getUser())) {
          if (rule.isEnabled() && rule.evaluate(t.getDescription()) &&
              (rule.getAmount() == null || rule.amountEquals(t.getAmount()))) {
            if (rule.getConversion() != null && rule.getConversion().length() > 0) {
              t.setDescription(rule.getConversion());
            }
            transactionService.addSplit(t, rule.getEnvelope(), t.getAmount());
            break;
          }
        }
        for (Split item : t.getSplit()) {
          modifiedEnvelopes.add(item.getEnvelope());
        }
      }
    }
  }

  private void initialDownloadCheck(List<InternalTransaction> transactions) {
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
        if (initialBalanceDate == null || it.getDate().before(initialBalanceDate)) {
          initialBalanceDate = it.getDate();
        }
      }
      if (balance != null) {
        account.setInitialBalance(balance);
        initialBalanceDate = new Date(initialBalanceDate.getTime() - 1);
        InternalTransaction transaction = transactionService.getInitialBalanceTransaction(account);
        if (transaction == null) {
          transaction = new InternalTransaction();
          transaction.setUuid(transactionService.createUuid());
          transaction.setBalance(balance);
          transaction.setType(TransactionType.OTHER);
          transaction.setDate(initialBalanceDate);
          transaction.setDescription("Initial Balance");
          transaction.setStatus(TransactionStatus.reconciled);
          transaction.setAccount(account);
          transaction.setInitialBalance(true);
          transactionService.saveOrUpdate(transaction);
          transactionService.addSplit(transaction, envelopeService.getAvailableEnvelope(account.getUser()), transaction.getAmount());
        } else {
          transactionService.setAmount(transaction, balance, null, true, null, null, null);
          transaction.setDate(initialBalanceDate);
        }
      }
    }
  }

  private void addUnmatchedTransactions(List<InternalTransaction> transactions) {

    for (InternalTransaction t : transactions) {
      if (!t.isMatched()) {
        t.setCustom(false);
        transactionService.saveOrUpdate(t);
      }
    }
  }

  private void matchPreviouslyDownloadedTransactions(List<InternalTransaction> transactions) {

    for (InternalTransaction t : transactions) {
      InternalTransaction existingTransaction =
          transactionService.findTransactionByExternalId(t.getAccount().getUser(), t.getExternalId());
      System.out.println("ZZZ checking downlaoded " + t);
      System.out.println("ZZZ existing " + existingTransaction);
      if (existingTransaction != null && t.getAmount().doubleValue() == existingTransaction.getAmount().doubleValue()) {
        System.out.println("ZZZ matched " + t);
        t.setMatchedTransaction(existingTransaction);
//        t.setSplit(existingTransaction.getSplit());
        transactionService.saveOrUpdate(t);
      }
    }
  }

  private void findMatchedTransactions(List<InternalTransaction> transactions) {

//		int threshold = preferenceService.getInt("ACCOUNT_IMPORT_MATCHING_DAY_THRESHOLD");

    List<InternalTransaction> register = transactionService.getAccountTransactions(account, true);
    for (InternalTransaction importedTransaction : transactions) {

      System.out.println("ZZZ checking import " + importedTransaction);

      if (!importedTransaction.isMatched()) {

        Calendar lowerBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
        lowerBound.add(Calendar.DATE, -matchedDayThreshold);
        Calendar upperBound = CalendarUtil.convertToCalendar(importedTransaction.getDate());
        upperBound.add(Calendar.DATE, matchedDayThreshold);

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
//              importedTransaction.setSplit(t.getSplit());
              t.setExternalId(importedTransaction.getExternalId());
              t.setStatus(TransactionStatus.cleared);
              transactionService.saveOrUpdate(importedTransaction);
              transactionService.saveOrUpdate(t);
            }
          }
        }
      }
    }
  }

}
