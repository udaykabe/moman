package net.deuce.moman.envelope.command;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.delete";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);		
		
		if (envelope != null) {
			if (MessageDialog.openQuestion(window.getShell(), "Delete Envelope?",
					"Are you sure you want to delete the '" + envelope.getName() + "' envelope?")) {
				ServiceNeeder.instance().getEnvelopeService().removeEnvelope(envelope);
			}
		}
		return null;
	}

}