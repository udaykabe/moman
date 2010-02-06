package net.deuce.moman.envelope.command;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractEnvelopeHandler extends AbstractHandler {
	
	public Envelope getEnvelope(IWorkbenchWindow window) {
		ISelection selection = ServiceNeeder.instance().getEnvelopeService().getViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		if (ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error", "Select only one parent envelope.");
			return null;
		}
	
		return (Envelope)ss.getFirstElement();
	}

}
