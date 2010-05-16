package net.deuce.moman.om;

import net.deuce.moman.util.CalendarUtil;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "transaction", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class RepeatingTransaction extends InternalTransaction {

  private static final long serialVersionUID = 1L;

  private Boolean enabled;
  private Date dateDue;
  private Date originalDateDue;
  private Frequency frequency;
  private Integer count;

  public RepeatingTransaction() {
    super();
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
  public Date getOriginalDateDue() {
    return originalDateDue;
  }

  public void setOriginalDateDue(Date originalDateDue) {
    this.originalDateDue = originalDateDue;
  }

	@Enumerated
  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  @Basic
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  private void resetDateDueIfNecessary() {
    Calendar now = CalendarUtil.today();
    Calendar next = CalendarUtil.convertToCalendar(dateDue);

    if (now.after(next)) {
      while (now.after(next)) {
        frequency.advanceCalendar(next, false);
      }
      setDateDue(next.getTime());
    }
  }

  @Temporal(TemporalType.DATE)
  public Date getDateDue() {
    resetDateDueIfNecessary();
    return dateDue;
  }

  public void setDateDue(Date dateDue) {
    this.dateDue = dateDue;
  }

  public int compare(RepeatingTransaction o1, RepeatingTransaction o2) {
    int dateCompare = compareObjects(o1.dateDue, o2.dateDue);

    if (dateCompare == 0) {
      return compareObjects(o1.getDescription(), o2.getDescription());
    }
    return dateCompare;
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RepeatingTransaction it = (RepeatingTransaction) o;

    return !(getId() != null ? !getId().equals(it.getId()) : it.getId() != null);

  }


  public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
  }

}
