package net.deuce.moman.envelope.command;

import java.util.Iterator;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.envelope.command.deleteBill";

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		ISelection selection = ServiceNeeder.instance().getEnvelopeService().getBillViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Envelope)ss.getFirstElement()).getName() + "' bill?";
		} else {
			msg = ss.size() + " bills";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Bill?",
				"Are you sure you want to delete the " + msg)) {
			
			ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
			try {
				Iterator<Envelope> itr = ss.iterator();
				while (itr.hasNext()) {
					Envelope bill = itr.next();
					ServiceNeeder.instance().getEnvelopeService().removeEnvelope(bill);
				}
			} finally {
				ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
			}
		}
		return null;
	}

}