package net.deuce.moman.om;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;
import net.sf.ofx4j.client.context.DefaultApplicationContext;
import net.sf.ofx4j.client.context.OFXApplicationContextHolder;
import net.sf.ofx4j.client.impl.BaseFinancialInstitutionData;
import net.sf.ofx4j.domain.data.banking.BankAccountInfo;
import net.sf.ofx4j.domain.data.signup.AccountProfile;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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

  public Command getAvailableAccountsCommand(final FinancialInstitution financialInstitution, final String username,
                                             final String password) {
    return new AbstractCommand(Account.class.getSimpleName() + " getAvailableAccounts(" + financialInstitution.getUuid() + ")", true) {
      public void doExecute() throws Exception {
        List<Account> accounts = getAvailableAccounts(financialInstitution, username, password);
        setResult(Arrays.asList(new Element[]{toXml(accounts)}));
      }
    };
  }

	public List<Account> getAvailableAccounts(FinancialInstitution financialInstitution,
			String username, String password) throws Exception {
		List<Account> list = new LinkedList<Account>();

		OFXApplicationContextHolder.setCurrentContext(new DefaultApplicationContext("QWIN", "1700"));

		BaseFinancialInstitutionData data = new BaseFinancialInstitutionData(financialInstitution.getFinancialInstitutionId());
		data.setOFXURL(new URL(financialInstitution.getUrl()));
		data.setOrganization(financialInstitution.getOrganization());
		data.setFinancialInstitutionId(financialInstitution.getFinancialInstitutionId());
		net.sf.ofx4j.client.FinancialInstitutionService service = new net.sf.ofx4j.client.impl.FinancialInstitutionServiceImpl();
		net.sf.ofx4j.client.FinancialInstitution fi = service.getFinancialInstitution(data);

		Collection<AccountProfile> profiles = fi.readAccountProfiles(username, password);
		Account account;

		for (AccountProfile profile : profiles) {
			BankAccountInfo bankInfo = profile.getBankSpecifics();

			account = new Account();
      account.setSelected(Boolean.TRUE);
      account.setNickname(profile.getDescription());
      account.setBankId(bankInfo.getBankAccount().getBankId());
      account.setAccountId(bankInfo.getBankAccount().getAccountNumber());
      account.setUsername(username);
      account.setPassword(password);
      account.setStatus(bankInfo.getStatus());
      account.setSupportsDownloading(bankInfo.getSupportsTransactionDetailOperations());
      account.setBalance(0.0);
      account.setOnlineBalance(0.0);
      account.setLastReconciledEndingBalance(0.0);
			list.add(account);
		}
		return list;
	}

  @Transactional(readOnly = true)
  public List<Account> getSelectedAccounts(User user) {
    return getDao().listSelected(user);
  }

  public Class<Account> getType() {
    return Account.class;
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
    addElement(el, "onlineBalance", Utils.formatDouble(account.getOnlineBalance()));
    addElement(el, "lastReconciledEndingBalance", Utils.formatDouble(account.getLastReconciledEndingBalance()));

    if (account.getStatus() != null) {
      addElement(el, "status", account.getStatus().name());
    }
    addElement(el, "supportsDownloading", account.isSupportsDownloading());
    addOptionalElement(el, "initialBalance", account.getInitialBalance());
    if (account.getLastDownloadDate() != null) {
      addElement(el, "lastDownloadDate", Constants.SHORT_DATE_FORMAT.format(account.getLastDownloadDate()));
    }
    if (account.getLastReconciledDate() != null) {
      addElement(el, "lastReconciledDate", Constants.SHORT_DATE_FORMAT.format(account.getLastReconciledDate()));
    }
    if (account.getFinancialInstitution() != null) {
      el.addElement("financialInstitution").addAttribute("id", account.getFinancialInstitution().getUuid());
    }
  }

  public String getRootElementName() {
    return "accounts";
  }
}