package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class NewSet extends AbstractHandler {

	public static final String ID = "net.deuce.moman.allocation.command.newSet";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0].showView(AllocationView.ID,null,IWorkbenchPage.VIEW_ACTIVATE);
			AllocationSet allocationSet = ServiceNeeder.instance().getAllocationSetFactory().newEntity("Set Name", null);
			new CreateEntityOperation<AllocationSet, AllocationSetService>(
					allocationSet, ServiceNeeder.instance().getAllocationSetService()).execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
