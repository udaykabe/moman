package net.deuce.moman.allocation.command;

import java.util.List;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.operation.DeleteAllocationOperation;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.command.AbstractEntityHandler;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractEntityHandler<Allocation> {
	
	public static final String ID = "net.deuce.moman.allocation.command.delete";
	
	public Delete() {
		super(true);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final AllocationSetService service = ServiceNeeder.instance().getAllocationSetService();
		TableViewer viewer = (TableViewer)service.getAllocationViewer();
		final List<Allocation> list = getEntities(window, viewer);		
		
		if (list.size() > 0) {
			String msg;
			if (list.size() == 1) {
				msg = "allocation?";
			} else {
				msg = list.size() + " allocations?";
			}
			if (MessageDialog.openQuestion(window.getShell(), "Delete Allocation?",
					"Are you sure you want to delete the " + msg)) {
				
				new DeleteAllocationOperation(list).execute();
			}
		}
		return null;
	}

}
