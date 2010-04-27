package net.deuce.moman.om;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.deuce.moman.util.CalendarUtil;

import org.dom4j.Document;

import javax.persistence.*;

@Entity
@Table(name = "Income", uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
public class Income extends AbstractEntity<Income> {

	private static final long serialVersionUID = 1L;

	private Boolean enabled = Boolean.TRUE;
	private String name;
	private Double amount;
	private Date nextPayday;
	private Frequency frequency;

	public Income() {
		super();
	}
	
	public int compareTo(Income o) {
		return compare(this, o);
	}

	
	public int compare(Income o1, Income o2) {
		return o1.name.compareTo(o2.name);
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
			this.name = name;
	}
	
	private void resetNextPaydayIfNecessary() {
		Calendar now = CalendarUtil.today();
		Calendar next = CalendarUtil.convertToCalendar(nextPayday);
		
		if (now.after(next)) {
			while (now.after(next)) {
				frequency.advanceCalendar(next, false);
			}
			setNextPayday(next.getTime());
		}
	}

  @Temporal(TemporalType.DATE)
	public Date getNextPayday() {
		resetNextPaydayIfNecessary();
		return nextPayday;
	}

	public void setNextPayday(Date nextPayday) {
			this.nextPayday = nextPayday;
	}

  @Basic
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
			this.amount = amount;
	}

  @Enumerated
	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
			this.frequency = frequency;
	}

	public int calcPaycheckCountUntilDate(Date d) {
		Frequency freq = getFrequency();
		Calendar cal = new GregorianCalendar();
		cal.setTime(getNextPayday());
		int count = 0;
		while (d.after(cal.getTime())) {
			count++;
			cal.add(freq.getCalendarFrequency(), freq.getCardinality());
		}
		
		return count;
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
