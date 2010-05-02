package net.deuce.moman.om;

import javax.persistence.*;

@Entity
@Table(name = "Allocation", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Allocation extends AbstractEntity<Allocation> {

  private static final long serialVersionUID = 1L;

  private Boolean enabled = Boolean.TRUE;
  private Double amount = 0.0;
  private Envelope envelope; // TODO service = envelopeService.getRootEnvelope();
  private Double limit = 0.0;
  private AmountType amountType;
  private LimitType limitType;
  private Double proposed = 0.0;
  private Double remainder = 0.0;
  private Integer index;
  private AllocationSet allocationSet;

  public Allocation() {
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "set_id")
  public AllocationSet getAllocationSet() {
    return allocationSet;
  }

  public void setAllocationSet(AllocationSet allocationSet) {
    this.allocationSet = allocationSet;
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
  @Column(name = "idx")
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  @Enumerated
  public AmountType getAmountType() {
    return amountType;
  }

  public void setAmountType(AmountType amountType) {
    this.amountType = amountType;
  }

  @Enumerated
  public LimitType getLimitType() {
    return limitType;
  }

  public void setLimitType(LimitType limitType) {
    this.limitType = limitType;
  }


  public int compareTo(Allocation o) {
    return compare(this, o);
  }


  public int compare(Allocation o1, Allocation o2) {
    return compareObjects(o1.index, o2.index);
  }


  public String toString() {
    return "Allocation(" + index + ")";
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

  @Basic
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Basic
  @Column(name = "_limit")
  public Double getLimit() {
    return limit;
  }

  public void setLimit(Double limit) {
    this.limit = limit;
  }

  @Basic
  public Double getProposed() {
    return proposed;
  }

  public void setProposed(Double proposed) {
    this.proposed = proposed;
  }

  @Basic
  public Double getRemainder() {
    return remainder;
  }

  public void setRemainder(Double remainder) {
    this.remainder = remainder;
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
