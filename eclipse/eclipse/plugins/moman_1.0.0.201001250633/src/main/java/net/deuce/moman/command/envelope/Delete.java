package net.deuce.moman.command.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.command.envelope.delete";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);		
		
		if (envelope != null) {
			if (MessageDialog.openQuestion(window.getShell(), "Delete Envelope?",
					"Are you sure you want to delete the '" + envelope.getName() + "' envelope?")) {
				Registry.instance().removeEnvelope(envelope);
			}
		}
		return null;
	}

}