package net.deuce.moman.model.account;

import java.util.Date;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;
import net.deuce.moman.model.fi.FinancialInstitution;

public class Account extends MomanEntity implements Comparable<Account> {

	private static final long serialVersionUID = 1L;

	private String bankId;
	private String accountId;
	private String username;
	private String password;
	private String nickname;
	private boolean selected;

	private FinancialInstitution financialInstitution;
	private Date lastDownloadDate;
	private transient EntityMonitor<Account> monitor = new EntityMonitor<Account>();
	

	@Override
	public int compareTo(Account o) {
		return nickname.compareTo(o.getNickname());
	}

	public void setMonitor(EntityMonitor<Account> monitor) {
		this.monitor = monitor;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		if (propertyChanged(this.bankId, bankId)) {
			this.bankId = bankId;
			monitor.fireEntityChanged(this);
		}
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		if (propertyChanged(this.accountId, accountId)) {
			this.accountId = accountId;
			monitor.fireEntityChanged(this);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (propertyChanged(this.username, username)) {
			this.username = username;
			monitor.fireEntityChanged(this);
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (propertyChanged(this.password, password)) {
			this.password = password;
			monitor.fireEntityChanged(this);
		}
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		if (propertyChanged(this.nickname, nickname)) {
			this.nickname = nickname;
			monitor.fireEntityChanged(this);
		}
	}

	public Date getLastDownloadDate() {
		return lastDownloadDate;
	}

	public void setLastDownloadDate(Date lastDownloadDate) {
		if (propertyChanged(this.lastDownloadDate, lastDownloadDate)) {
			this.lastDownloadDate = lastDownloadDate;
			monitor.fireEntityChanged(this);
		}
	}

	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		if (propertyChanged(this.financialInstitution, financialInstitution)) {
			this.financialInstitution = financialInstitution;
			monitor.fireEntityChanged(this);
		}
	}

}
