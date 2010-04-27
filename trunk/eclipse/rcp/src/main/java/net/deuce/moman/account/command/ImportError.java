package net.deuce.moman.account.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class ImportError extends AbstractHandler {

	public static final String ID = "net.deuce.moman.account.command.importError";

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
				"Error communicating with the bank: " + event.getTrigger());
		return null;
	}

}
