package net.deuce.moman.om;

import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.DataDateRange;
import net.deuce.moman.util.Utils;
import net.sf.ofx4j.domain.data.common.TransactionType;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@Service
public class TransactionService extends UserBasedService<InternalTransaction, TransactionDao> {

  @Autowired
  private TransactionDao transactionDao;

  @Autowired
  private AccountService accountService;

  @Autowired
  private SplitService splitService;

  @Autowired
  private EnvelopeService envelopeService;

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
    addElement(el, "desc", trans.getDescription());
    addOptionalElement(el, "extid", trans.getExternalId());
    addOptionalBooleanElement(el, "initial-balance", trans.isInitialBalance());
    addOptionalElement(el, "balance", Utils.formatDouble(trans.getBalance()));
    addElement(el, "memo", trans.getMemo());
    addElement(el, "check", trans.getCheckNo());
    addElement(el, "ref", trans.getRef());
    addElement(el, "status", trans.getStatus().name());
    if (trans.getTransferTransaction() != null) {
      el.addElement("etransfer").addAttribute("id", trans.getTransferTransaction().getUuid());
    }
    Element sel = el.addElement("split");
    Element eel;
    for (Split item : trans.getSplit()) {
      eel = sel.addElement("envelope");
      eel.addAttribute("id", item.getEnvelope().getUuid());
      eel.addAttribute("amount", Utils.formatDouble(item.getAmount()));
    }
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement("transactions");

    for (InternalTransaction trans : getEntities(user)) {
      toXml(trans, root);
    }
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

  public void setAmount(InternalTransaction transaction, Double amount, List<Split> newSplits) {
    setAmount(transaction, amount, newSplits, false);
  }

  private boolean adjustSplits(InternalTransaction transaction, double newAmount, List<Split> newSplits) {
    List<Split> split = transaction.getSplit();
    if (split.size() == 0) return true;

    transaction.clearSplit();

    for (Split item : newSplits) {
      if (transaction.getAmount() < 0.0) {
        item.setAmount(-item.getAmount());
      }
      transaction.addSplit(item);
    }
    transactionDao.saveOrUpdate(transaction);
    return true;
  }

  @Transactional
  public void setAmount(InternalTransaction transaction, Double amount, List<Split> newSplits, boolean adjustBalances) {
    if (transaction.propertyChanged(transaction.getAmount(), amount)) {
      double difference = 0.0;
      if (transaction.getAmount() != null) {
        difference = transaction.getAmount() - amount;
      }
      if (adjustSplits(transaction, amount, newSplits)) {
        transaction.setAmount(amount);
        if (amount > 0) {
          transaction.setType(TransactionType.CREDIT);
        } else if (transaction.getType() == null || transaction.getType() != TransactionType.CHECK) {
          transaction.setType(TransactionType.DEBIT);
        }
        if (transaction.getBalance() != null) {
          transaction.setBalance(transaction.getBalance() - difference);
          for (Split split : transaction.getSplit()) {
            envelopeService.resetBalance(split.getEnvelope());
          }
        }
        if (adjustBalances) {
          adjustBalances(transaction, false);
        }
        saveOrUpdate(transaction);
      }
    }
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getAccountTransactions(Account account, boolean reverse) {
    return transactionDao.getAccountTransactions(account, reverse);
  }

  @Transactional(readOnly = true)
  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env) {
    List<Account> selectedAccounts = accountService.getSelectedAccounts(env.getUser());
    return transactionDao.getEnvelopeTransactionsForAccounts(env, selectedAccounts);
  }

  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env, boolean deep) {
    return getSelectedAccountTransactions(env, null, deep);
  }

  public List<InternalTransaction> getSelectedAccountTransactions(Envelope env, DataDateRange dateRange, boolean deep) {
    List<InternalTransaction> list = getSelectedAccountTransactions(env);
    if (deep) {
      for (Envelope child : env.getChildren()) {
        list.addAll(getSelectedAccountTransactions(child, true));
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
  public List<InternalTransaction> getAccountTransactions(Envelope envelope, Account account) {
    return transactionDao.getAccountEnvelopeTransactions(account, envelope, false);
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

  @Transactional
  public void adjustBalances(InternalTransaction transaction, boolean remove) {

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
          it.setBalance(balance);
          saveOrUpdate(it);

        }
      } else {
        balance = it.getBalance();
      }

    }

    account.setBalance(balance);
    accountService.saveOrUpdate(account);

  }

  @Transactional
  public void setDate(InternalTransaction transaction, Date date, boolean adjust) {
    if (transaction.propertyChanged(transaction.getDate(), date)) {

      transaction.setDate(date);

      if (adjust) {
        adjustBalances(transaction, false);
      }
    }
  }

  @Transactional
  public void clearSplit(InternalTransaction transaction) {
    transaction.clearSplit();
    transactionDao.saveOrUpdate(transaction);
  }

  @Transactional
  public void addSplit(InternalTransaction transaction, Envelope envelope, Double amount) {
    Split item = new Split();
    item.setAmount(amount);
    item.setEnvelope(envelope);
    item.setTransaction(transaction);
    splitService.saveOrUpdate(item);

    List<Split> split = transaction.getSplit();
    if (!split.contains(item)) {
      transaction.addSplit(item);
      saveOrUpdate(transaction);
    }
  }

  @Transactional
  public void removeSplit(InternalTransaction transaction, Envelope envelope) {

    Split item = transaction.getEnvelopeSplit(envelope);

    if (item != null && transaction.removeSplit(item)) {
      envelopeService.resetBalance(envelope);
    }
    transactionDao.saveOrUpdate(transaction);
  }

  @Transactional
  public InternalTransaction getInitialBalanceTransaction(Account account) {
    return transactionDao.getInitialBalanceTransaction(account);
  }

  @Transactional
  public void clearCustomTransactions(User user) {
    transactionDao.clearCustomTransactions(user);
  }
}