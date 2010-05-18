package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "preference", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"uuid"}),
    @UniqueConstraint(columnNames = {"user_id", "name"})
})
public class Preference extends AbstractEntity<Preference> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private String name;
  private String value;
  private String type;
  private User user;

  public Preference() {
    super();
  }

  public int compareTo(Preference o) {
    return compare(this, o);
  }


  public int compare(Preference o1, Preference o2) {
    return compareObjects(o1.name, o2.name);
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Basic
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Basic
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Basic
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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