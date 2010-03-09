package net.deuce.moman.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.util.DataDateRange;

public enum DateRange {

	currentMonth("Current Month", Calendar.MONTH, 1, true),
	lastMonth("Last Month", Calendar.MONTH, 1, false),
	last6Months("Last 6 Months", Calendar.MONTH, 6, true),
	last12Months("Last 12 Months", Calendar.MONTH, 12, true),
	currentYear("Current Year", Calendar.YEAR, 1, true),
	lastYear("Last Year", Calendar.YEAR, 1, false);
	
	private static DateFormat MONTH_FORMAT = new SimpleDateFormat("MMM yyyy");
	private static DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

	private String label;
	private int calendarField;
	private int cardinality;
	private boolean current;
	private Date startDate;
	private Date endDate;
	
	private DateRange(String label, int calendarField, int cardinality, boolean current) {
		this.label = label;
		this.calendarField = calendarField;
		this.cardinality = cardinality;
		this.current = current;
	}
	
	private DateRange(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String label() { return label; }
	public Date startDate() { 
		if (startDate == null) {
			calcDateRanges();
		}
		return startDate;
	}
	public Date endDate() {
		if (endDate == null) {
			calcDateRanges();
		}
		return endDate;
	}
	
	public void calcDateRanges() {
		GregorianCalendar cal = new GregorianCalendar();
		
		if (current) {
			endDate = cal.getTime();
			
			if (cardinality > 1) {
				cal.add(calendarField, -(cardinality-1));
			}
			
			switch (calendarField) {
			case Calendar.MONTH:
				cal.set(Calendar.DAY_OF_MONTH, 1);
				break;
			case Calendar.YEAR:
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.MONTH, 0);
				break;
			}
			startDate = cal.getTime();
		} else {
			cal.add(calendarField, -1);
			
			if (calendarField == Calendar.MONTH) {
				switch (cal.get(Calendar.MONTH)) {
				case Calendar.JANUARY:
				case Calendar.MARCH:
				case Calendar.MAY:
				case Calendar.JULY:
				case Calendar.AUGUST:
				case Calendar.OCTOBER:
				case Calendar.DECEMBER:
					cal.set(Calendar.DAY_OF_MONTH, 31);
					break;
				case Calendar.APRIL:
				case Calendar.JUNE:
				case Calendar.SEPTEMBER:
				case Calendar.NOVEMBER:
					cal.set(Calendar.DAY_OF_MONTH, 30);
					break;
				case Calendar.FEBRUARY:
					if (cal.isLeapYear(cal.get(Calendar.YEAR))) {
						cal.set(Calendar.DAY_OF_MONTH, 29);
					} else {
						cal.set(Calendar.DAY_OF_MONTH, 28);
					}
					break;
				}
				endDate = cal.getTime();
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startDate = cal.getTime();
			} else {
				cal.set(Calendar.MONTH, 11);
				cal.set(Calendar.DAY_OF_MONTH, 31);
				endDate = cal.getTime();
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				startDate = cal.getTime();
			}
		}
	}
	
	public List<String> chartLabels() {
		List<String> list = new LinkedList<String>();
		Calendar cal = new GregorianCalendar();
		cal.setTime(startDate());
		
		while (cal.getTime().before(endDate)) {
			if (calendarField == Calendar.MONTH) {
				list.add(MONTH_FORMAT.format(cal.getTime()));
			} else {
				list.add(YEAR_FORMAT.format(cal.getTime()));
			}
			cal.add(calendarField, 1);
		}
		return list;
	}
	
	public List<DataDateRange> dataDateRanges() {
		List<DataDateRange> list = new LinkedList<DataDateRange>();
		Calendar cal = new GregorianCalendar();
		cal.setTime(startDate());
		Date startDate;
		
		while (cal.getTime().before(endDate)) {
			startDate = cal.getTime();
			cal.add(calendarField, 1);
			cal.add(Calendar.DATE, -1);
			list.add(new DataDateRange(startDate, cal.getTime()));
			cal.add(Calendar.DATE, 1);
		}
		return list;
	}
	
	public static void main(String[] args) {
		for (DateRange dateRange : DateRange.values()) {
			dateRange.calcDateRanges();
			System.out.println(dateRange.label() + ": " + Constants.SHORT_DATE_FORMAT.format(dateRange.startDate()) + " -> " + 
					Constants.SHORT_DATE_FORMAT.format(dateRange.endDate()) + " - " + dateRange.dataDateRanges());
		}
	}
}
