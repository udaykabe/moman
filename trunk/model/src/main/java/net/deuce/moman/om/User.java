package net.deuce.moman.om;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(name = "User", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class User extends AbstractEntity<User> {

  private static final long serialVersionUID = 1L;

  private String username;

  private String password;

  private SortedSet<Account> accounts = new TreeSet<Account>();

  public User() {
    super();
  }

  @Basic
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Basic
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int compareTo(User o) {
    return compare(this, o);
  }


  public int compare(User o1, User o2) {
    return compareObjects(o1.username, o2.username);
  }

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @Column(name = "id")
  @Sort(type = SortType.NATURAL)
  public SortedSet<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(SortedSet<Account> accounts) {
    this.accounts = accounts;
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