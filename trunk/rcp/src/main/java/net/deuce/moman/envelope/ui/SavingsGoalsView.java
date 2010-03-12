package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.envelope.command.DeleteSavingsGoal;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsGoalsView extends AbstractEntityTableView<Envelope> {

	public static final String ID = SavingsGoalsView.class.getName();

	public static final String SAVINGS_GOAL_VIEWER_NAME = "savingsGoal";

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public SavingsGoalsView() {
		super();
	}

	protected String getViewerName() {
		return SAVINGS_GOAL_VIEWER_NAME;
	}

	protected EntityService<Envelope> getService() {
		return envelopeService;
	}

	protected boolean isSettingServiceViewer() {
		return false;
	}

	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent,
				SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setComparator(new SavingsGoalViewerComparator());

		TableViewerColumn column = new TableViewerColumn(tableViewer,
				SWT.CENTER);
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
		column.setEditingSupport(new SavingsGoalDateSelectionEditingSupport(
				tableViewer, tableViewer.getTable()));

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		tableViewer.setContentProvider(new EnvelopeListContentProvider());
		tableViewer.setLabelProvider(new SavingsGoalLabelProvider());
		return tableViewer;
	}

	protected int getNewEntitySelectionColumn() {
		return 1;
	}

	protected String getDeleteCommandId() {
		return DeleteSavingsGoal.ID;
	}

	protected List<Envelope> getEntities() {
		return envelopeService.getOrderedSavingsGoals(false);
	}

	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

}
