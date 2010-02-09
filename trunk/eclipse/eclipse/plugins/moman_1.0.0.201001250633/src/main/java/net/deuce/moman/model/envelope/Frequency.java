package net.deuce.moman.model.envelope;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public enum Frequency {
	NONE(Calendar.DATE, 0, "None"),
	WEEKLY(Calendar.DATE, 7, "Weekly"),
	BIWEEKLY(Calendar.DATE, 14, "Bi-Weekly"),
	MONTHLY(Calendar.MONTH, 1, "Monthly"),
	BIMONTHLY(Calendar.MONTH, 2, "Bi-Monthly"),
	QUARTERLY(Calendar.MONTH, 3, "Quarterly"),
	SEMIANNUALLY(Calendar.MONTH, 6, "Semi-Annually"),
	ANNUALLY(Calendar.YEAR, 1, "Annually");

	private static Map<String, Frequency> map = new HashMap<String, Frequency>();

	static {
		for (Frequency f : Frequency.values()) {
			map.put(f.label(), f);
		}
	}

	public static Frequency instance(String name) {
		return map.get(name);
	}

	private int calendarFrequency;
	private int cardinality;
	private String label;

	private Frequency(int calendarFrequency, int cardinality, String label) {
		this.calendarFrequency = calendarFrequency;
		this.cardinality = cardinality;
		this.label = label;
	}

	public String label() {
		return label;
	}

	public int getCalendarFrequency() {
		return calendarFrequency;
	}

	public void setCalendarFrequency(int calendarFrequency) {
		this.calendarFrequency = calendarFrequency;
	}

	public int getCardinality() {
		return cardinality;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	public Date calculateNextDate(Date date) {
		if (this == NONE) return date;
		
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		c.add(calendarFrequency, cardinality);
		return c.getTime();
	}
}
