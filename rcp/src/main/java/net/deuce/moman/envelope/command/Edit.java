package net.deuce.moman.envelope.command;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeDialog;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Edit extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.edit";

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
