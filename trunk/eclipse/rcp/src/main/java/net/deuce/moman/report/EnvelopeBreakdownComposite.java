package net.deuce.moman.report;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;

public class EnvelopeBreakdownComposite extends SpendingComposite {

	public EnvelopeBreakdownComposite(Composite parent, DateRangeCombo combo,
			int style) {
		super(parent, combo, style);
	}

	protected SpendingCanvas buildCanvas(SpendingComposite parent,
			DateRangeCombo combo, int style) {
		return new EnvelopeBreakdownCanvas(this, combo, style);
	}

}
