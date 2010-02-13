package net.deuce.moman.allocation.command;

import net.deuce.moman.envelope.ui.EnvelopeView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class Allocate extends AbstractHandler {

	public static final String ID = "net.deuce.moman.allocation.command.allocate";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(EnvelopeView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
