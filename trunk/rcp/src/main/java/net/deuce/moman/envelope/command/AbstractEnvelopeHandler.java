package net.deuce.moman.envelope.command;

import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeView;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractEnvelopeHandler extends AbstractHandler {

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public Envelope getEnvelope(IWorkbenchWindow window) {
		ISelection selection = viewerRegistry.getViewer(
				EnvelopeView.ENVELOPE_VIEWER_NAME).getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		StructuredSelection ss = (StructuredSelection) selection;
		if (ss.size() == 0)
			return null;

		if (ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error",
					"Select only one parent envelope.");
			return null;
		}

		return (Envelope) ss.getFirstElement();
	}

}
