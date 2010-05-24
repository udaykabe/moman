package net.deuce.moman.om;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "tag", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Tag extends AbstractEntity<Tag> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private String name;
  private Boolean enabled = Boolean.TRUE;
  private User user;
  private SortedSet<InternalTransaction> transactions = new TreeSet<InternalTransaction>();

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @OneToMany(fetch = FetchType.LAZY)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<InternalTransaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(SortedSet<InternalTransaction> transactions) {
    this.transactions = transactions;
  }

  public void addTransaction(InternalTransaction t) {
    transactions.add(t);
  }

  public boolean removeTransaction(InternalTransaction t) {
    return transactions.remove(t);
  }

  public void clearTransactions() {
    transactions.clear();
  }

  @Basic
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  @Id
  @GeneratedValue
  public Long getId() {
    return super.getId();
  }

  @Basic
  public String getUuid() {
    return super.getUuid();
  }

  public int compareTo(Tag o) {
    return compare(this, o);
  }


  public int compare(Tag o1, Tag o2) {
    return compareObjects(o1.name, o2.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Tag tag = (Tag) o;

    if (!name.equals(tag.name)) return false;
    if (!user.equals(tag.user)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + user.hashCode();
    return result;
  }
}