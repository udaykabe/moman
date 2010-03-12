package net.deuce.moman.rule.command;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.rule.ui.TransactionRuleView;
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

public class Delete extends AbstractHandler {

	public static final String ID = "net.deuce.moman.rule.command.delete";

	private TransactionRuleService transactionRuleService = ServiceProvider.instance().getTransactionRuleService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	@SuppressWarnings("unchecked")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		ISelection selection = viewerRegistry.getViewer(
				TransactionRuleView.TRANSACTION_RULE_VIEWER_NAME)
				.getSelection();
		if (!(selection instanceof StructuredSelection))
			return null;

		StructuredSelection ss = (StructuredSelection) selection;
		if (ss.size() == 0)
			return null;

		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Rule) ss.getFirstElement()).getExpression()
					+ "' rule?";
		} else {
			msg = ss.size() + " rules";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Rule?",
				"Are you sure you want to delete the " + msg)) {

			List<String> ids = serviceManager.startQueuingNotifications();
			try {
				Iterator<Rule> itr = ss.iterator();
				while (itr.hasNext()) {
					Rule rule = itr.next();
					transactionRuleService.removeEntity(rule);
				}
			} finally {
				serviceManager.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
