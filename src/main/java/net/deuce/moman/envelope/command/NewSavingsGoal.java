package net.deuce.moman.envelope.command;

import java.util.Date;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.SavingsGoalsView;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewSavingsGoal extends AbstractSavingsGoalHandler {

	public static final String ID = "net.deuce.moman.envelope.command.newSavingsGoal";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(SavingsGoalsView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
			Envelope goal = ServiceNeeder.instance().getEnvelopeFactory().newEntity(
				envelopeService.getNextIndex(),
				"Set Name", Frequency.MONTHLY, 0.0, null, true, false, true, 1);
			goal.setSavingsGoalDate(new Date());
			envelopeService.addEnvelope(goal, envelopeService.getSavingsGoalsEnvelope());
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
