package net.deuce.moman.envelope.ui;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.demo.SWTViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class EnvelopeSpendingView extends ViewPart implements EntityListener<Envelope> {
	
	public static final String ID = EnvelopeSpendingView.class.getName();
	
	private EnvelopeService envelopeService;
	private SWTViewer viewer;

	public EnvelopeSpendingView() {
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		envelopeService.addEntityListener(this);
	}

	public void createPartControl(final Composite parent) {
		viewer = new SWTViewer(parent, SWT.NONE);
	}
	
	@Override
	public void setFocus() {
		viewer.setFocus();
	}
	
	private void refresh() {
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

}
