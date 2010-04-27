package net.deuce.moman.fi;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class FinanacialInstitutionTextContentAdapter extends TextContentAdapter {


	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		((Text) control).setText(text);
	}

}
