package org.eclipse.swt.widgets;

import net.deuce.moman.Constants;
import net.deuce.moman.report.DateRange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class DateRangeCombo extends Combo {
	
	private DateRange dateRange;

	public DateRangeCombo(Composite parent) {
		this(parent, DateRange.currentMonth);
	}
	
	public DateRangeCombo(Composite parent, DateRange dateRange) {
		super(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		setFont(Constants.COMBO_FONT);
		
		this.dateRange = dateRange;
		
		int i = 0;
		int index = 0;
		for (DateRange dr : DateRange.values()) {
            add(dr.name());
            if (dr == dateRange) {
            	index = i;
            }
        }
		
		select(index);
		
		addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DateRangeCombo.this.dateRange = DateRange.values()[DateRangeCombo.this.getSelectionIndex()];
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		setSize(152, 54);
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

	/*
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point p = super.computeSize(wHint, hHint, changed);
		int minHeight = 20*2 + getFont().getFontData()[0].getHeight();
		System.out.println("ZZZ p.y: " + p.y);
		p.y = Math.max(minHeight, p.y);
		System.out.println("ZZZ p.y2: " + p.y);
		return p;
	}
	*/

	
}
