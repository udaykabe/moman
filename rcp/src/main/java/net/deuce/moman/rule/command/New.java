package net.deuce.moman.rule.command;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.rule.Condition;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.model.rule.RuleFactory;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.rule.ui.TransactionRuleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.springframework.beans.factory.annotation.Autowired;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.rule.command.new";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private TransactionRuleService transactionRuleService = ServiceProvider.instance().getTransactionRuleService();

	private RuleFactory ruleFactory = ServiceProvider.instance().getRuleFactory();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(TransactionRuleView.ID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
			Rule rule = ruleFactory.newEntity("Set Expression",
					"Set Conversion", Condition.Contains, envelopeService
							.getUnassignedEnvelope(), true);
			transactionRuleService.addEntity(rule);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
