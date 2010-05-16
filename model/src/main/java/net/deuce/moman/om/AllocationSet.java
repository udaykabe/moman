package net.deuce.moman.om;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "allocation_set", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class AllocationSet extends AbstractEntity<AllocationSet> {

  private static final long serialVersionUID = 1L;

  private String name;
  private Income income;
  private SortedSet<Allocation> allocations = new TreeSet<Allocation>();

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

  @ManyToOne(fetch = FetchType.LAZY)
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

  @OneToMany(mappedBy = "allocationSet", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Allocation> getAllocations() {
    return allocations;
  }

  public void setAllocations(SortedSet<Allocation> allocations) {
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
    return o1.name.compareTo(o2.name);
  }


  public String toString() {
    return "AllocationSet(" + name + ")";
  }

}
