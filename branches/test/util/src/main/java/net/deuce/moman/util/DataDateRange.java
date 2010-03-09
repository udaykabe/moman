package net.deuce.moman.util;

import java.util.Date;

public class DataDateRange {

	private Date startDate;
	private Date endDate;

	public DataDateRange(Date startDate, Date endDate) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "DataDateRange [startDate=" + Constants.SHORT_DATE_FORMAT.format(startDate) + ", endDate=" + Constants.SHORT_DATE_FORMAT.format(endDate)
				+ "]";
	}
	
}
