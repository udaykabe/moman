package net.deuce.moman.income.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.income.model.Income;
import net.deuce.moman.income.service.IncomeService;
import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractHandler {
	
	public static final String ID = "net.deuce.moman.income.command.delete";
	
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		ISelection selection = ServiceNeeder.instance().getIncomeService().getViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Income)ss.getFirstElement()).getName() + "' pay source?";
		} else {
			msg = ss.size() + " pay source instances?";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Pay Source?",
				"Are you sure you want to delete the " + msg)) {
			
			IncomeService incomeService = ServiceNeeder.instance().getIncomeService();
			ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();
			List<String> ids = serviceContainer.startQueuingNotifications();
			try {
				Iterator<Income> itr = ss.iterator();
				while (itr.hasNext()) {
					Income income = itr.next();
					incomeService.removeEntity(income);
				}
			} finally {
				serviceContainer.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
