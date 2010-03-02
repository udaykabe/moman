package net.deuce.moman.income.ui;

import net.deuce.moman.income.command.Delete;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class IncomeView extends AbstractEntityTableView<Income> {
	
	public static final String ID = IncomeView.class.getName();
	
	public IncomeView() {
		super(ServiceNeeder.instance().getIncomeService());
	}
	
	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
 		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Name");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Frequency");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new IncomeEditingSupport(tableViewer, 3));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Next Payday");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new IncomeDateSelectionEditingSupport(tableViewer, tableViewer.getTable()));
 	    
	    tableViewer.setContentProvider(new IncomeContentProvider());
	    tableViewer.setLabelProvider(new IncomeLabelProvider());
		return tableViewer;
	}
	
	@Override
	protected int getNewEntitySelectionColumn() {
		return 1;
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

}
