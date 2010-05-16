package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "device", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"}), @UniqueConstraint(columnNames = {"deviceId"})})
public class Device extends AbstractEntity<Device> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private String deviceId;

  private String passcode;

  private User user;

  public Device() {
    super();
  }

  @Basic
  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  @Basic
  public String getPasscode() {
    return passcode;
  }

  public void setPasscode(String passcode) {
    this.passcode = passcode;
  }

  public int compareTo(Device o) {
    return compare(this, o);
  }


  public int compare(Device o1, Device o2) {
    return compareObjects(o1.deviceId, o2.deviceId);
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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