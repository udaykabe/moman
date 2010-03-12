package net.deuce.moman.allocation.command;

import java.util.List;

import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.operation.DeleteEntityOperation;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteSet extends AbstractAllocationSetHandler {

	public static final String ID = "net.deuce.moman.allocation.command.deleteSet";

	private AllocationSetService allocationSetService = ServiceProvider.instance().getAllocationSetService();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public DeleteSet() {
		super(true);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TableViewer viewer = (TableViewer) viewerRegistry
				.getViewer(AllocationView.ALLOCATION_SET_VIEWER_NAME);

		final List<AllocationSet> list = getEntities(window, viewer);

		if (list.size() > 0) {
			String msg;
			if (list.size() == 1) {
				msg = "'" + list.get(0).getName() + "' allocation profile?";
			} else {
				msg = list.size() + " allocation profiles?";
			}
			if (MessageDialog.openQuestion(window.getShell(),
					"Delete Allocation Profile?",
					"Are you sure you want to delete the " + msg)) {

				new DeleteEntityOperation<AllocationSet, AllocationSetService>(
						list, allocationSetService).execute();

			}
		}
		return null;
	}

}
