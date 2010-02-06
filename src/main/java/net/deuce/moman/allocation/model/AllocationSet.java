package net.deuce.moman.allocation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;

public class AllocationSet extends AbstractEntity<AllocationSet> {

	private static final long serialVersionUID = 1L;
	
	public enum Properties implements EntityProperty {
	    name(String.class), allocations(List.class);
	    
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private String name;
	private List<Allocation> allocations = new ArrayList<Allocation>();
	
	public AllocationSet() {}
	
	public AllocationSet(String name) {
		this.name = name;
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
	
	@Override
	public int compareTo(AllocationSet o) {
		return compare(this, o);
	}

	@Override
	public int compare(AllocationSet o1, AllocationSet o2) {
		return o1.name.compareTo(o2.getName());
	}
	
	@Override
	public String toString() {
		return "AllocationSet("+name+")";
	}

}
