package net.deuce.moman.allocation.command;

import java.util.List;

import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.operation.DeleteEntityOperation;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteSet extends AbstractAllocationSetHandler {
	
	public static final String ID = "net.deuce.moman.allocation.command.deleteSet";
	
	public DeleteSet() {
		super(true);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final AllocationSetService service = ServiceNeeder.instance().getAllocationSetService();
		TableViewer viewer = (TableViewer)service.getViewer();
		final List<AllocationSet> list = getEntities(window, viewer);		
		
		if (list.size() > 0) {
			String msg;
			if (list.size() == 1) {
				msg = "'" + list.get(0).getName() + "' allocation profile?";
			} else {
				msg = list.size() + " allocation profiles?";
			}
			if (MessageDialog.openQuestion(window.getShell(), "Delete Allocation Profile?",
					"Are you sure you want to delete the " + msg)) {
				
				new DeleteEntityOperation<AllocationSet, AllocationSetService>(
						list, ServiceNeeder.instance().getAllocationSetService()).execute();
				
			}
		}
		return null;
	}

}
