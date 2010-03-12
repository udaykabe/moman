package net.deuce.moman.report;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateRangeCombo;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractReportView extends ViewPart {

	private Control control;
	private DateRangeCombo combo;

	public AbstractReportView() {
	}

	protected abstract Control doCreateChartControl(Composite parent,
			DateRangeCombo combo);

	protected Control createChartControl(Composite parent) {
		control = doCreateChartControl(parent, combo);
		return control;
	}

	protected Control createDateRangeControl(Composite parent) {
		combo = new DateRangeCombo(parent);
		return combo;
	}

	public void createPartControl(final Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		createDateRangeControl(parent);

		Control chartControl = createChartControl(parent);
		chartControl.setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	public void setFocus() {
		control.setFocus();
	}

}
