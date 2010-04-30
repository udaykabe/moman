package net.deuce.moman.om;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import net.sf.ofx4j.domain.data.common.AccountStatus;

@Entity
@Table(name = "Account",  uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Account extends AbstractEntity<Account> {
	
	private static final long serialVersionUID = 1L;

  private User user;

	private String bankId;
	
	private String accountId;
	
	private String username;
	
	private String password;
	
	private String nickname;
	
	private Double initialBalance;
	
	private Double balance = 0.0;
	
	private Double onlineBalance = 0.0;
	
	private Double lastReconciledEndingBalance = 0.0;
	
	private Boolean selected = Boolean.FALSE;

	private FinancialInstitution financialInstitution;
	
	private AccountStatus status;
	
	private Boolean supportsDownloading = Boolean.FALSE;
	
	private Date lastDownloadDate;
	
	private Date lastReconciledDate;

  private List<InternalTransaction> transactions = new LinkedList<InternalTransaction>();
	
	public Account() {
		super();
	}
	
	public int compareTo(Account o) {
		return compare(this, o);
	}
	
	public int compare(Account o1, Account o2) {
		return o1.nickname.compareTo(o2.nickname);
	}

  @OneToMany(mappedBy="account")
  @Column(name="id")
  public List<InternalTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<InternalTransaction> transactions) {
    this.transactions = transactions;
  }

  @Basic
  public Double getOnlineBalance() {
		return onlineBalance;
	}

	public void setOnlineBalance(Double onlineBalance) {
		this.onlineBalance = onlineBalance;
	}

  @Basic
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

  @Basic
	public Double getLastReconciledEndingBalance() {
		return lastReconciledEndingBalance;
	}

	public void setLastReconciledEndingBalance(Double lastReconciledEndingBalance) {
		this.lastReconciledEndingBalance = lastReconciledEndingBalance;
	}

  @Enumerated
	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

  @Basic
	public Boolean isSupportsDownloading() {
		return supportsDownloading;
	}

	public void setSupportsDownloading(Boolean supportsDownloading) {
		this.supportsDownloading = supportsDownloading;
	}

  @Basic
	public boolean isSelected() {
		return evaluateBoolean(selected);
	}

  @Transient
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
    this.selected = selected;
	}

  @ManyToOne
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Basic
  public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

  @Basic
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

  @Basic
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

  @Basic
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

  @Basic
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

  @Basic
	public Double getInitialBalance() {
		return initialBalance;
	}

	public void setInitialBalance(Double initialBalance) {
    this.initialBalance = initialBalance;
	}

  @Temporal(TemporalType.DATE)
	public Date getLastDownloadDate() {
		return lastDownloadDate;
	}

	public void setLastDownloadDate(Date lastDownloadDate) {
		this.lastDownloadDate = lastDownloadDate;
	}

  @Temporal(TemporalType.DATE)
	public Date getLastReconciledDate() {
		return lastReconciledDate;
	}

	public void setLastReconciledDate(Date lastReconciledDate) {
		this.lastReconciledDate = lastReconciledDate;
	}

  @ManyToOne
  @JoinColumn(name = "fi_id")
	public FinancialInstitution getFinancialInstitution() {
		return financialInstitution;
	}

	public void setFinancialInstitution(FinancialInstitution financialInstitution) {
		this.financialInstitution = financialInstitution;
	}

  @Id
  @GeneratedValue
  public Long getId() {
    return super.getId();
  }

  @Basic
  public String getUuid() {
    return super.getUuid();
  }
}
