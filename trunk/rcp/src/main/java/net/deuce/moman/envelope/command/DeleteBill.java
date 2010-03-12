package net.deuce.moman.envelope.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.envelope.ui.BillView;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.envelope.command.deleteBill";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		ISelection selection = viewerRegistry.getViewer(
				BillView.BILL_VIEWER_NAME).getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		StructuredSelection ss = (StructuredSelection) selection;
		if (ss.size() == 0)
			return null;

		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Envelope) ss.getFirstElement()).getName() + "' bill?";
		} else {
			msg = ss.size() + " bills";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Bill?",
				"Are you sure you want to delete the " + msg)) {

			List<String> ids = serviceManager.startQueuingNotifications();
			try {
				Iterator<Envelope> itr = ss.iterator();
				while (itr.hasNext()) {
					Envelope bill = itr.next();
					envelopeService.removeEnvelope(bill);
				}
			} finally {
				serviceManager.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
