package net.deuce.moman.envelope.command;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.delete";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);

		if (envelope != null) {
			if (MessageDialog.openQuestion(window.getShell(),
					"Delete Envelope?", "Are you sure you want to delete the '"
							+ envelope.getName() + "' envelope?")) {
				envelopeService.removeEnvelope(envelope);
			}
		}
		return null;
	}

}
