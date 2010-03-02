package net.deuce.moman.transaction.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.transaction.service.RepeatingTransactionService;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteRepeating extends AbstractHandler {

	public static final String ID = "net.deuce.moman.transaction.command.deleteRepeating";
	
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		ISelection selection = ServiceNeeder.instance().getRepeatingTransactionService().getViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		if (ss.size() == 1 && ((InternalTransaction)ss.getFirstElement()).isInitialBalance()) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((InternalTransaction)ss.getFirstElement()).getDescription() + "' repeating transaction?";
		} else {
			msg = ss.size() + " repeating transactions";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Repeating Transaction?",
				"Are you sure you want to delete the " + msg)) {
			
			RepeatingTransactionService repeatingTransactionService = ServiceNeeder.instance().getRepeatingTransactionService();
			ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();
			List<String> ids = serviceContainer.startQueuingNotifications();
			try {
				Iterator<RepeatingTransaction> itr = ss.iterator();
				while (itr.hasNext()) {
					RepeatingTransaction transaction = itr.next();
					repeatingTransactionService.removeEntity(transaction);
				}
			} finally {
				serviceContainer.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}