package net.deuce.moman.util;

import java.util.Date;

import net.deuce.moman.RcpConstants;

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

	public String toString() {
		return "DataDateRange [startDate="
				+ RcpConstants.SHORT_DATE_FORMAT.format(startDate)
				+ ", endDate=" + RcpConstants.SHORT_DATE_FORMAT.format(endDate)
				+ "]";
	}

}
