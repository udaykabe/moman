package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.operation.CreateAllocationOperation;
import net.deuce.moman.allocation.ui.AllocationView;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationFactory;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.ui.ViewerRegistry;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractAllocationSetHandler {

	public static final String ID = "net.deuce.moman.allocation.command.new";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private AllocationFactory allocationFactory = ServiceProvider.instance().getAllocationFactory();

	private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public New() {
		super(false);
	}

	protected String getMultiSelectionMessage() {
		return "Please select only one allocation profile.";
	}

	protected String getEmptySelectionTitle() {
		return "Select an allocation profile";
	}

	protected String getEmptySelectionMessage() {
		return "Please select one allocation profile.";
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TableViewer viewer = (TableViewer) viewerRegistry
				.getViewer(AllocationView.ALLOCATION_SET_VIEWER_NAME);
		AllocationSet allocationSet = getEntities(window, viewer).get(0);

		Allocation allocation = allocationFactory.newEntity(allocationSet
				.getAllocations().size(), true, 0.0, AmountType.FIXED,
				envelopeService.getRootEnvelope(), 0.0, LimitType.NONE);

		new CreateAllocationOperation(allocationSet, allocation).execute();

		return null;
	}

}
