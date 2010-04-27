package net.deuce.moman.om;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "AllocationSet", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class AllocationSet extends AbstractEntity<AllocationSet> {

  private static final long serialVersionUID = 1L;

  private String name;
  private Income income;
  private List<Allocation> allocations = new ArrayList<Allocation>();

  public AllocationSet() {
  }

  public AllocationSet(String name) {
    this.name = name;
  }

  @Basic
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ManyToOne
  @JoinColumn(name = "income_id")
  public Income getIncome() {
    return income;
  }

  public void setIncome(Income income) {
    this.income = income;
  }

  public boolean hasAllocations() {
    return allocations.size() > 0;
  }

  @OneToMany(mappedBy = "allocationSet")
  @Column(name = "id")
  public List<Allocation> getAllocations() {
    List<Allocation> list = new LinkedList<Allocation>(allocations);
    Collections.sort(list);
    return list;
  }

  public void setAllocations(List<Allocation> allocations) {
    this.allocations = allocations;
  }

  public void addAllocation(Allocation allocation) {
    allocations.add(allocation);
    allocation.setAllocationSet(this);
  }

  public void removeAllocation(Allocation allocation) {
    allocations.remove(allocation);
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

  public int compareTo(AllocationSet o) {
    return compare(this, o);
  }


  public int compare(AllocationSet o1, AllocationSet o2) {
    return o1.name.compareTo(o2.getName());
  }


  public String toString() {
    return "AllocationSet(" + name + ")";
  }

}
