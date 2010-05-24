package net.deuce.moman.om;

import net.sf.ofx4j.domain.data.common.TransactionType;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "transaction", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class InternalTransaction extends AbstractEntity<InternalTransaction> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private String externalId;
  private Double amount;
  private TransactionType type;
  private Date date;
  private String description;
  private String memo;
  private String checkNo;
  private String ref;
  private Double balance;
  private Boolean initialBalance = Boolean.FALSE;
  private TransactionStatus status = TransactionStatus.open;
  private Boolean custom = Boolean.FALSE;
  private User user;
  private Boolean viewed = Boolean.FALSE;

  private InternalTransaction transferTransaction;

  private SortedSet<Split> split = new TreeSet<Split>();
  private SortedSet<Tag> tags = new TreeSet<Tag>();
  private SortedSet<Alert> alerts = new TreeSet<Alert>();
  private transient Map<Envelope, Split> envelopeSplitMap = null;

  private Account account;

  private transient InternalTransaction matchedTransaction;
  private transient String transferTransactionId;
  private transient boolean imported;

  // constants for annotations
  public static final int TYPE_LENGTH = 20;
  public static final int DESCRIPTION_LENGTH = 256;
  public static final int MEMO_LENGTH = 256;
  public static final int CHECK_NUMBER_LENGTH = 20;
  public static final int REF_NUMBER_LENGTH = 256;
  public static final int EXT_ID_LENGTH = 50;

  public InternalTransaction() {
    super();
  }

  @Transient
  private Map<Envelope, Split> getEnvelopeSplitMap() {
    if (envelopeSplitMap == null) {
      envelopeSplitMap = new HashMap<Envelope, Split>();
      for (Split s : getSplit()) {
        envelopeSplitMap.put(s.getEnvelope(), s);
      }
    }
    return envelopeSplitMap;
  }

  @Transient
  public Double getSplitAmount(Envelope env) {
    Split split = getEnvelopeSplit(env);
    if (split != null) {
      return split.getAmount();
    }
    return 0.0;
  }

  public Split getEnvelopeSplit(Envelope env) {
    return getEnvelopeSplitMap().get(env);
  }

  @Transient
  public boolean isImported() {
    return imported;
  }

  public void setImported(boolean imported) {
    this.imported = imported;
  }

  @Transient
  public boolean isEnvelopeTransfer() {
    return transferTransaction != null;
  }

  @Transient
  public String getTransferTransactionId() {
    return transferTransactionId;
  }

  public void setTransferTransactionId(String transferTransactionId) {
    this.transferTransactionId = transferTransactionId;
  }

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transfer_id")
  public InternalTransaction getTransferTransaction() {
    return transferTransaction;
  }

  public void setTransferTransaction(InternalTransaction transferTransaction) {
    this.transferTransaction = transferTransaction;
  }

  @Enumerated
  public TransactionStatus getStatus() {
    return status;
  }

  public void setStatus(TransactionStatus status) {
    this.status = status;
  }

  @Basic
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void determineAndSetType() {
    if (checkNo == null || checkNo.length() == 0) {
      if (amount >= 0) {
        setType(TransactionType.CREDIT);
      } else {
        setType(TransactionType.DEBIT);
      }
    } else {
      setType(TransactionType.CHECK);
    }
  }

  @Basic
  public boolean isViewed() {
    return evaluateBoolean(viewed);
  }

  @Transient
  public Boolean getViewed() {
    return viewed;
  }

  public void setViewed(Boolean viewed) {
    this.viewed = viewed;
  }

  @Basic
  public boolean isCustom() {
    return evaluateBoolean(custom);
  }

  @Transient
  public Boolean getCustom() {
    return custom;
  }

  public void setCustom(Boolean custom) {
    this.custom = custom;
  }

  @Basic
  public boolean isInitialBalance() {
    return evaluateBoolean(initialBalance);
  }

  @Transient
  public Boolean getInitialBalance() {
    return initialBalance;
  }

  public void setInitialBalance(Boolean initialBalance) {
    this.initialBalance = initialBalance;
  }

  @Transient
  public boolean isMatched() {
    return matchedTransaction != null;
  }

  public void setMatchedTransaction(InternalTransaction transaction) {
    this.matchedTransaction = transaction;
  }

  @Transient
  public InternalTransaction getMatchedTransaction() {
    return matchedTransaction;
  }

  @Basic
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Enumerated
  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  @Temporal(TemporalType.DATE)
  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Basic
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Basic
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Basic
  public String getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  @Basic
  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  @Transient
  public boolean isExternal() {
    return externalId != null;
  }

  @Basic
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @OneToMany(fetch = FetchType.LAZY)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Tag> getTags() {
    return tags;
  }

  public void setTags(SortedSet<Tag> tags) {
    this.tags = tags;
  }

  public void addTag(Tag t) {
    tags.add(t);
  }

  public boolean removeTag(Tag t) {
    return tags.remove(t);
  }

  public void clearTags() {
    tags.clear();
  }

  @OneToMany(fetch = FetchType.LAZY)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Alert> getAlerts() {
    return alerts;
  }

  public void setAlerts(SortedSet<Alert> alerts) {
    this.alerts = alerts;
  }

  public void addAlert(Alert t) {
    alerts.add(t);
  }

  public boolean removeAlert(Alert t) {
    return alerts.remove(t);
  }

  public void clearAlerts() {
    alerts.clear();
  }

  @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Split> getSplit() {
    if (isMatched()) {
      return matchedTransaction.getSplit();
    }
    return split;
  }

  public void setSplit(SortedSet<Split> split) {
    this.split = split;
  }

  public void addSplit(Split s) {
    split.add(s);
    envelopeSplitMap = null;
  }

  public boolean removeSplit(Split s) {
    envelopeSplitMap = null;
    return split.remove(s);
  }

  public void clearSplit() {
    split.clear();
    envelopeSplitMap = null;
  }

  public int compareTo(InternalTransaction o) {
    return compare(this, o);
  }

  public int compare(InternalTransaction o1, InternalTransaction o2) {
    int dateCompare = compareObjects(o1.date, o2.date);

    if (dateCompare == 0) {
      if (o1.externalId != null && o2.getExternalId() != null) {
        return o1.externalId.compareTo(o2.getExternalId());
      }
      if (o1.externalId != null) {
        return -1;
      }
      if (o2.externalId != null) {
        return 1;
      }
      return compareObjects(o1.description, o2.description);
    }
    return dateCompare;
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    InternalTransaction it = (InternalTransaction) o;

    if (externalId != null ? !externalId.equals(it.externalId) : it.externalId != null) return false;
    return !(getId() != null ? !getId().equals(it.getId()) : it.getId() != null);

  }


  public int hashCode() {
    int result = getUuid() != null ? getUuid().hashCode() : 0;
    result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
    return result;
  }


  public String toString() {
    return "InternalTransaction : " + getId() + " - " + description + " - " + amount;
  }


  @Transient
  public String getRootName() {
    return "transaction";
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
