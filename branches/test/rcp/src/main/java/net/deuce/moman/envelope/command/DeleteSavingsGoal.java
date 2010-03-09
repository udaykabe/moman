package net.deuce.moman.envelope.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteSavingsGoal extends AbstractSavingsGoalHandler {

	public static final String ID = "net.deuce.moman.envelope.command.deleteSavingsGoal";

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		ISelection selection = ServiceNeeder.instance().getEnvelopeService().getSavingsGoalViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Envelope)ss.getFirstElement()).getName() + "' savings goal?";
		} else {
			msg = ss.size() + " savings goals";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Savings Goal?",
				"Are you sure you want to delete the " + msg)) {
			
			List<String> ids = ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
			try {
				Iterator<Envelope> itr = ss.iterator();
				while (itr.hasNext()) {
					Envelope env = itr.next();
					ServiceNeeder.instance().getEnvelopeService().removeEnvelope(env);
				}
			} finally {
				ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}