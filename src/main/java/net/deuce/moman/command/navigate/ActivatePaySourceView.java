package net.deuce.moman.command.navigate;

import net.deuce.moman.income.ui.IncomeView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivatePaySourceView extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.navigate.paySource";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(IncomeView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new ExecutionException("show view failed", e);
		}		return null;
	}

}