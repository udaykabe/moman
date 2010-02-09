package net.deuce.moman.command.envelope;

import java.util.Iterator;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteBill extends AbstractBillHandler {

	public static final String ID = "net.deuce.moman.command.bill.delete";

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		TableViewer viewer = Registry.instance().getBillViewer();
		ISelection selection = viewer.getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		String msg;
		if (ss.size() == 1) {
			msg = "'" + ((Bill)ss.getFirstElement()).getName() + "' bill?";
		} else {
			msg = ss.size() + " bills";
		}
		if (MessageDialog.openQuestion(window.getShell(), "Delete Bill?",
				"Are you sure you want to delete the " + msg)) {
			
			Registry.instance().setMonitor(false);
			try {
				Iterator<Bill> itr = ss.iterator();
				while (itr.hasNext()) {
					Bill bill = itr.next();
					Registry.instance().removeBill(bill);
				}
			} finally {
				Registry.instance().setMonitor(true);
				Registry.instance().notifyBillListenersOfRemovals();
			}
		}
		return null;
	}

}