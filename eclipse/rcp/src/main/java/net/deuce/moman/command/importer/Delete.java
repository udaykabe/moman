package net.deuce.moman.command.importer;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.transaction.ImportService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.transaction.ui.TransactionImportView;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.import.delete";

	private ImportService importService = ServiceProvider.instance().getImportService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		ISelection selection = viewerRegistry.getViewer(
				TransactionImportView.IMPORT_VIEWER_NAME).getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		StructuredSelection ss = (StructuredSelection) selection;
		if (ss.size() == 0)
			return null;

		String msg;
		if (ss.size() == 1) {
			msg = "'"
					+ ((InternalTransaction) ss.getFirstElement())
							.getDescription() + "' imported transaction?";
		} else {
			msg = ss.size() + " imported transactions";
		}
		if (MessageDialog.openQuestion(window.getShell(),
				"Delete Imported Transaction?",
				"Are you sure you want to delete the " + msg)) {

			List<String> ids = serviceManager.startQueuingNotifications();
			try {
				Iterator<InternalTransaction> itr = ss.iterator();
				while (itr.hasNext()) {
					InternalTransaction transaction = itr.next();
					importService.removeEntity(transaction);
					if (transaction.isMatched()) {
						transactionService.removeEntity(transaction
								.getMatchedTransaction());
					} else {
						transactionService.removeEntity(transaction);
					}
				}
			} finally {
				serviceManager.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
