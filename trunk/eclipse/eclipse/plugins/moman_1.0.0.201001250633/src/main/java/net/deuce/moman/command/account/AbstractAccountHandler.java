package net.deuce.moman.command.account;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.account.Account;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractAccountHandler extends AbstractHandler {

	public Account getAccount(IWorkbenchWindow window) {
		TableViewer viewer = Registry.instance().getAccountViewer();
		ISelection selection = viewer.getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return null;
		
		if (ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error", "Select only one account.");
			return null;
		}
	
		return (Account)ss.getFirstElement();
	}

}
