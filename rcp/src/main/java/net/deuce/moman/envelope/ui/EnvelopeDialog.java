package net.deuce.moman.envelope.ui;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractModelDialog;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EnvelopeDialog extends AbstractModelDialog<Envelope> {
	
	private Text nameText;
	private Envelope parent;
	private Envelope envelope;

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

	@Override
	protected void createTextFields(Composite parent, GridData gridData) {
		nameText = createTextField(parent, gridData, "Name", false);
		
		if (envelope != null) {
			nameText.setText(envelope.getName());
		}
	}

	@Override
	protected boolean isValidInput() {
		if (nameText.getText().length() == 0) {
			setErrorMessage("Please enter an envelope name");
			return false;
		}
		return true;
	}

	@Override
	protected void saveInput() {
		if (envelope == null) {
			envelope = ServiceNeeder.instance().getEnvelopeFactory().newEntity(
				ServiceNeeder.instance().getEnvelopeService().getNextIndex(),
				null, Frequency.MONTHLY, null, null, true, false, true, 0);
		}
		envelope.setName(nameText.getText());
	}

}
