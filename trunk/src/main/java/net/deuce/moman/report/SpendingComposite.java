package net.deuce.moman.report;

import java.util.Collection;

import net.deuce.moman.report.AbstractEnvelopeReportPieCanvas.EnvelopeSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateRangeCombo;
import org.eclipse.swt.widgets.Label;

public class SpendingComposite extends Composite {
	
	private Composite breadCrumbComposite;
	private SpendingCanvas canvas;

	public SpendingComposite(Composite parent, DateRangeCombo combo, int style) {
		super(parent, style);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		setLayout(layout);
		
		Composite breadCrumbs = createBreadCrumbs(this);
		breadCrumbs.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		canvas = buildCanvas(this, combo, style);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createButton(canvas.getTopEnvelopeSource());
	}
	
	protected SpendingCanvas buildCanvas(SpendingComposite parent, DateRangeCombo combo, int style) {
		return new SpendingCanvas(this, combo, style);
	}
	
	private Composite createBreadCrumbs(Composite parent) {
		breadCrumbComposite = new Composite(parent, SWT.NONE);
		breadCrumbComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		return breadCrumbComposite;
	}
	
	private Button createButton(final EnvelopeSource source) {
		final Button button = new Button(breadCrumbComposite, SWT.PUSH | SWT.FLAT);
		String text = source.getLabel();
		button.setText(text);
		button.setData(source);
		button.setToolTipText(text);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getData() != canvas.peekSourceEnvelope()) {
					if (button.getData() != null) {
						while (button.getData() != canvas.peekSourceEnvelope()) {
							canvas.popSourceEnvelope();
						}
					}
					canvas.regenerateChart();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		button.setEnabled(true);
		return button;
	}
	
	private Label createSeparator() {
		Label label = new Label(breadCrumbComposite, SWT.NONE);
		label.setText(">");
		return label;
	}
	
	public void setSourceEnvelopes(Collection<EnvelopeSource> sourceEnvelopes) {
		for (Control item : breadCrumbComposite.getChildren()) {
			item.dispose();
		}
		
		if (sourceEnvelopes != null) {
			for (EnvelopeSource source : sourceEnvelopes) {
				if (source != canvas.getTopEnvelopeSource()) {
					createSeparator();
				}
				createButton(source);
			}
		}
		breadCrumbComposite.layout(true, true);
	}
}
