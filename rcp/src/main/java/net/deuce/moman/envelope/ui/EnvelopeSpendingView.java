package net.deuce.moman.envelope.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.ui.demo.SWTViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.springframework.beans.factory.annotation.Autowired;

public class EnvelopeSpendingView extends ViewPart implements
		EntityListener<Envelope> {

	public static final String ID = EnvelopeSpendingView.class.getName();

	private SWTViewer viewer;

    public EnvelopeSpendingView() {
        EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();
        envelopeService.addEntityListener(this);
	}

	public void createPartControl(final Composite parent) {
		viewer = new SWTViewer(parent, SWT.NONE);
	}

	public void setFocus() {
		viewer.setFocus();
	}

	private void refresh() {
	}

	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

}
