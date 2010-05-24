package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "alert", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Alert extends AbstractEntity<Alert> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private Envelope envelope;
  private InternalTransaction transaction;
  private User user;
  private AlertType alertType;

  public Alert() {
    super();
  }

  public int compareTo(Alert o) {
    return compare(this, o);
  }


  public int compare(Alert o1, Alert o2) {
    return compareObjects(o1.alertType, o2.alertType);
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
  public AlertType getAlertType() {
    return alertType;
  }

  public void setAlertType(AlertType alertType) {
    this.alertType = alertType;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "envelope_id")
  public Envelope getEnvelope() {
    return envelope;
  }

  public void setEnvelope(Envelope envelope) {
    this.envelope = envelope;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id")
  public InternalTransaction getTransaction() {
    return transaction;
  }

  public void setTransaction(InternalTransaction transaction) {
    this.transaction = transaction;
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