package net.deuce.moman.entity.model.income;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityProperty;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.util.CalendarUtil;

import org.dom4j.Document;

public class Income extends AbstractEntity<Income> {

	private static final long serialVersionUID = 1L;

    public enum Properties implements EntityProperty {
        enabled(Boolean.class), name(String.class), amount(Double.class),
        nextPayday(Date.class), frequency(Frequency.class);
        
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
    }

	private Boolean enabled = Boolean.TRUE;
	private String name;
	private Double amount;
	private Date nextPayday;
	private Frequency frequency;

	public Income() {
		super();
	}
	
	
	public Document toXml() {
		return buildXml(Properties.values());
	}
	
	
	public int compareTo(Income o) {
		return compare(this, o);
	}

	
	public int compare(Income o1, Income o2) {
		return o1.name.compareTo(o2.name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (propertyChanged(this.name, name)) {
			this.name = name;
			getMonitor().fireEntityChanged(this);
		}
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

	public Date getNextPayday() {
		resetNextPaydayIfNecessary();
		return nextPayday;
	}

	public void setNextPayday(Date nextPayday) {
		if (propertyChanged(this.nextPayday, nextPayday)) {
			this.nextPayday = nextPayday;
			getMonitor().fireEntityChanged(this);
		}
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		if (propertyChanged(this.amount, amount)) {
			this.amount = amount;
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
}
