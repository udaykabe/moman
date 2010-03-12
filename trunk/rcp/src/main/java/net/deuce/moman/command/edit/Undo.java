package net.deuce.moman.command.edit;

import net.deuce.moman.ui.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbench;

public class Undo extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.edit.undo";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		final IUndoContext undoContext = workbench.getOperationSupport()
				.getUndoContext();
		final IOperationHistory operHistory = workbench.getOperationSupport()
				.getOperationHistory();
		operHistory.undo(undoContext, new NullProgressMonitor(), null);
		return null;
	}

}
