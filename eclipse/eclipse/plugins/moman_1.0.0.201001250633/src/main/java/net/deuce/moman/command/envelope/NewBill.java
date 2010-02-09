package net.deuce.moman.command.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.rcp.envelope.BillView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class NewBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.command.envelope.newBill";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Bill bill = new Bill();
		bill.setAmount(0);
		bill.setDueDay(1);
		bill.setEditable(true);
		bill.setEnabled(true);
		bill.setFrequency(Frequency.MONTHLY);
		bill.setName("Set Name");
		Registry.instance().addBill(bill, Registry.instance().getRootEnvelope());
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(BillView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}

}