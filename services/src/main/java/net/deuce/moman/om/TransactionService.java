package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.DataDateRange;
import net.deuce.moman.util.Utils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ofx4j.domain.data.common.TransactionType;
import org.dom4j.Element;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TransactionService extends UserBasedService<InternalTransaction, TransactionDao> implements InitializingBean {

  @Autowired
  private TransactionDao transactionDao;

  @Autowired
  private AccountService accountService;

  @Autowired
  private SplitService splitService;

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private UserService userService;

  private CacheManager cacheManager;
  private Cache queryCache;

  protected TransactionDao getDao() {
    return transactionDao;
  }

  public void buildTransactionXml(Element el, InternalTransaction trans) {
    el.addAttribute("id", trans.getUuid());
    el.addElement("account").addAttribute("id", trans.getAccount().getUuid());
    addElement(el, "amount", Utils.formatDouble(trans.getAmount()));
    addElement(el, "type", trans.getType().name());
    if (trans.getDate() != null) {
      addElement(el, "date", Constants.DATE_FORMAT.format(trans.getDate()));
    }
    addElement(el, "description", trans.getDescription());
    addOptionalElement(el, "externalId", trans.getExternalId());
    addOptionalBooleanElement(el, "initialBalance", trans.isInitialBalance());
    addOptionalElement(el, "balance", Utils.formatDouble(trans.getBalance()));
    addElement(el, "memo", trans.getMemo());
    addElement(el, "checkNo", trans.getCheckNo());
    addElement(el, "ref", trans.getRef());
    addElement(el, "status", trans.getStatus().name());
    if ("201005062".equals(trans.getExternalId())) {
      System.out.println("ZZZ");
    }
    if (trans.isMatched()) {
      el.addElement("matchedTransaction").addAttribute("id", trans.getMatchedTransaction().getUuid());
    }
    if (trans.getTransferTransaction() != null) {
      el.addElement("etransfer").addAttribute("id", trans.getTransferTransaction().getUuid());
    }
    Element sel = el.addElement("split");
    Element eel;
    try {
      for (Split item : trans.getSplit()) {
        eel = sel.addElement("envelope");
        eel.addAttribute("id", item.getEnvelope().getUuid());
        eel.addAttribute("amount", Utils.formatDouble(item.getAmount()));
      }
    } catch (LazyInitializationException e) {
      throw e;
    }
  }

  public void clearQueryCache() {
    queryCache.removeAll();
  }

  public void toXml(InternalTransaction trans, Element parent) {
    Element el = parent.addElement("transaction");
    buildTransactionXml(el, trans);
  }

  public Class<InternalTransaction> getType() {
    return InternalTransaction.class;
  }

  public String getRootElementName() {
    return "transactions";
  }

  private boolean adjustSplits(InternalTransaction transaction, double newAmount, List<Split> newSplits, List<Split> affectedSplits) {
    SortedSet<Split> split = transaction.getSplit();
    if (split.size() == 0) return true;

    affectedSplits.addAll(transaction.getSplit());
    clearSplit(transaction);

    for (Split item : newSplits) {
      if (transaction.getAmount() < 0.0) {
        Split savedSplit = new Split();
        savedSplit.setId(item.getId());
        item.setAmount(-item.getAmount());
      }
      transaction.addSplit(item);
    }
    saveOrUpdate(transaction);

    queryCache.removeAll();

    return true;
  }

  public Command setAmountCommand(final InternalTransaction transaction, final Double amount, final List<Split> newSplits, final Boolean adjustBalances) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " setAmount(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Double accountBalance = transaction.getAccount().getBalance();
        final List<Envelope> affectedEnvelopes = new LinkedList<Envelope>();
        final List<InternalTransaction> affectedTransactions = new LinkedList<InternalTransaction>();
        final List<Split> affectedSplits = new LinkedList<Split>();
        setAmount(transaction, amount, newSplits, adjustBalances, affectedEnvelopes, affectedTransactions, affectedSplits);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAdjustBalances(transaction.getAccount(), accountBalance, affectedTransactions);
            undoSetAmount(affectedEnvelopes);
            undoClearSplit(transaction, affectedSplits);
          }
        });
      }
    };
  }

  @Transactional
  public void undoSetAmount(List<Envelope> affectedEnvelopes) {

    Envelope envelope;
    for (Envelope affectedEnvelope : affectedEnvelopes) {
      envelope = envelopeService.get(affectedEnvelope.getId());
      envelope.setBalance(affectedEnvelope.getBalance());
      envelopeService.saveOrUpdate(envelope);
    }
  }

  private void saveAffectedTransaction(InternalTransaction transaction, List<InternalTransaction> affectedTransactions) {
    if (affectedTransactions == null) return;
    InternalTransaction savedTransaction = new InternalTransaction();
    savedTransaction.setId(transaction.getId());
    savedTransaction.setAmount(transaction.getAmount());
    savedTransaction.setType(transaction.getType());
    savedTransaction.setBalance(transaction.getBalance());
    savedTransaction.setDate(transaction.getDate());
    affectedTransactions.add(savedTransaction);
  }

  private void saveAffectedEnvelope(Envelope envelope, List<Envelope> affectedEnvelopes) {
    if (affectedEnvelopes == null) return;
    Envelope savedEnvelope = new Envelope();
    savedEnvelope.setId(envelope.getId());
    savedEnvelope.setBalance(envelope.getBalance());
    affectedEnvelopes.add(savedEnvelope);
  }

  @Transactional
  public void setAmount(InternalTransaction transaction, Double amount, List<Split> newSplits, boolean adjustBalances,
                        List<Envelope> affectedEnvelopes, List<InternalTransaction> affectedTransactions,
                        List<Split> affectedSplits) {
    if (transaction.propertyChanged(transaction.getAmount(), amount)) {
      double difference = 0.0;
      if (transaction.getAmount() != null) {
        difference = transaction.getAmount() - amount;
      }
      if (adjustSplits(transaction, amount, newSplits, affectedSplits)) {

        saveAffectedTransaction(transaction, affectedTransactions);

        transaction.setAmount(amount);
        if (amount > 0) {
          transaction.setType(TransactionType.CREDIT);
        } else if (transaction.getType() == null || transaction.getType() != TransactionType.CHECK) {
          transaction.setType(TransactionType.DEBIT);
        }
        if (transaction.getBalance() != null) {
          transaction.setBalance(transaction.getBalance() - difference);
          for (Split split : transaction.getSplit()) {

            saveAffectedEnvelope(split.getEnvelope(), affectedEnvelopes);
            envelopeService.resetBalance(split.getEnvelope());
          }
        }
        if (adjustBalances) {
          adjustBalances(transaction, false, affectedTransactions);
        }
        saveOrUpdate(transaction);
      }
      queryCache.removeAll();
    }
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getAccountTransactions(Account account, boolean reverse) {
    return transactionDao.getAccountTransactions(account, reverse);
  }

  private void walkDom(List<InternalTransaction> transactions) {
    for (InternalTransaction trans : transactions) {
      trans.getAccount().getFinancialInstitution();
      trans.getMatchedTransaction();
      for (Split item : trans.getSplit()) {
        item.getEnvelope();
      }
      trans.getTransferTransaction();
    }
  }

  public Command getSelectedAccountTransactionsCommand(final Envelope env, final Boolean deep, final Boolean transfers, final Boolean reverse, final Integer from, final Integer count) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getSelectedAccountTransactions()", true) {
      public void doExecute() throws Exception {
        String key = new StringBuffer(env.getUuid()).append(deep.toString()).append(transfers.toString()).append(reverse.toString()).toString();
        net.sf.ehcache.Element cacheElement = queryCache.get(key);
        if (cacheElement == null) {

          List<InternalTransaction> fetchedTransactions = getSelectedAccountTransactions(env, deep, transfers);

          if (fetchedTransactions.size() > 1) {
            if (reverse) {
              Collections.sort(fetchedTransactions, fetchedTransactions.get(0).getReverseComparator());
            } else {
              Collections.sort(fetchedTransactions, fetchedTransactions.get(0).getForwardComparator());
            }
          }

          cacheElement = new net.sf.ehcache.Element(key, fetchedTransactions);
          queryCache.put(cacheElement);

          // first time through navigate the entire object graph because subsequent pages
          // won't be fully available on future requests
          walkDom(fetchedTransactions);

        }

        List<InternalTransaction> transactions = (List<InternalTransaction>) cacheElement.getValue();

        setResult(toXml(transactions, from, count));
      }
    };
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env) {
    List<Account> selectedAccounts = accountService.getSelectedAccounts(env.getUser());
    return transactionDao.getEnvelopeTransactionsForAccounts(env, selectedAccounts);
  }

  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env, boolean deep, boolean transfers) {
    return getSelectedAccountTransactions(env, null, deep, transfers);
  }

  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env, DataDateRange dateRange, boolean deep, boolean transfers) {
    List<InternalTransaction> list = getSelectedAccountTransactions(env);
    if (deep) {
      for (Envelope child : env.getChildren()) {
        list.addAll(getSelectedAccountTransactions(child, true, transfers));
      }
    }

    if (dateRange != null) {
      ListIterator<InternalTransaction> itr = list.listIterator();
      while (itr.hasNext()) {
        if (!CalendarUtil.dateInRange(itr.next().getDate(), dateRange)) {
          itr.remove();
        }
      }
    }

    if (!transfers) {
      ListIterator<InternalTransaction> itr = list.listIterator();
      while (itr.hasNext()) {
        if (itr.next().isEnvelopeTransfer()) {
          itr.remove();
        }
      }
    }

    return list;
  }

  public Command getAccountTransactionsWithRangeCommand(final Envelope envelope, final Account account, final DataDateRange dateRange, final Boolean deep) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getAccountTransactions()", true) {
      public void doExecute() throws Exception {
        setResult(toXml(getAccountTransactions(envelope, account, dateRange, deep)));
      }
    };
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getAccountTransactions(Envelope envelope, Account account) {
    return transactionDao.getAccountEnvelopeTransactions(account, envelope, false);
  }

  public Command getAccountTransactionsCommand(final Envelope envelope, final Account account, final Boolean deep) {
    return new AbstractCommand(Envelope.class.getSimpleName() + " getAccountTransactions()", true) {
      public void doExecute() throws Exception {
        setResult(toXml(getAccountTransactions(envelope, account, null, deep)));
      }
    };
  }

  public List<InternalTransaction> getAccountTransactions(Envelope env, Account account, boolean deep) {
    return getAccountTransactions(env, account, null, deep);
  }

  public List<InternalTransaction> getAccountTransactions(Envelope env, Account account, DataDateRange dateRange, boolean deep) {
    List<InternalTransaction> list = new LinkedList<InternalTransaction>(getAccountTransactions(env, account));

    if (deep) {
      for (Envelope child : env.getChildren()) {
        list.addAll(getAccountTransactions(child, account, deep));
      }
    }

    if (dateRange != null) {
      ListIterator<InternalTransaction> itr = list.listIterator();
      while (itr.hasNext()) {
        if (!CalendarUtil.dateInRange(itr.next().getDate(), dateRange)) {
          itr.remove();
        }
      }
    }

    return list;
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getAllTransactions(Envelope env) {
    return transactionDao.getEnvelopeTransactions(env);
  }

  public Command adjustBalancesCommand(final InternalTransaction transaction, final Boolean remove) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " adjustBalances(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Account account = transaction.getAccount();
        final Double accountBalance = account.getBalance();
        final List<InternalTransaction> affectedTransactions = new LinkedList<InternalTransaction>();
        adjustBalances(transaction, remove, affectedTransactions);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAdjustBalances(account, accountBalance, affectedTransactions);
          }
        });
      }
    };
  }

  @Transactional
  public void undoAdjustBalances(Account account, Double accountBalance, List<InternalTransaction> affectedTransactions) {
    account.setBalance(accountBalance);
    accountService.saveOrUpdate(account);

    InternalTransaction trans;
    for (InternalTransaction affectedTransaction : affectedTransactions) {
      trans = get(affectedTransaction.getId());
      trans.setAmount(affectedTransaction.getAmount());
      trans.setType(affectedTransaction.getType());
      trans.setBalance(affectedTransaction.getBalance());
      trans.setDate(affectedTransaction.getDate());
      saveOrUpdate(trans);
    }
  }

  public Command newTransactionCommand(final InternalTransaction transaction, final Envelope envelope) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " newTransaction(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Account account = transaction.getAccount();
        final Double accountBalance = account.getBalance();
        final List<InternalTransaction> affectedTransactions = new LinkedList<InternalTransaction>();
        final List<Split> affectedSplits = new LinkedList<Split>();

        saveOrUpdate(transaction);
        if (envelope != null) {
          affectedSplits.add(addSplit(transaction, envelope, transaction.getAmount()));
        } else {
          affectedSplits.add(addSplit(transaction, envelopeService.getUnassignedEnvelope(userService.getStaticUser()), transaction.getAmount()));
        }
        adjustBalances(transaction, false, affectedTransactions);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAdjustBalances(account, accountBalance, affectedTransactions);
            undoAddSplit(affectedSplits.get(0));
            delete(transaction);
          }
        });
      }
    };
  }

  @Transactional
  public void adjustBalances(InternalTransaction transaction, boolean remove, List<InternalTransaction> affectedTransactions) {

    Double balance = null;

    boolean foundStartingPlace = false;
    Account account = transaction.getAccount();

    for (InternalTransaction it : getAccountTransactions(account, false)) {
      if (it == transaction) {
        if (balance == null) {
          balance = 0.0;
        }
        foundStartingPlace = true;
      }

      if (foundStartingPlace) {
        if (!remove) {
          try {
            balance += it.getAmount();
          } catch (Exception e) {
            e.printStackTrace();
          }
          saveAffectedTransaction(it, affectedTransactions);
          it.setBalance(balance);
          saveOrUpdate(it);

        }
      } else {
        balance = it.getBalance();
      }

    }

    account.setBalance(balance);
    accountService.saveOrUpdate(account);
    queryCache.removeAll();

  }

  public Command setDateCommand(final InternalTransaction transaction, final Date date, final Boolean adjust) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " setDate(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Double accountBalance = transaction.getAccount().getBalance();
        final List<InternalTransaction> affectedTransactions = new LinkedList<InternalTransaction>();
        setDate(transaction, date, adjust, affectedTransactions);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAdjustBalances(transaction.getAccount(), accountBalance, affectedTransactions);
          }
        });
      }
    };
  }

  @Transactional
  public void setDate(InternalTransaction transaction, Date date, boolean adjust, List<InternalTransaction> affectedTransactions) {
    if (transaction.propertyChanged(transaction.getDate(), date)) {

      saveAffectedTransaction(transaction, affectedTransactions);
      transaction.setDate(date);

      if (adjust) {
        adjustBalances(transaction, false, affectedTransactions);
      }

      queryCache.removeAll();
    }
  }

  public Command clearSplitCommand(final InternalTransaction transaction) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " clearSplit(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final List<Split> affectedSplits = new LinkedList<Split>();
        affectedSplits.addAll(transaction.getSplit());

        clearSplit(transaction);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoClearSplit(transaction, affectedSplits);
          }
        });
      }
    };
  }

  @Transactional
  public void undoClearSplit(InternalTransaction transaction, List<Split> affectedSplits) {
    transaction.clearSplit();
    for (Split item : affectedSplits) {
      addSplit(transaction, item.getEnvelope(), item.getAmount());
    }
  }

  @Transactional
  public void clearSplit(InternalTransaction transaction) {

    for (Split item : transaction.getSplit()) {
      splitService.delete(item);
    }

    transaction.clearSplit();
    saveOrUpdate(transaction);

    queryCache.removeAll();
  }

  public Command addSplitCommand(final InternalTransaction transaction, final Envelope envelope, final Double amount) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " addSplit(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Split addedSplitItem = addSplit(transaction, envelope, amount);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoAddSplit(addedSplitItem);
          }
        });
      }
    };
  }

  @Transactional
  public void undoAddSplit(Split splitItem) {
    removeSplit(splitItem.getTransaction(), splitItem.getEnvelope());
    splitService.delete(splitItem);
  }

  @Transactional
  public Split addSplit(InternalTransaction transaction, Envelope envelope, Double amount) {
    Split item = new Split();
    item.setAmount(amount);
    item.setEnvelope(envelope);
    item.setTransaction(transaction);

    SortedSet<Split> split = transaction.getSplit();
    if (split.contains(item)) {
      throw new RuntimeException("Duplicate split item (" + item + ") added to " + transaction);
    }

    splitService.saveOrUpdate(item);
    transaction.addSplit(item);
    saveOrUpdate(transaction);
    queryCache.removeAll();

    return item;
  }

  public Command removeSplitCommand(final InternalTransaction transaction, final Envelope envelope) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " removeSplit(" + transaction.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Double splitAmount = transaction.getEnvelopeSplit(envelope).getAmount();

        removeSplit(transaction, envelope);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            addSplit(transaction, envelope, splitAmount);
          }
        });
      }
    };
  }

  @Transactional
  public void removeSplit(InternalTransaction transaction, Envelope envelope) {

    Split item = transaction.getEnvelopeSplit(envelope);

    if (item != null && transaction.removeSplit(item)) {
      envelopeService.resetBalance(envelope);
    }
    saveOrUpdate(transaction);
    splitService.delete(item);
    queryCache.removeAll();
  }

  @Transactional
  public InternalTransaction getInitialBalanceTransaction(Account account) {
    return transactionDao.getInitialBalanceTransaction(account);
  }

  public Command clearCustomTransactionsCommand(final User user) {
    return new AbstractCommand(InternalTransaction.class.getSimpleName() + " clearCustomTransactions", true) {

      public void doExecute() throws Exception {

        final List<InternalTransaction> savedTransactions = new LinkedList<InternalTransaction>();
        savedTransactions.addAll(transactionDao.getCustomTransactions(user, false));

        clearCustomTransactions(user);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            undoClearCustomTransactions(savedTransactions);
          }
        });
      }
    };
  }

  public InternalTransaction cloneTransaction(InternalTransaction trans) {
    InternalTransaction transaction = new InternalTransaction();
    transaction.setAccount(trans.getAccount());
    transaction.setAmount(trans.getAmount());
    transaction.setBalance(trans.getBalance());
    transaction.setCheckNo(trans.getCheckNo());
    transaction.setCustom(trans.isCustom());
    transaction.setDate(trans.getDate());
    transaction.setDescription(trans.getDescription());
    transaction.setExternalId(trans.getExternalId());
    transaction.setImported(trans.isImported());
    transaction.setInitialBalance(trans.isInitialBalance());
    transaction.setMatchedTransaction(trans.getMatchedTransaction());
    transaction.setMemo(trans.getMemo());
    transaction.setRef(trans.getRef());
    transaction.setSplit(trans.getSplit());
    transaction.setStatus(trans.getStatus());
    transaction.setTransferTransaction(trans.getTransferTransaction());
    transaction.setType(trans.getType());
    transaction.setUuid(createUuid());
    return transaction;
  }

  @Transactional
  public void undoClearCustomTransactions(List<InternalTransaction> savedTransactions) {
    for (InternalTransaction trans : savedTransactions) {
      saveOrUpdate(cloneTransaction(trans));
    }
  }

  @Transactional
  public void clearCustomTransactions(User user) {

//    transactionDao.clearCustomTransactions(user);
    for (InternalTransaction t : transactionDao.getCustomTransactions(user, false)) {
      t.setCustom(false);
      saveOrUpdate(t);
    }
    queryCache.removeAll();
  }

  @Transactional(readOnly = true)
  public InternalTransaction findTransactionByExternalId(User user, String externalId) {
    return transactionDao.findTransactionsByExternalId(user, externalId);
  }

  public List<InternalTransaction> getCustomTransactions(User user, boolean reverse) {
    return transactionDao.getCustomTransactions(user, reverse);
  }

  public void afterPropertiesSet() throws Exception {
    cacheManager = CacheManager.create();
    queryCache = new Cache("transactionService", 100, false, false, 300, 300);
    cacheManager.addCache(queryCache);
  }
}