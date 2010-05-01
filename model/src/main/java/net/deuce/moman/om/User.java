package net.deuce.moman.om;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "User",  uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class User extends AbstractEntity<User> {

  private static final long serialVersionUID = 1L;

  private String username;

  private String password;

  private List<Account> accounts = new LinkedList<Account>();

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
    return o1.username.compareTo(o2.username);
  }

  @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
  @Column(name="id")
  public List<Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(List<Account> accounts) {
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