package net.deuce.moman.allocation.command;

import java.util.List;

import net.deuce.moman.allocation.operation.DeleteAllocationOperation;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.command.AbstractEntityHandler;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractEntityHandler<Allocation> {

	public static final String ID = "net.deuce.moman.allocation.command.delete";

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public Delete() {
		super(true);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		TableViewer viewer = (TableViewer) viewerRegistry
				.getViewer(AllocationView.ALLOCATION_VIEWER_NAME);
		final List<Allocation> list = getEntities(window, viewer);

		if (list.size() > 0) {
			String msg;
			if (list.size() == 1) {
				msg = "allocation?";
			} else {
				msg = list.size() + " allocations?";
			}
			if (MessageDialog.openQuestion(window.getShell(),
					"Delete Allocation?",
					"Are you sure you want to delete the " + msg)) {

				new DeleteAllocationOperation(list).execute();
			}
		}
		return null;
	}

}
