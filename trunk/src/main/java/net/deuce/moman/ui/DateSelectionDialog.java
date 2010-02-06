package net.deuce.moman.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

public class DateSelectionDialog extends Dialog {
	
	private SWTCalendar calendar;
	private Date date;

	public DateSelectionDialog(Shell parentShell, Date date) {
		super(parentShell);
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return Display.getCurrent().getCursorLocation();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		calendar = new SWTCalendar(parent, SWT.NONE);
		if (date != null) {
			Calendar c = new GregorianCalendar();
			c.setTime(date);
			calendar.setCalendar(c);
		}
		
		date = null;
		
		calendar.addSWTCalendarListener(new SWTCalendarListener () {
			@Override
			public void dateChanged(SWTCalendarEvent event) {
			}

			@Override
			public void dateDoubleClicked(SWTCalendarEvent event) {
				DateSelectionDialog.this.close();
				date = calendar.getCalendar().getTime();
			}

			@Override
			public void escPressed(SWTCalendarEvent event) {
				DateSelectionDialog.this.close();
			}
		});
		
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}
	
}
