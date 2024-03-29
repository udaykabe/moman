package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "split", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"}), @UniqueConstraint(columnNames = {"envelope_id", "transaction_id"})})
public class Split extends AbstractEntity<Split> implements UserBasedEntity {

  private Envelope envelope;
  private Double amount;
  private InternalTransaction transaction;
  private User user;

  public Split() {
    super();
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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

  public String toString() {
    return "Split [amount=" + amount + ", envelope=" + envelope + "]";
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Split split = (Split) o;

    if (envelope != null ? !envelope.equals(split.envelope) : split.envelope != null) return false;
    if (transaction != null ? !transaction.equals(split.transaction) : split.transaction != null) return false;

    return true;
  }

  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (envelope != null ? envelope.hashCode() : 0);
    result = 31 * result + (transaction != null ? transaction.hashCode() : 0);
    return result;
  }

  public int compare(Split o1, Split o2) {
    return compareObjects(o1, o2);
  }


  public int compareTo(Split o) {
    return envelope.compareTo(o.getEnvelope());
  }


}
