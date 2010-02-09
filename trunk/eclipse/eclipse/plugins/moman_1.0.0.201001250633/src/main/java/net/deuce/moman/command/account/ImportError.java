package net.deuce.moman.command.account;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class ImportError extends AbstractAccountHandler {

	public static final String ID = "net.deuce.moman.command.account.importError";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
				"Error communicating with the bank: " + event.getTrigger());
		return null;
	}

}
