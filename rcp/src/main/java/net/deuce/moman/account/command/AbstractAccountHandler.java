package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class AbstractAccountHandler extends AbstractHandler {
	
	private boolean multiSelection = false;
	
	public AbstractAccountHandler(boolean multiSelection) {
		this.multiSelection = multiSelection;
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> getAccounts(IWorkbenchWindow window) {
		ISelection selection = ServiceNeeder.instance().getAccountService().getViewer().getSelection();
		if (!(selection instanceof StructuredSelection)) return null;
		
		StructuredSelection ss = (StructuredSelection)selection;
		if (ss.size() == 0) return ss.toList();
		
		if (!multiSelection && ss.size() > 1) {
			MessageDialog.openError(window.getShell(), "Error", "Select only one account.");
			return null;
		}
	
		return ss.toList();
	}

}
