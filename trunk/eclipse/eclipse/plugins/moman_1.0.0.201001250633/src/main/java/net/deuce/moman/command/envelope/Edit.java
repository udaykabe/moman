package net.deuce.moman.command.envelope;

import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.rcp.envelope.EnvelopeDialog;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Edit extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.command.envelope.edit";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);
		
		if (envelope != null) {
			EnvelopeDialog dialog = new EnvelopeDialog(window.getShell());
			dialog.setEnvelope(envelope);
			dialog.create();
			dialog.open();
		}
		return null;
	}

}
