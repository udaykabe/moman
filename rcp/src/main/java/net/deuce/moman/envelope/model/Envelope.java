package net.deuce.moman.envelope.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.util.CalendarUtil;
import net.deuce.moman.util.DataDateRange;

import org.dom4j.Document;

public class Envelope extends AbstractEntity<Envelope> {

	private static final long serialVersionUID = 1L;
	
	public static final Envelope TOP_LEVEL = new Envelope("", Frequency.NONE, false);
	
	public static final Comparator<Envelope> CHILD_COMPARATOR = new ChildComparator();
	public static final Comparator<Envelope> BILL_COMPARATOR = new BillComparator();
	public static final Comparator<Envelope> SAVINGS_GOAL_COMPARATOR = new SavingsGoalComparator();
	
	public enum Properties implements EntityProperty {
	    name(String.class), balance(Double.class), frequency(Frequency.class),
	    budget(Double.class), parentId(String.class), parent(Envelope.class),
	    children(List.class), transactions(List.class), editable(Boolean.class),
	    selected(Boolean.class), expanded(Boolean.class), monthly(Boolean.class),
	    enabled(Boolean.class), root(Boolean.class), available(Boolean.class),
	    dueDay(Integer.class), unassigned(Boolean.class), index(Integer.class),
	    savingsGoalDate(Date.class);
	    
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private String name;
	private transient Double balance;
	private Frequency frequency;
	private Double budget;
	private transient String parentId;
	private Envelope parent;
	private Map<String, Envelope> children = new HashMap<String, Envelope>();
	private Map<Account, List<InternalTransaction>> transactions = new HashMap<Account, List<InternalTransaction>>();
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
	private transient boolean dirty;
	
	public Envelope() {}
	
	public Envelope(String name, Frequency frequency, Boolean editable) {
		this.name = name;
		this.frequency = frequency;
		this.editable = editable;
	}
	
	@Override
	public Document toXml() {
		return buildXml(Properties.values());
	}

	public Double getSavingsGoalOverrideAmount() {
		return savingsGoalOverrideAmount;
	}

	public void setSavingsGoalOverrideAmount(Double savingsGoalOverrideAmount) {
		this.savingsGoalOverrideAmount = savingsGoalOverrideAmount;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void markDirty() {
		this.dirty = true;
	}

	public void clearDirty() {
		this.dirty = false;
	}

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
	
	public double calculateTransactionsSince(Account account, Calendar cal) {
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
	
	public Boolean isSavingsGoals() {
		return evaluateBoolean(savingsGoals);
	}

	public Boolean getSavingsGoals() {
		return savingsGoals;
	}

	public void setSavingsGoals(Boolean savingsGoal) {
		this.savingsGoals = savingsGoal;
	}

	public boolean isSavingsGoal() {
		return budget != null && savingsGoalDate != null;
	}
	
	public Date getSavingsGoalDate() {
		return savingsGoalDate;
	}

	public void setSavingsGoalDate(Date savingsGoalDate) {
		if (this.savingsGoalDate != savingsGoalDate) {
			dirty = true;
			this.savingsGoalDate = savingsGoalDate;
			getMonitor().fireEntityChanged(this, Properties.savingsGoalDate);
		}
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public boolean isRoot() {
		return evaluateBoolean(root);
	}

	public Boolean getRoot() {
		return root;
	}

	public void setRoot(Boolean root) {
		this.root = root;
	}

	public boolean isAvailable() {
		return evaluateBoolean(available);
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public boolean isMonthly() {
		return evaluateBoolean(monthly);
	}

	public Boolean getMonthly() {
		return monthly;
	}

	public void setMonthly(Boolean monthly) {
		this.monthly = monthly;
	}

	public boolean isUnassigned() {
		return evaluateBoolean(unassigned);
	}

	public Boolean getUnassigned() {
		return unassigned;
	}

	public void setUnassigned(Boolean unassigned) {
		this.unassigned = unassigned;
	}

	public boolean isBill() {
		return dueDay > 0;
	}
	
	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		if (this.frequency != frequency) {
			dirty = true;
			this.frequency = frequency;
			getMonitor().fireEntityChanged(this, Properties.frequency);
		}
	}
	
	public Double getBudget() {
		return getBudget(false);
	}

	public Double getBudget(boolean descend) {
		if (budget == null) return 0.0;
		
		double value = budget;
		if (descend) {
			for (Envelope e : children.values()) {
				value += e.getBudget(true);
			}
		}
		return value;
	}

	public void setBudget(Double budget) {
		if (propertyChanged(this.budget, budget)) {
			dirty = true;
			this.budget = budget;
			getMonitor().fireEntityChanged(this, Properties.budget);
		}
	}

	public int getLevel() {
		if (parent != null) return parent.getLevel() + 1;
		return 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (propertyChanged(this.name, name)) {
			dirty = true;
			this.name = name;
			getMonitor().fireEntityChanged(this, Properties.name);
		}
	}
	
	public String getChartLegendLabel() {
		return name + " " + Constants.CURRENCY_VALIDATOR.format(getBalance());
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	private boolean isBalanceDirty() {
		if (balance == null) return true;
		for (Envelope child : children.values()) {
			if (child.isBalanceDirty()) return true;
		}
		return false;
	}
	
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
			
			/*
			if (available) {
				for (Account account : ServiceNeeder.instance().getAccountService().getSelectedAccounts()) {
					if (account.isSelected() && account.getInitialBalance() != null) {
						balance += account.getInitialBalance();
					}
				}
				
			}
			*/
		}
		
		return Math.round(balance*100)/100.0;
	}
	
	public void setBalance(Double balance) {
		dirty = propertyChanged(this.balance, balance);
		this.balance = balance;
	}

	public boolean isEditable() {
		return evaluateBoolean(editable);
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}
	
	public Boolean isSelected() {
		return evaluateBoolean(selected);
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		if (propertyChanged(this.selected, selected)) {
			dirty = true;
			this.selected = selected;
			Envelope oldEnvelope = getEnvelopeService().getSelectedEnvelope();
			if (oldEnvelope != null) {
				oldEnvelope.selected = false;
			}
			if (selected) {
				getEnvelopeService().setSelectedEnvelope(this);
			} else {
				getEnvelopeService().setSelectedEnvelope(null);
			}
			getMonitor().fireEntityChanged(this, Properties.selected);
		}
	}
	
	public Boolean isExpanded() {
		return evaluateBoolean(expanded);
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		if (propertyChanged(this.expanded, expanded)) {
			dirty = true;
			this.expanded = expanded;
			getMonitor().fireEntityChanged(this, Properties.expanded);
		}
	}

	public Envelope getParent() {
		return parent;
	}

	public void setParent(Envelope parent) {
		if (propertyChanged(this.parent, parent)) {
			dirty = true;
			this.parent = parent;
			getMonitor().fireEntityChanged(this, Properties.parent);
		}
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public List<Envelope> getChildren() {
		List<Envelope> l = new LinkedList<Envelope>(children.values());
		//Collections.sort(l, CHILD_COMPARATOR);
		return l;
	}
	
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
	
	public List<InternalTransaction> getTransactions() {
		List<Account> selectedAccounts = getAccountService().getSelectedAccounts();
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
	
	public List<RepeatingTransaction> getRepeatingTransactions() {
		return repeatingTransactions;
	}

	public List<InternalTransaction> getAllTransactions() {
		List<InternalTransaction> list = new LinkedList<InternalTransaction>();
		
		for (Entry<Account, List<InternalTransaction>> entry : transactions.entrySet()) {
			list.addAll(entry.getValue());
		}
		return list;
	}
	
	public boolean isSpecialEnvelope() {
		return available || unassigned || root || monthly || savingsGoals;
	}
	
	@Override
	public int compareTo(Envelope o) {
		return compare(this, o);
	}

	@Override
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
	
	@Override
	public String toString() {
		return "Envelope("+name+")";
	}
	
	public boolean isEnabled() {
		return evaluateBoolean(enabled);
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		if (this.enabled != enabled) {
			dirty = true;
			this.enabled = enabled;
			getMonitor().fireEntityChanged(this, Properties.enabled);
		}
	}
	public Integer getDueDay() {
		return dueDay;
	}
	public void setDueDay(Integer dueDay) {
		if (propertyChanged(this.dueDay, dueDay)) {
			dirty = true;
			this.dueDay = dueDay;
			getMonitor().fireEntityChanged(this, Properties.dueDay);
		}
	}
	public Double getAmount() {
		return getBudget();
	}
	public void setAmount(Double amount) {
		setBudget(amount);
	}
	
	private static class ChildComparator implements Comparator<Envelope> {
		@Override
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
		@Override
		public int compare(Envelope e1, Envelope e2) {
			return e1.getName().compareTo(e2.getName());
		}
	}
	
	private static class BillComparator implements Comparator<Envelope> {
		@Override
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
