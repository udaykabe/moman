package net.deuce.moman.command.account;

import net.deuce.moman.rcp.account.AccountView;
import net.deuce.moman.rcp.account.NewAccountWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.account.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NewAccountWizard wizard = new NewAccountWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		try {
			dialog.open();
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(AccountView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException(e.getMessage(), e);
		}
		/*
		AccountDialog dialog = new AccountDialog(window.getShell());
		dialog.create();
		if (dialog.open() == Window.OK) {
			Registry.instance().addAccount(dialog.getAccount());
		}
		*/
		return null;
	}

}
