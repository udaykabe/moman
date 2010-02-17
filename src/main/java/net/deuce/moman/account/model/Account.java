package net.deuce.moman.account.model;

import java.util.Date;

import net.deuce.moman.fi.model.FinancialInstitution;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.sf.ofx4j.domain.data.common.AccountStatus;

public class Account extends AbstractEntity<Account> {
	
	public enum Properties implements EntityProperty {
		
		NONE(null), ALL(null), bankId(String.class), accountId(String.class),
		username(String.class), password(String.class), nickname(String.class),
		initialBalance(Double.class), selected(Boolean.class),
		financialInstitution(FinancialInstitution.class),
		lastDownloadDate(Date.class), status(AccountStatus.class),
		lastReconciledDate(Date.class), lastReconciledEndingBalance(Double.class),
		supportsDownloading(Boolean.class), balance(Double.class);
		
		private Class<?> type;
		
		private Properties(Class<?> type) { this.type = type; }
		
		public Class<?> type() { return type; }
	}

	private static final long serialVersionUID = 1L;

	private String bankId;
	private String accountId;
	private String username;
	private String password;
	private String nickname;
	private Double initialBalance;
	private Double balance = 0.0;
	private Double lastReconciledEndingBalance = 0.0;
	private Boolean selected = Boolean.FALSE;

	private FinancialInstitution financialInstitution;
	private AccountStatus status;
	private Boolean supportsDownloading = Boolean.FALSE;
	private Date lastDownloadDate;
	private Date lastReconciledDate;
	
	public Account() {
		super();
	}
	
	@Override
	public int compareTo(Account o) {
		return compare(this, o);
	}

	@Override
	public int compare(Account o1, Account o2) {
		return o1.nickname.compareTo(o2.nickname);
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		if (propertyChanged(this.balance, balance)) {
			this.balance = balance;
			getMonitor().fireEntityChanged(this, Properties.balance);
		}
	}

	public Double getLastReconciledEndingBalance() {
		return lastReconciledEndingBalance;
	}

	public void setLastReconciledEndingBalance(Double lastReconciledEndingBalance) {
		if (propertyChanged(this.lastReconciledEndingBalance, lastReconciledEndingBalance)) {
			this.lastReconciledEndingBalance = lastReconciledEndingBalance;
			getMonitor().fireEntityChanged(this, Properties.lastReconciledEndingBalance);
		}
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		if (propertyChanged(this.status, status)) {
			this.status = status;
			getMonitor().fireEntityChanged(this, Properties.status);
		}
	}

	public Boolean isSupportsDownloading() {
		return supportsDownloading;
	}

	public void setSupportsDownloading(Boolean supportsDownloading) {
		if (propertyChanged(this.supportsDownloading, supportsDownloading)) {
			this.supportsDownloading = supportsDownloading;
			getMonitor().fireEntityChanged(this, Properties.supportsDownloading);
		}
	}
	
	public boolean isSelected() {
		return evaluateBoolean(selected);
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		if (propertyChanged(this.selected, selected)) {
			this.selected = selected;
			getMonitor().fireEntityChanged(this, Properties.selected);
			if (selected) {
				getAccountService().addSelectedAccount(this);
			} else {
				getAccountService().removeSelectedAccount(this);
			}
		}
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		if (propertyChanged(this.bankId, bankId)) {
			this.bankId = bankId;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		if (propertyChanged(this.accountId, accountId)) {
			this.accountId = accountId;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (propertyChanged(this.username, username)) {
			this.username = username;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (propertyChanged(this.password, password)) {
			this.password = password;
			getMonitor().fireEntityChanged(this);
		}
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		if (propertyChanged(this.nickname, nickname)) {
			this.nickname = nickname;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public Double getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(Double initialBalance) {
		if (propertyChanged(this.initialBalance, initialBalance)) {
			double difference = 0;
			if (this.initialBalance != null) {
				if (initialBalance != null) {
					difference = this.initialBalance - initialBalance;
				} else {
					difference = this.initialBalance;
				}
			} else {
				if (initialBalance != null) {
					difference = -initialBalance;
				} else {
					difference = 0;
				}
			}
			
			this.initialBalance = initialBalance;
			getMonitor().fireEntityChanged(this);
			
			if (difference != 0) {
				for (InternalTransaction it : getTransactionService().getAccountTransactions(this, true)) {
					if (it.getBalance() != null) {
						it.setBalance(it.getBalance()-difference);
					} else {
						it.setBalance(-difference);
					}
				}
				getTransactionService().notifyEntityListenersOfChanges();
			}
			if (initialBalance == null) {
				InternalTransaction it = getTransactionService().getInitialBalanceTransaction(this);
				if (it != null) {
					getTransactionService().removeEntity(it);
				}
			}
		}
	}

	public Date getLastDownloadDate() {
		return lastDownloadDate;
	}

	public void setLastDownloadDate(Date lastDownloadDate) {
		if (propertyChanged(this.lastDownloadDate, lastDownloadDate)) {
			this.lastDownloadDate = lastDownloadDate;
			getMonitor().fireEntityChanged(this);
		}
	}

	public Date getLastReconciledDate() {
		return lastReconciledDate;
	}

	public void setLastReconciledDate(Date lastReconciledDate) {
		if (propertyChanged(this.lastReconciledDate, lastReconciledDate)) {
			this.lastReconciledDate = lastReconciledDate;
			getMonitor().fireEntityChanged(this, Properties.lastReconciledDate);
		}
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		if (propertyChanged(this.financialInstitution, financialInstitution)) {
			this.financialInstitution = financialInstitution;
			getMonitor().fireEntityChanged(this);
		}
	}

}
