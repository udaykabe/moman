package net.deuce.moman.command.transaction;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.rules.Condition;
import net.deuce.moman.model.rules.Rule;
import net.deuce.moman.rcp.transaction.TransactionRuleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewRule extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.transaction.newRule";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Rule rule = new Rule();
		rule.setExpression("Set Exression");
		rule.setCondition(Condition.Contains);
		rule.setConversion("Set Conversion");
		rule.setEnabled(true);
		rule.setEnvelope(Registry.instance().getAvailableEnvelope());
		Registry.instance().addTransactionRule(rule);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(TransactionRulesView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}