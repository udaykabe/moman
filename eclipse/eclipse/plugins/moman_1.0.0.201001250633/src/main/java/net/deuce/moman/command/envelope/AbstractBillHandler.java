package net.deuce.moman.command.envelope;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractBillHandler extends AbstractHandler {

	public Bill getBill(IWorkbenchWindow window) {
		TableViewer viewer = Registry.instance().getBillViewer();
		ISelection selection = viewer.getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		if (ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error", "Select only one bill.");
			return null;
		}
	
		return (Bill)ss.getFirstElement();
	}

}
