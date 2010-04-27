package net.deuce.moman.envelope.command;

import java.util.Date;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.envelope.ui.SavingsGoalsView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewSavingsGoal extends AbstractSavingsGoalHandler {

	public static final String ID = "net.deuce.moman.envelope.command.newSavingsGoal";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(SavingsGoalsView.ID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
			Envelope goal = envelopeFactory.newEntity(envelopeService
					.getNextIndex(), "Set Name", Frequency.MONTHLY, 0.0, null,
					true, false, true, 1);
			goal.setSavingsGoalDate(new Date());
			envelopeService.addEnvelope(goal, envelopeService
					.getSavingsGoalsEnvelope());
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
