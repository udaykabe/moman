package net.deuce.moman.om;

import net.deuce.moman.util.CalendarUtil;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Envelope", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Envelope extends AbstractEntity<Envelope> {

  private static final long serialVersionUID = 1L;

  public static final Envelope TOP_LEVEL = new Envelope("", Frequency.NONE, false);

  public static final Comparator<Envelope> CHILD_COMPARATOR = new ChildComparator();
  public static final Comparator<Envelope> BILL_COMPARATOR = new BillComparator();
  public static final Comparator<Envelope> SAVINGS_GOAL_COMPARATOR = new SavingsGoalComparator();

  private String name;
  private transient Double balance;
  private Frequency frequency;
  private Double budget;
  private transient String parentId;
  private Envelope parent;
  private List<Envelope> children = new LinkedList<Envelope>();
  private List<RepeatingTransaction> repeatingTransactions = new LinkedList<RepeatingTransaction>();
  private Boolean editable;
  private Boolean selected = Boolean.FALSE;
  private Boolean expanded = Boolean.TRUE;
  private Boolean enabled = Boolean.TRUE;
  private Boolean root = Boolean.FALSE;
  private Boolean monthly = Boolean.FALSE;
  private Boolean savingsGoals = Boolean.FALSE;
  private Boolean unassigned = Boolean.FALSE;
  private Boolean available = Boolean.FALSE;
  private Integer dueDay = 0;
  private Integer index = 0;
  private Date savingsGoalDate;
  private Double savingsGoalOverrideAmount;
  private User user;

  public Envelope() {
  }

  public Envelope(String name, Frequency frequency, Boolean editable) {
    this.name = name;
    this.frequency = frequency;
    this.editable = editable;
  }

  @Basic
  public Double getSavingsGoalOverrideAmount() {
    return savingsGoalOverrideAmount;
  }

  public void setSavingsGoalOverrideAmount(Double savingsGoalOverrideAmount) {
    this.savingsGoalOverrideAmount = savingsGoalOverrideAmount;
  }

  /* TODO service method
  public double expensesDuringPeriod(Account account, Frequency frequency) {
    Calendar cal = new GregorianCalendar();
    CalendarUtil.convertCalendarToMidnight(cal);

    frequency.advanceCalendar(cal, true);

    return calculateTransactionsSince(account, cal);
  }

  public double expensesDuringLastNDays(Account account, int days) {
    Calendar cal = new GregorianCalendar();
    CalendarUtil.convertCalendarToMidnight(cal);

    cal.add(Calendar.DAY_OF_YEAR, -days);

    return calculateTransactionsSince(account, cal);
  }
    List<InternalTransaction> transactions = null;
    if (account != null) {
      transactions = getAccountTransactions(account);
    } else {
      transactions = getAllTransactions();
    }

    double sum = 0.0;
    for (InternalTransaction it : transactions) {
      Calendar tcal = new GregorianCalendar();
      tcal.setTime(it.getDate());
      CalendarUtil.convertCalendarToMidnight(tcal);

      if (tcal.after(cal)) {
        sum += it.getAmount();
      }
    }
    return sum;
  }
  */

  @ManyToOne
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Basic
  public Boolean isSavingsGoals() {
    return evaluateBoolean(savingsGoals);
  }

  @Transient
  public Boolean getSavingsGoals() {
    return savingsGoals;
  }

  public void setSavingsGoals(Boolean savingsGoal) {
    this.savingsGoals = savingsGoal;
  }

  @Transient
  public boolean isSavingsGoal() {
    return budget != null && savingsGoalDate != null;
  }

  @Temporal(TemporalType.DATE)
  public Date getSavingsGoalDate() {
    return savingsGoalDate;
  }

  public void setSavingsGoalDate(Date savingsGoalDate) {
    this.savingsGoalDate = savingsGoalDate;
  }

  @Basic
  @Column(name = "idx")
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  @Basic
  public boolean isRoot() {
    return evaluateBoolean(root);
  }

  @Transient
  public Boolean getRoot() {
    return root;
  }

  public void setRoot(Boolean root) {
    this.root = root;
  }

  @Basic
  public boolean isAvailable() {
    return evaluateBoolean(available);
  }

  @Transient
  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  @Basic
  public boolean isMonthly() {
    return evaluateBoolean(monthly);
  }

  @Transient
  public Boolean getMonthly() {
    return monthly;
  }

  public void setMonthly(Boolean monthly) {
    this.monthly = monthly;
  }

  @Basic
  public boolean isUnassigned() {
    return evaluateBoolean(unassigned);
  }

  @Transient
  public Boolean getUnassigned() {
    return unassigned;
  }

  public void setUnassigned(Boolean unassigned) {
    this.unassigned = unassigned;
  }

  @Transient
  public boolean isBill() {
    return dueDay > 0;
  }

  @Enumerated
  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  @Basic
  public Double getBudget() {
    return getBudget(false);
  }

  public Double getBudget(boolean descend) {
    if (budget == null) return 0.0;

    double value = budget;
    if (descend) {
      for (Envelope e : children) {
        value += e.getBudget(true);
      }
    }
    return value;
  }

  public void setBudget(Double budget) {
    this.budget = budget;
  }

  @Transient
  public int getLevel() {
    if (parent != null) return parent.getLevel() + 1;
    return 1;
  }

  @Basic
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /* TODO service method
   public String getChartLegendLabel() {
     return name + " " + Constants.CURRENCY_VALIDATOR.format(getBalance());
   }
   */

  @Transient
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @Transient
  private boolean isBalanceDirty() {
    if (balance == null) return true;
    for (Envelope child : children) {
      if (child.isBalanceDirty()) return true;
    }
    return false;
  }

  /* TODO service method
    public Double getBalance() {
      if (isBalanceDirty()) {

        Double value = 0.0;
        for (InternalTransaction t : getTransactions()) {
          double splitAmount = t.getSplitAmount(this);
          value += splitAmount;
        }
        for (Envelope e : children.values()) {
          value += e.getBalance();
        }
        balance = value;

      }

      return Math.round(balance*100)/100.0;
    }
  */

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Basic
  public boolean isEditable() {
    return evaluateBoolean(editable);
  }

  @Transient
  public Boolean getEditable() {
    return editable;
  }

  public void setEditable(Boolean editable) {
    this.editable = editable;
  }

  @Basic
  public Boolean isSelected() {
    return evaluateBoolean(selected);
  }

  @Transient
  public Boolean getSelected() {
    return selected;
  }

  public void setSelected(Boolean selected) {
    this.selected = selected;
  }

  /* TODO service method
	public void setSelected(Boolean selected) {
		if (propertyChanged(this.selected, selected)) {
			this.selected = selected;
			Envelope oldEnvelope = envelopeService.getSelectedEnvelope();
			if (oldEnvelope != null) {
				oldEnvelope.selected = false;
			}
			if (selected) {
				envelopeService.setSelectedEnvelope(this);
			} else {
				envelopeService.setSelectedEnvelope(null);
			}
			getMonitor().fireEntityChanged(this, Properties.selected);
		}
	}
	*/

  @Basic
  public Boolean isExpanded() {
    return evaluateBoolean(expanded);
  }

  @Transient
  public Boolean getExpanded() {
    return expanded;
  }

  public void setExpanded(Boolean expanded) {
    this.expanded = expanded;
  }

  @ManyToOne
  @JoinColumn(name = "parent_id")
  public Envelope getParent() {
    return parent;
  }

  public void setParent(Envelope parent) {
    this.parent = parent;
  }

  public boolean hasChildren() {
    return children.size() > 0;
  }

  @OneToMany(mappedBy="parent")
  @Column(name="id")
  public List<Envelope> getChildren() {
    return children;
  }

  public void setChildren(List<Envelope> children) {
    this.children = children;
  }
  
/* TODO service method
   public void addChild(Envelope child) {
     if (children.get(child.getName()) != null) {
       throw new RuntimeException("Duplicate envelope name for " + this + ": '" + child.getName() + "'");
     }
     children.put(child.getName(), child);
     getMonitor().fireEntityChanged(this, Properties.children);
   }

   public void removeChild(Envelope child) {
     children.remove(child.getName());
     getMonitor().fireEntityChanged(this, Properties.children);
   }

   public Envelope getChild(String name) {
     return children.get(name);
   }

   */

  /* TODO service method
   public List<InternalTransaction> getAccountTransactions(Account account) {
     List<InternalTransaction> list = transactions.get(account);
     if (list == null) {
       list = new ArrayList<InternalTransaction>();
       transactions.put(account, list);
     }

     return list;
   }

   public List<InternalTransaction> getAccountTransactions(Account account, boolean deep) {
     return getAccountTransactions(account, null, deep);
   }

   public List<InternalTransaction> getAccountTransactions(Account account, DataDateRange dateRange, boolean deep) {
     List<InternalTransaction> list = new LinkedList<InternalTransaction>(getAccountTransactions(account));

     if (deep) {
       for (Envelope child : getChildren()) {
         list.addAll(child.getAccountTransactions(account, deep));
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

   public void addTransaction(InternalTransaction transaction) {
     addTransaction(transaction, true);
   }


   public void resetBalance() {
     resetBalance(this);
   }

   private void resetBalance(Envelope envelope) {
     if (envelope != null) {
       dirty = true;
       envelope.setBalance(null);
       resetBalance(envelope.getParent());
     }
   }

   public void addTransaction(InternalTransaction transaction, boolean notifyTransaction) {
     if (transaction instanceof RepeatingTransaction) {
       repeatingTransactions.add((RepeatingTransaction)transaction);
       return;
     }

     Account account = transaction.getAccount();
     getAccountTransactions(account).add(transaction);
     resetBalance(this);
     dirty = true;
     getMonitor().fireEntityChanged(this, Properties.transactions);
     if (notifyTransaction) {
       transaction.addSplit(this, transaction.getAmount(), false);
     }
   }

   public void removeTransaction(InternalTransaction transaction) {
     removeTransaction(transaction, true);
   }

   public void removeTransaction(InternalTransaction transaction, boolean notifyTransaction) {

     if (transaction instanceof RepeatingTransaction) {
       repeatingTransactions.remove((RepeatingTransaction)transaction);
       return;
     }

     Account account = transaction.getAccount();
     List<InternalTransaction> l = getAccountTransactions(account);
     l.remove(transaction);
     getMonitor().fireEntityChanged(this, Properties.transactions);
     resetBalance(this);
     if (notifyTransaction) {
       transaction.removeSplit(this, false);
     }
   }
   */

  public boolean contains(Envelope env) {
    return contains(env, false);
  }

  public boolean contains(Envelope env, boolean deep) {

    for (Envelope child : getChildren()) {
      if (child == env) return true;
    }

    if (deep) {
      for (Envelope child : getChildren()) {
        if (child.contains(env, true)) {
          return true;
        }
      }
    }
    return false;
  }

  /* TODO service method
   public List<InternalTransaction> getTransactions() {
     List<Account> selectedAccounts = accountService.getSelectedAccounts();
     List<InternalTransaction> list = new LinkedList<InternalTransaction>();

     for (Entry<Account, List<InternalTransaction>> entry : transactions.entrySet()) {
       if (selectedAccounts.size() == 0 || selectedAccounts.contains(entry.getKey())) {
         list.addAll(entry.getValue());
       }
     }
     return list;
   }

   public List<InternalTransaction> getTransactions(boolean deep) {
     return getTransactions(null, deep);
   }

   public List<InternalTransaction> getTransactions(DataDateRange dateRange, boolean deep) {
     List<InternalTransaction> list = getTransactions();
     if (deep) {
       for (Envelope child : getChildren()) {
         list.addAll(child.getTransactions(true));
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
   */

  /*
  @OneToMany(mappedBy="parent")
  @Column(name="id")
  public List<RepeatingTransaction> getRepeatingTransactions() {
    return repeatingTransactions;
  }
  */

  /* TODO service method
	public List<InternalTransaction> getAllTransactions() {
		List<InternalTransaction> list = new LinkedList<InternalTransaction>();
		
		for (Entry<Account, List<InternalTransaction>> entry : transactions.entrySet()) {
			list.addAll(entry.getValue());
		}
		return list;
	}
	*/

  @Transient
  public boolean isSpecialEnvelope() {
    return available || unassigned || root || monthly || savingsGoals;
  }


  public int compareTo(Envelope o) {
    return compare(this, o);
  }


  public int compare(Envelope o1, Envelope o2) {

    if (o1.isSpecialEnvelope() && o2.isSpecialEnvelope()) {
      if (o1.isAvailable() || o2.isRoot()) return -1;
      if (o1.isRoot() || o2.isAvailable()) return 1;
      if (o1.isUnassigned() || o2.isSavingsGoals()) return -1;
      return 1;
    }

    if (!o1.hasChildren() && o2.hasChildren()) return -1;
    if (o1.hasChildren() && !o2.hasChildren()) return 1;
    return o1.name.compareTo(o2.getName());
  }


  public String toString() {
    return "Envelope(" + name + ")";
  }

  @Basic
  public boolean isEnabled() {
    return evaluateBoolean(enabled);
  }

  @Transient
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Integer getDueDay() {
    return dueDay;
  }

  public void setDueDay(Integer dueDay) {
    this.dueDay = dueDay;
  }

  public Double getAmount() {
    return getBudget();
  }

  public void setAmount(Double amount) {
    setBudget(amount);
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

  private static class ChildComparator implements Comparator<Envelope> {

    public int compare(Envelope e1, Envelope e2) {

      if (e1.isSpecialEnvelope() && e2.isSpecialEnvelope()) {
        if (e1.isAvailable() || e2.isRoot()) return -1;
        if (e1.isRoot() || e2.isAvailable()) return 1;
        if (e1.isUnassigned() || e2.isSavingsGoals()) return -1;
        return 1;
      }

      if (!e1.hasChildren() && e2.hasChildren()) return -1;
      if (e1.hasChildren() && !e2.hasChildren()) return 1;
      return e1.getName().compareTo(e2.getName());
    }
  }

  private static class SavingsGoalComparator implements Comparator<Envelope> {

    public int compare(Envelope e1, Envelope e2) {
      return e1.getName().compareTo(e2.getName());
    }
  }

  private static class BillComparator implements Comparator<Envelope> {

    public int compare(Envelope e1, Envelope e2) {
      if (!e1.getFrequency().equals(e2.getFrequency())) {
        return e1.getFrequency().compareTo(e2.getFrequency());
      }
      int val = new Integer(e1.getDueDay()).compareTo(new Integer(e2.getDueDay()));
      if (val == 0) {
        val = e1.getName().compareTo(e2.getName());
        if (val == 0) {
          val = new Integer(e1.getIndex()).compareTo(new Integer(e2.getIndex()));
        }
      }
      return val;
    }
  }

}
