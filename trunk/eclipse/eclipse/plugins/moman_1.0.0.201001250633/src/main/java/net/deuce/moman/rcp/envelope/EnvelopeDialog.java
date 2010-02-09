package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.rcp.AbstractModelDialog;

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
			envelope = new Envelope();
			envelope.setEditable(true);
		}
		envelope.setName(nameText.getText());
	}

}
