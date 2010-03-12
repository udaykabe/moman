package net.deuce.moman.envelope.command;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.envelope.ui.BillView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.envelope.command.newBill";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(BillView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			Envelope bill = envelopeFactory.newEntity(envelopeService
					.getNextIndex(), "Set Name", Frequency.MONTHLY, null, null,
					true, false, true, 1);
			envelopeService.addEnvelope(bill, envelopeService
					.getMonthlyEnvelope());

		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}
