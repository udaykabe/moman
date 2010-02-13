package net.deuce.moman.command.file;

import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		if (MessageDialog.openQuestion(window.getShell(), "New File?",
				"Are you sure you want to abandon the current file?")) {
			ServiceNeeder.instance().getServiceContainer().clearCaches();
		}
		return null;
	}

}