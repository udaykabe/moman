package net.deuce.moman.model.envelope;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;
import net.deuce.moman.model.transaction.InternalTransaction;

public class Envelope extends MomanEntity implements Comparable<Envelope> {

	private static final long serialVersionUID = 1L;
	
	public static final Comparator<Envelope> CHILD_COMPARATOR = new ChildComparator();

	private String name;
	private transient Float balance;
	private Frequency frequency;
	private float budget;
	private transient String parentId;
	private Envelope parent;
	private List<Envelope> children = new LinkedList<Envelope>();
	private List<Transaction> transactions = new LinkedList<Transaction>();
	private boolean editable;
	private boolean expanded = true;
	
	private transient EntityMonitor<Envelope> monitor = new EntityMonitor<Envelope>();
	
	public EntityMonitor<Envelope> getMonitor() {
		return this.monitor;
	}
	
	public void setMonitor(EntityMonitor<Envelope> monitor) {
		this.monitor = monitor;
	}
	
	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		if (this.frequency != frequency) {
			this.frequency = frequency;
			getMonitor().fireEntityChanged(this);
		}
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		if (this.budget != budget) {
			this.budget = budget;
			getMonitor().fireEntityChanged(this);
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
			this.name = name;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public float getBalance() {
		if (balance == null) {
			float value = 0f;
			for (Transaction t : transactions) {
				value += t.getAmount();
			}
			for (Envelope e : children) {
				value += e.getBalance();
			}
			balance = value;
		}
		return balance;
	}
	
	public void setBalance(float balance) {
		this.balance = balance;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		if (propertyChanged(this.expanded, expanded)) {
			this.expanded = expanded;
			getMonitor().fireEntityChanged(this);
		}
	}

	public Envelope getParent() {
		return parent;
	}

	public void setParent(Envelope parent) {
		if (propertyChanged(this.parent, parent)) {
			this.parent = parent;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public List<Envelope> getChildren() {
		List<Envelope> l = new LinkedList<Envelope>(children);
		//Collections.sort(l, CHILD_COMPARATOR);
		return l;
	}
	
	public void addChild(Envelope child) {
		children.add(child);
		getMonitor().fireEntityChanged(this);
	}
	
	public void removeChild(Envelope child) {
		children.remove(child);
		getMonitor().fireEntityChanged(this);
	}

	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
		getMonitor().fireEntityChanged(this);
	}

	public void removeTransaction(Transaction transaction) {
		transactions.remove(transaction);
		getMonitor().fireEntityChanged(this);
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}

	@Override
	public int compareTo(Envelope o) {
		if (!hasChildren() && o.hasChildren()) return -1;
		if (hasChildren() && !o.hasChildren()) return 1;
		return name.compareTo(o.getName());
	}

	@Override
	public String toString() {
		return "Envelope("+name+")";
	}

	private static class ChildComparator implements Comparator<Envelope> {
		@Override
		public int compare(Envelope e1, Envelope e2) {
			if (!e1.hasChildren() && e2.hasChildren()) return -1;
			if (e1.hasChildren() && !e2.hasChildren()) return 1;
			return e1.getName().compareTo(e2.getName());
		}
		
	}
}
