package org.vafada.swtcalendar;


import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTCalendarDialog {
    private Shell shell;
    private SWTCalendar swtcal;
    private Display display;
    private int status = Window.OK;

    public SWTCalendarDialog(Display display) {
        this.display = display;
        shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setLayout(new RowLayout());
        shell.setLocation(Display.getCurrent().getCursorLocation());
        swtcal = new SWTCalendar(shell);
        
        swtcal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					status = Window.CANCEL;
					shell.dispose();
				}
			}
        });
        
        swtcal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				shell.dispose();
			}
        });
    }

    public int open() {
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        return status;
    }
    
    public Calendar getCalendar() {
        return swtcal.getCalendar();
    }

    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        swtcal.setCalendar(calendar);
    }

    public void addDateChangedListener(SWTCalendarListener listener) {
        swtcal.addSWTCalendarListener(listener);
    }

}


