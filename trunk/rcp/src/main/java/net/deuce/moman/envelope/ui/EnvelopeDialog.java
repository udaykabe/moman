package net.deuce.moman.envelope.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.ui.AbstractModelDialog;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EnvelopeDialog extends AbstractModelDialog<Envelope> {

	private Text nameText;
	private Envelope parent;
	private Envelope envelope;

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();

	public EnvelopeDialog(Shell shell) {
		super(shell);
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	public Envelope getParent() {
		return parent;
	}

	public void setParent(Envelope parent) {
		this.parent = parent;
	}

	protected void createTextFields(Composite parent, GridData gridData) {
		nameText = createTextField(parent, gridData, "Name", false);

		if (envelope != null) {
			nameText.setText(envelope.getName());
		}
	}

	protected boolean isValidInput() {
		if (nameText.getText().length() == 0) {
			setErrorMessage("Please enter an envelope name");
			return false;
		}
		return true;
	}

	protected void saveInput() {
		if (envelope == null) {
			envelope = envelopeFactory.newEntity(
					envelopeService.getNextIndex(), null, Frequency.MONTHLY,
					null, null, true, false, true, 0);
		}
		envelope.setName(nameText.getText());
	}

}
