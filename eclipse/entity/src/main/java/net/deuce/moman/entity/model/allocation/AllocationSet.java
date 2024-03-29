package net.deuce.moman.entity.model.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityProperty;
import net.deuce.moman.entity.model.income.Income;

import org.dom4j.Document;

public class AllocationSet extends AbstractEntity<AllocationSet> {

	private static final long serialVersionUID = 1L;
	
	public enum Properties implements EntityProperty {
	    name(String.class), allocations(List.class), income(Income.class);
	    
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private String name;
	private Income income;
	private List<Allocation> allocations = new ArrayList<Allocation>();
	
	public AllocationSet() {}
	
	public AllocationSet(String name) {
		this.name = name;
	}
	
	
	public Document toXml() {
		return buildXml(Properties.values());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (propertyChanged(this.name, name)) {
			this.name = name;
			getMonitor().fireEntityChanged(this, Properties.name);
		}
	}
	
	public Income getIncome() {
		return income;
	}

	public void setIncome(Income income) {
		if (propertyChanged(this.income, income)) {
			this.income = income;
			getMonitor().fireEntityChanged(this, Properties.income);
		}
	}

	public boolean hasAllocations() {
		return allocations.size() > 0;
	}
	
	public List<Allocation> getAllocations() {
		List<Allocation> list = new LinkedList<Allocation>(allocations);
		Collections.sort(list);
		return list;
	}
	
	public void moveAllocations(List<Integer> indexes, Allocation target, boolean before) {
		int startIndex = target.getIndex();
		if (before) {
			startIndex--;
		}
		
		for (int i=startIndex; i<startIndex+indexes.size(); i++) {
			allocations.get(indexes.get(i-startIndex)).setIndex(i);
		}
		
		for (int i=startIndex+indexes.size(); i<allocations.size(); i++) {
			allocations.get(i).setIndex(allocations.get(i).getIndex()+indexes.size());
		}
		Collections.sort(allocations);
		getMonitor().fireEntityChanged(this, Properties.allocations);
	}
	
	public void addAllocation(Allocation allocation) {
		allocations.add(allocation);
		allocation.setAllocationSet(this);
		getMonitor().fireEntityChanged(this, Properties.allocations);
	}
	
	public void removeAllocation(Allocation allocation) {
		allocations.remove(allocation);
		getMonitor().fireEntityChanged(this, Properties.allocations);
	}
	
	
	public int compareTo(AllocationSet o) {
		return compare(this, o);
	}

	
	public int compare(AllocationSet o1, AllocationSet o2) {
		return o1.name.compareTo(o2.getName());
	}
	
	
	public String toString() {
		return "AllocationSet("+name+")";
	}

}
