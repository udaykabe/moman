package net.deuce.moman.om;

import net.sf.ofx4j.domain.data.common.TransactionType;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "Transaction", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class InternalTransaction extends AbstractEntity<InternalTransaction> {

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
  private Boolean initialBalance;
  private TransactionStatus status;

  private InternalTransaction transferTransaction;

  private List<Split> split = new LinkedList<Split>();

  private Account account;

//	private Map<Envelope, Split> splitMap = new HashMap<Envelope, Split>();

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

  @ManyToOne
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

  /* TODO service method
   public void setAmount(Double amount, SplitSelectionHandler handler) {
     setAmount(amount, false, handler);
   }

   private boolean adjustSplits(double newAmount, SplitSelectionHandler handler) {
     if (splitMap.size() == 0) return true;

     List<Split> split = new LinkedList<Split>(splitMap.values());
     if (split.size() == 1) {
       split.get(0).setAmount(newAmount);
     } else if (handler != null) {
       return handler.handleSplitSelection(this, newAmount, split);
     }
     return true;
   }

   public void setAmount(Double amount, boolean adjustBalances, SplitSelectionHandler handler) {
     if (propertyChanged(this.amount, amount)) {
       double difference = 0.0;
       if (this.amount != null) {
         difference = this.amount - amount;
       }
       if (adjustSplits(amount, handler)) {
         this.amount = amount;
         if (amount > 0) {
           type = TransactionType.CREDIT;
         } else if (type == null || type != TransactionType.CHECK) {
           type = TransactionType.DEBIT;
         }
         if (balance != null) {
           setBalance(balance - difference);
           for (Split split : splitMap.values()) {
             split.getEnvelope().resetBalance();
           }
           getMonitor().fireEntityChanged(this, Properties.amount);
         }
         if (adjustBalances) {
           transactionService.adjustBalances(this, false);
         }
       }
     }
   }
   */

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

  /* TODO service method
	public void setDate(Date date, boolean adjust) {
		if (propertyChanged(this.date, date)) {
			this.date = date;
			getMonitor().fireEntityChanged(this, Properties.date);
			
			if (adjust) {
				transactionService.adjustBalances(this, false);
			}
		}
	}
	*/

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

  @ManyToOne
  @JoinColumn(name = "account_id")
  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @OneToMany(mappedBy = "transaction")
  @Column(name = "id")
  public List<Split> getSplit() {
    return split;
  }

  public void setSplit(List<Split> split) {
    this.split = split;
  }

/* TODO service method
	public Double getSplitAmount(Envelope env) {
		Split split = splitMap.get(env);
		if (split != null) {
			return split.getAmount();
		} 
		return 0.0;
	}

	public void clearSplit() {
		for (Envelope env : splitMap.keySet()) {
			env.removeTransaction(this, false);
		}
		splitMap.clear();
		getMonitor().fireEntityChanged(this, Properties.split);
	}
	
	public void addSplit(Split item) {
		addSplit(item, true);
	}
	
	public void addSplit(Envelope envelope, Double amount) {
		addSplit(new Split(envelope, amount), true);
	}
	
	public void addSplit(Envelope envelope, Double amount, boolean notifyEnvelope) {
		addSplit(new Split(envelope, amount), notifyEnvelope);
	}
	
	public void addSplit(Split split, boolean notifyEnvelope) {
		if (!splitMap.containsValue(split)) {
			if (notifyEnvelope) {
				split.getEnvelope().addTransaction(this, false);
			}
			splitMap.put(split.getEnvelope(), split);
			getMonitor().fireEntityChanged(this, Properties.split);
		}
	}

	public void removeSplit(Envelope envelope) {
		removeSplit(envelope, true);
	}
	
	public void removeSplit(Envelope envelope, boolean notifyEnvelope) {
		if (notifyEnvelope) {
			envelope.removeTransaction(this, false);
		}
		
		if (splitMap.remove(envelope) != null) {
			envelope.resetBalance();
			getMonitor().fireEntityChanged(this, Properties.split);
		}
	}
	
	public List<Split> getSplit() {
		return new LinkedList<Split>(splitMap.values());
	}
	
	public void setSplit(List<Split> splitList) {
		setSplit(splitList, true);
	}
	
	public void setSplit(List<Split> splitList, boolean notify) {
		clearSplit();
		for (Split split : splitList) {
			addSplit(split, notify);
		}
	}

	*/

  public int compareTo(InternalTransaction o) {
    return compare(this, o);
  }


  public int compare(InternalTransaction o1, InternalTransaction o2) {
    int dateCompare = o1.date.compareTo(o2.getDate());

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
      return o1.description.compareTo(o2.description);
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
