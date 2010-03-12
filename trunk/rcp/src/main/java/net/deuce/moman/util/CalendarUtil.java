package net.deuce.moman.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarUtil {

	public static void convertCalendarToMidnight(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.AM_PM, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	public static Calendar today() {
		Calendar cal = new GregorianCalendar();
		convertCalendarToMidnight(cal);
		return cal;
	}

	public static Calendar convertToCalendar(Date d) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(d);
		convertCalendarToMidnight(cal);
		return cal;
	}

	public static boolean dateInRange(Date d, DataDateRange ddr) {
		Calendar dCal = convertToCalendar(d);
		Calendar sCal = convertToCalendar(ddr.getStartDate());
		Calendar eCal = convertToCalendar(ddr.getEndDate());
		return dCal.equals(sCal) || dCal.equals(eCal)
				|| (sCal.before(dCal) && dCal.before(eCal));
	}
}
