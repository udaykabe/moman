package net.deuce.moman.transaction.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.RepeatingTransaction;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.transaction.RepeatingTransactionService;
import net.deuce.moman.transaction.ui.RepeatingTransactionView;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteRepeating extends AbstractHandler {

	public static final String ID = "net.deuce.moman.transaction.command.deleteRepeating";

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	private RepeatingTransactionService repeatingTransactionService = ServiceProvider.instance().getRepeatingTransactionService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		ISelection selection = viewerRegistry.getViewer(
				RepeatingTransactionView.REPEATING_TRANSACTION_VIEWER_NAME)
				.getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		StructuredSelection ss = (StructuredSelection) selection;
		if (ss.size() == 0)
			return null;

		if (ss.size() == 1
				&& ((InternalTransaction) ss.getFirstElement())
						.isInitialBalance())
			return null;

		String msg;
		if (ss.size() == 1) {
			msg = "'"
					+ ((InternalTransaction) ss.getFirstElement())
							.getDescription() + "' repeating transaction?";
		} else {
			msg = ss.size() + " repeating transactions";
		}
		if (MessageDialog.openQuestion(window.getShell(),
				"Delete Repeating Transaction?",
				"Are you sure you want to delete the " + msg)) {

			List<String> ids = serviceManager.startQueuingNotifications();
			try {
				Iterator<RepeatingTransaction> itr = ss.iterator();
				while (itr.hasNext()) {
					RepeatingTransaction transaction = itr.next();
					repeatingTransactionService.removeEntity(transaction);
				}
			} finally {
				serviceManager.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
