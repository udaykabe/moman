package net.deuce.moman.rule.command;

import java.util.Iterator;

import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.rule.service.TransactionRuleService;
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

	public static final String ID = "net.deuce.moman.rule.command.delete";

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		ISelection selection = ServiceNeeder.instance().getTransactionRuleService().getViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Rule)ss.getFirstElement()).getExpression() + "' rule?";
		} else {
			msg = ss.size() + " rules";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Rule?",
				"Are you sure you want to delete the " + msg)) {
			
			ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();
			TransactionRuleService ruleService = ServiceNeeder.instance().getTransactionRuleService();
			serviceContainer.startQueuingNotifications();
			try {
				Iterator<Rule> itr = ss.iterator();
				while (itr.hasNext()) {
					Rule rule = itr.next();
					ruleService.removeEntity(rule);
				}
			} finally {
				serviceContainer.stopQueuingNotifications();
			}
		}
		return null;
	}

}