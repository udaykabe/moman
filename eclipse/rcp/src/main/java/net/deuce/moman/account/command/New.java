package net.deuce.moman.account.command;

import net.deuce.moman.account.ui.AccountView;
import net.deuce.moman.account.ui.NewAccountWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.account.command.new";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(AccountView.ID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
			NewAccountWizard wizard = new NewAccountWizard();
			WizardDialog dialog = new WizardDialog(HandlerUtil
					.getActiveShell(event), wizard);
			dialog.open();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
