package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class AccountService extends UserBasedService<Account, AccountDao> {

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private TransactionDao transactionDao;

  protected AccountDao getDao() {
    return accountDao;
  }

  public Command setInitialBalanceCommand(final Account account, final Double initialBalance) {
    return new AbstractCommand(Account.class.getSimpleName() + " setInitialBalance(" + account.getUuid() + ")", true) {

      public void doExecute() throws Exception {

        final Double oldInitialBalance = account.getInitialBalance();

        setInitialBalance(account, initialBalance);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            setInitialBalance(account, oldInitialBalance);
          }
        });
      }
    };
  }

  @Transactional
  public void setInitialBalance(Account account, Double initialBalance) {
    Double currentValue = account.getInitialBalance();

    if (account.propertyChanged(currentValue, initialBalance)) {
      double difference = 0;
      if (currentValue != null) {
        if (initialBalance != null) {
          difference = currentValue - initialBalance;
        } else {
          difference = currentValue;
        }
      } else {
        if (initialBalance != null) {
          difference = -initialBalance;
        } else {
          difference = 0;
        }
      }

      account.setInitialBalance(initialBalance);

      if (difference != 0) {
        for (InternalTransaction it : transactionDao.getAccountTransactions(account, true)) {
          if (it.getBalance() != null) {
            it.setBalance(it.getBalance() - difference);
          } else {
            it.setBalance(-difference);
          }
        }
      }
      if (initialBalance == null) {
        InternalTransaction it = transactionDao.getInitialBalanceTransaction(account);
        if (it != null) {
          transactionDao.delete(it);
        }
      }
    }
  }

  @Transactional(readOnly = true)
  public List<Account> getSelectedAccounts(User user) {
    return getDao().listSelected(user);
  }

  public Class<Account> getType() {
    return Account.class;
  }

  public void toXml(User user, Document doc) {

    Element root = doc.getRootElement().addElement("accounts");

    for (Account account : getEntities(user)) {
      toXml(account, root);
    }
  }

  public void toXml(Account account, Element parent) {
    Element el = parent.addElement("account");
    el.addAttribute("id", account.getUuid());
    addOptionalBooleanElement(el, "selected", account.isSelected());
    addElement(el, "bankId", account.getBankId());
    addElement(el, "accountId", account.getAccountId());
    addElement(el, "username", account.getUsername());
    addElement(el, "password", account.getPassword());
    addElement(el, "nickname", account.getNickname());
    addElement(el, "balance", Utils.formatDouble(account.getBalance()));
    addElement(el, "online-balance", Utils.formatDouble(account.getOnlineBalance()));
    addElement(el, "last-reconciled-ending-balance", Utils.formatDouble(account.getLastReconciledEndingBalance()));

    if (account.getStatus() != null) {
      addElement(el, "status", account.getStatus().name());
    }
    addElement(el, "supports-downloading", account.isSupportsDownloading());
    addOptionalElement(el, "initial-balance", account.getInitialBalance());
    if (account.getLastDownloadDate() != null) {
      addElement(el, "last-download-date", Constants.SHORT_DATE_FORMAT.format(account.getLastDownloadDate()));
    }
    if (account.getLastReconciledDate() != null) {
      addElement(el, "last-reconciled-date", Constants.SHORT_DATE_FORMAT.format(account.getLastReconciledDate()));
    }
    if (account.getFinancialInstitution() != null) {
      el.addElement("financialInstitution").addAttribute("id", account.getFinancialInstitution().getUuid());
    }
  }

  public String getRootElementName() {
    return "accounts";
  }
}