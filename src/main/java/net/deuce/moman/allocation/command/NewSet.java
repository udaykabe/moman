package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.income.ui.SelectPaySourceDialog;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewSet extends AbstractHandler {

	public static final String ID = "net.deuce.moman.allocation.command.newSet";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if (ServiceNeeder.instance().getIncomeService().getEntities().size() > 0) {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			SelectPaySourceDialog dialog = new SelectPaySourceDialog(window.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(AllocationView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
					AllocationSet allocationSet = ServiceNeeder.instance().getAllocationSetFactory().newEntity("Set Name", dialog.getEntity());
					new CreateEntityOperation<AllocationSet, AllocationSetService>(
							allocationSet, ServiceNeeder.instance().getAllocationSetService()).execute();
				} catch (Exception e) {
					e.printStackTrace();
					throw new ExecutionException(e.getMessage(), e);
				}
			}
		}
		return null;
	}

}
