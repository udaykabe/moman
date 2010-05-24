package net.deuce.moman.om;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "envelope", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"}), @UniqueConstraint(columnNames = {"parent_id", "name"})})
public class Envelope extends AbstractEntity<Envelope> implements UserBasedEntity {

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
  private SortedSet<Envelope> children = new TreeSet<Envelope>();
  private transient Map<String, Envelope> childrenByName = null;
  private Boolean editable = Boolean.TRUE;
  private Boolean selected = Boolean.FALSE;
  private Boolean expanded = Boolean.FALSE;
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
  private SortedSet<Alert> alerts = new TreeSet<Alert>();

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Transient
  public boolean isDefault() {
    return user == null;
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

  @Transient
  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @Transient
  protected boolean isBalanceDirty() {
    if (balance == null) return true;
    for (Envelope child : children) {
      if (child.isBalanceDirty()) return true;
    }
    return false;
  }

  @Basic
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public void clearBalance() {
    this.balance = null;
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

  @ManyToOne(fetch = FetchType.LAZY)
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

  @OneToMany(mappedBy="parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @Column(name="id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Envelope> getChildren() {
    return children;
  }

  public void setChildren(SortedSet<Envelope> children) {
    this.children = children;
  }

  @Transient
  private Map<String, Envelope> getChildrenMap() {
    if (childrenByName == null) {
      childrenByName = new HashMap<String, Envelope>();
      for (Envelope env : getChildren()) {
        childrenByName.put(env.getName(), env);
      }
    }
    return childrenByName;
  }

  public Envelope getChild(String name) {
    Map<String, Envelope> map = getChildrenMap();
    return map.get(name);
  }

  public void addChild(Envelope child) {
    children.add(child);
    child.setParent(this);
    if (childrenByName != null) childrenByName.clear();
    childrenByName = null;
  }

  public boolean removeChild(Envelope child) {
    boolean result = children.remove(child);
    child.setParent(null);
    if (childrenByName != null) childrenByName.clear();
    childrenByName = null;
    return result;
  }

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

    return compareObjects(o1.name, o2.name);
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
