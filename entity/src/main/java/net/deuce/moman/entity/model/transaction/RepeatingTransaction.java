package net.deuce.moman.entity.model.transaction;

import java.util.Calendar;
import java.util.Date;

import net.deuce.moman.entity.model.EntityProperty;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.util.CalendarUtil;

public class RepeatingTransaction extends InternalTransaction {

	private static final long serialVersionUID = 1L;

    public enum Properties implements EntityProperty {
        enabled(Boolean.class), originalDateDue(Date.class), dateDue(Date.class),
        frequency(Frequency.class), count(Integer.class);
        
		private Class<?> ptype;
		
		public Class<?> type() { return ptype; }
		
		private Properties(Class<?> ptype) { this.ptype = ptype; }
    }
	
    private Boolean enabled;
	private Date dateDue;
	private Date originalDateDue;
	private Frequency frequency;
	private Integer count;

	public RepeatingTransaction() {
		super();
	}
	
	
	public String getDescription() {
		return super.getDescription();
	}

	
	public void setDescription(String description) {
		super.setDescription(description);
	}

	public boolean isEnabled() {
		return evaluateBoolean(enabled);
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public Date getOriginalDateDue() {
		return originalDateDue;
	}

	public void setOriginalDateDue(Date originalDateDue) {
		if (propertyChanged(this.originalDateDue, originalDateDue)) {
			this.originalDateDue = originalDateDue;
			getMonitor().fireEntityChanged(this);
		}
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		if (propertyChanged(this.frequency, frequency)) {
			this.frequency = frequency;
			getMonitor().fireEntityChanged(this);
		}
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		if (propertyChanged(this.count, count)) {
			this.count = count;
			getMonitor().fireEntityChanged(this);
		}
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

	public Date getDateDue() {
		resetDateDueIfNecessary();
		return dateDue;
	}

	public void setDateDue(Date dateDue) {
		if (propertyChanged(this.dateDue, dateDue)) {
			this.dateDue = dateDue;
			getMonitor().fireEntityChanged(this);
		}
	}
	
	public int compare(RepeatingTransaction o1, RepeatingTransaction o2) {
		int dateCompare = o1.getDateDue().compareTo(o2.getDateDue());
		
		if (dateCompare == 0) {
			return o1.getDescription().compareTo(o2.getDescription());
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
