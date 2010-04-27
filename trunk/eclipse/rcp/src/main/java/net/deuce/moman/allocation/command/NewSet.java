package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AllocationSetFactory;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.income.ui.SelectPaySourceDialog;
import net.deuce.moman.operation.CreateEntityOperation;

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

	private IncomeService incomeService = ServiceProvider.instance().getIncomeService();

	private AllocationSetService allocationSetService = ServiceProvider.instance().getAllocationSetService();

	private AllocationSetFactory allocationSetFactory = ServiceProvider.instance().getAllocationSetFactory();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (incomeService.getEntities().size() > 0) {
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			SelectPaySourceDialog dialog = new SelectPaySourceDialog(window
					.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getPages()[0].showView(AllocationView.ID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
					AllocationSet allocationSet = allocationSetFactory
							.newEntity("Set Name", dialog.getEntity());
					new CreateEntityOperation<AllocationSet, AllocationSetService>(
							allocationSet, allocationSetService).execute();
				} catch (Exception e) {
					e.printStackTrace();
					throw new ExecutionException(e.getMessage(), e);
				}
			}
		}
		return null;
	}

}
