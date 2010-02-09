package net.deuce.moman.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public enum Frequency {
	NONE(Calendar.DATE, 0, 0, 0, "None"),
	WEEKLY(Calendar.DATE, 7, 4, 52, "Weekly"),
	BIWEEKLY(Calendar.DATE, 14, 2, 26, "Bi-Weekly"),
	MONTHLY(Calendar.MONTH, 1, 1, 12, "Monthly"),
	BIMONTHLY(Calendar.MONTH, 2, .5, 6, "Bi-Monthly"),
	QUARTERLY(Calendar.MONTH, 3, .3333, 4, "Quarterly"),
	SEMIANNUALLY(Calendar.MONTH, 6, .1667, 2, "Semi-Annually"),
	ANNUALLY(Calendar.YEAR, 1, .0833, 1, "Annually");

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
	private double ppm;  // periods per month
	private int ppy; // periods per year

	private Frequency(int calendarFrequency, int cardinality, double ppm, int ppy, String label) {
		this.calendarFrequency = calendarFrequency;
		this.cardinality = cardinality;
		this.label = label;
		this.ppm = ppm;
		this.ppy = ppy;
	}

	public double ppm() { return ppm; }
	
	public int ppy() { return ppy; }
	
	public String label() { return label; }

	public int getCalendarFrequency() { return calendarFrequency; }

	public int getCardinality() { return cardinality; }

	public void advanceCalendar(Calendar c) {
		if (this == NONE) return;
		c.add(calendarFrequency, cardinality);
	}

	public Date calculateNextDate(Date date) {
		if (this == NONE) return date;
		
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		advanceCalendar(c);
		return c.getTime();
	}
}
