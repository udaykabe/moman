package net.deuce.moman.rule.command;

import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.rule.model.Condition;
import net.deuce.moman.rule.model.Rule;
import net.deuce.moman.rule.service.TransactionRuleService;
import net.deuce.moman.rule.ui.TransactionRuleView;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.rule.command.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(TransactionRuleView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
			TransactionRuleService ruleService = ServiceNeeder.instance().getTransactionRuleService();
			Rule rule = ServiceNeeder.instance().getRuleFactory().newEntity(
				"Set Expression", "Set Conversion", Condition.Contains, 
				envelopeService.getUnassignedEnvelope(), true);
			ruleService.addEntity(rule);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}