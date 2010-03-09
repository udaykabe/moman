package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.command.DeleteSavingsGoal;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SavingsGoalsView extends AbstractEntityTableView<Envelope> {
	
	public static final String ID = SavingsGoalsView.class.getName();
	
	public SavingsGoalsView() {
		super(ServiceNeeder.instance().getEnvelopeService());
	}
	
	@Override
	protected boolean isSettingServiceViewer() {
		return false;
	}
	
	private EnvelopeService getEnvelopeService() {
		return (EnvelopeService)super.getService();
	}

	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		tableViewer.setComparator(new SavingsGoalViewerComparator());
		getEnvelopeService().setSavingsGoalViewer(tableViewer);
		
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Name");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Due Date");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new SavingsGoalDateSelectionEditingSupport(tableViewer, tableViewer.getTable()));
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new EnvelopeListContentProvider());
	    tableViewer.setLabelProvider(new SavingsGoalLabelProvider());
		return tableViewer;
	}
	
	@Override
	protected int getNewEntitySelectionColumn() {
		return 1;
	}
	
	@Override
	protected String getDeleteCommandId() {
		return DeleteSavingsGoal.ID;
	}

	@Override
	protected List<Envelope> getEntities() {
		return getEnvelopeService().getOrderedSavingsGoals(false);
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		super.entityAdded(event);
		
		StructuredSelection selection = (StructuredSelection)getViewer().getSelection();
		if (selection.size() == 0) return;
		
	}
	
}
