package net.deuce.moman.account.command;

import net.deuce.moman.transaction.ui.ReconcileDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Reconcile extends AbstractHandler {

	public static final String ID = "net.deuce.moman.account.command.reconcile";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		ReconcileDialog dialog = new ReconcileDialog(window.getShell());

		try {
			dialog.create();
			dialog.getShell().setSize(1000, 600);
			dialog.open();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

}
