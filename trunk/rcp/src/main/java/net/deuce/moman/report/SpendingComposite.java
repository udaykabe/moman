package net.deuce.moman.report;

import java.util.Collection;

import net.deuce.moman.report.AbstractEnvelopeReportPieCanvas.EnvelopeSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateRangeCombo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

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

		createLink(canvas.getTopEnvelopeSource());
	}

	protected SpendingCanvas buildCanvas(SpendingComposite parent,
			DateRangeCombo combo, int style) {
		return new SpendingCanvas(this, combo, style);
	}

	private Composite createBreadCrumbs(Composite parent) {
		breadCrumbComposite = new Composite(parent, SWT.NONE);
		breadCrumbComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		return breadCrumbComposite;
	}

	private Link createLink(final EnvelopeSource source) {
		final Link link = new Link(breadCrumbComposite, SWT.NONE);
		String text = source.getLabel();
		link.setText("<a>" + text + "</a>");
		link.setData(source);
		link.setToolTipText(text);
		link.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (link.getData() != canvas.peekSourceEnvelope()) {
					if (link.getData() != null) {
						while (link.getData() != canvas.peekSourceEnvelope()) {
							canvas.popSourceEnvelope();
						}
					}
					canvas.regenerateChart();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		link.setEnabled(true);
		return link;
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
				createLink(source);
			}
		}
		breadCrumbComposite.layout(true, true);
	}
}
