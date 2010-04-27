package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "Split", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Split extends AbstractEntity<Split> {

  private Envelope envelope;
  private Double amount;
  private InternalTransaction transaction;

  public Split(Envelope envelope, Double amount) {
    super();
    this.envelope = envelope;
    this.amount = amount;
  }

  @ManyToOne
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


  @ManyToOne
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


  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((envelope == null) ? 0 : envelope.hashCode());
    return result;
  }


  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Split other = (Split) obj;
    if (envelope == null) {
      if (other.envelope != null)
        return false;
    } else if (!envelope.equals(other.envelope))
      return false;
    return true;
  }


  public int compare(Split o1, Split o2) {
    return o1.compareTo(o2);
  }


  public int compareTo(Split o) {
    return envelope.compareTo(o.getEnvelope());
  }


}
