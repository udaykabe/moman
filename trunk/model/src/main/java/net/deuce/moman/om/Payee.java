package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "Payee", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Payee extends AbstractEntity<Payee> implements UserBasedEntity {

  private static final long serialVersionUID = 1L;

  private String description;
  private Envelope envelope;
  private Double amount;
  private User user;

  public Payee() {
    super();
  }

  public int compareTo(Payee o) {
    return compare(this, o);
  }


  public int compare(Payee o1, Payee o2) {
    return compareObjects(o1.description, o2.description);
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
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "envelope_id")
  public Envelope getEnvelope() {
    return envelope;
  }

  public void setEnvelope(Envelope envelope) {
    this.envelope = envelope;
  }

  @Basic
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
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
