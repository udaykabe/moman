package net.deuce.moman.envelope.command;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.BillView;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.envelope.command.newBill";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(BillView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
			Envelope bill = ServiceNeeder.instance().getEnvelopeFactory().newEntity(
				envelopeService.getNextIndex(),
				"Set Name", Frequency.MONTHLY, null, null, true, false, true, 1);
			envelopeService.addEnvelope(bill, envelopeService.getMonthlyEnvelope());
		
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}