package net.deuce.moman.command.navigate;

import net.deuce.moman.rule.ui.TransactionRuleView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateImportRuleView extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.navigate.importRule";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(TransactionRuleView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new ExecutionException("show view failed", e);
		}		return null;
	}

}