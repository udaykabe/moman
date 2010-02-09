package net.deuce.moman.model.fi;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class FinanacialInstitutionTextContentAdapter extends TextContentAdapter {

	@Override
	public String getControlContents(Control control) {
		String contents = super.getControlContents(control);
		return contents;
	}

	@Override
	public int getCursorPosition(Control control) {
		return super.getCursorPosition(control);
	}

	@Override
	public Rectangle getInsertionBounds(Control control) {
		return super.getInsertionBounds(control);
	}

	@Override
	public Point getSelection(Control control) {
		return super.getSelection(control);
	}

	@Override
	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		((Text)control).setText(text);
	}

	@Override
	public void setControlContents(Control control, String text,
			int cursorPosition) {
		super.setControlContents(control, text, cursorPosition);
	}

	@Override
	public void setCursorPosition(Control control, int position) {
		super.setCursorPosition(control, position);
	}

	@Override
	public void setSelection(Control control, Point range) {
		super.setSelection(control, range);
	}

	
}
