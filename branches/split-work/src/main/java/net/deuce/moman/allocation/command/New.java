package net.deuce.moman.allocation.command;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AllocationFactory;
import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.allocation.model.AmountType;
import net.deuce.moman.allocation.model.LimitType;
import net.deuce.moman.allocation.operation.CreateAllocationOperation;
import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractAllocationSetHandler {

	public static final String ID = "net.deuce.moman.allocation.command.new";
	
	private EnvelopeService envelopeService;
	private AllocationSetService allocationSetService;
	private AllocationFactory allocationFactory;
	
	public New() {
		super(false);
		this.envelopeService = ServiceNeeder.instance().getEnvelopeService();
		this.allocationSetService = ServiceNeeder.instance().getAllocationSetService();
		this.allocationFactory = ServiceNeeder.instance().getAllocationFactory();
	}

	@Override
	protected String getMultiSelectionMessage() {
		return "Please select only one allocation profile.";
	}
	
	@Override
	protected String getEmptySelectionTitle() {
		return "Select an allocation profile";
	}

	@Override
	protected String getEmptySelectionMessage() {
		return "Please select one allocation profile.";
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		TableViewer viewer = (TableViewer)allocationSetService.getViewer();
		AllocationSet allocationSet = getEntities(window, viewer).get(0);
		
		Allocation allocation = allocationFactory.newEntity(
				allocationSet.getAllocations().size(), true, 0.0, AmountType.FIXED,
				envelopeService.getRootEnvelope(), 0.0, LimitType.NONE);
		
		new CreateAllocationOperation(allocationSet, allocation).execute();
		
		return null;
	}

}
