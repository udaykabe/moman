package net.deuce.moman.transaction.ui;

import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.DeleteRepeating;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class RepeatingTransactionView extends AbstractEntityTableView<RepeatingTransaction> {
	
	public static final String ID = RepeatingTransactionView.class.getName();
	
	public RepeatingTransactionView() {
		super(ServiceNeeder.instance().getRepeatingTransactionService());
	}

	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new RepeatingTransactionEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new RepeatingTransactionEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(
 	    	new TransactionEnvelopeSelectionEditingSupport(tableViewer, null, tableViewer.getTable()));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RepeatingTransactionEditingSupport(tableViewer, 3));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Frequency");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RepeatingTransactionEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Count");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RepeatingTransactionEditingSupport(tableViewer, 5));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Next Due Date");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new RepeatingTransactionDateSelectionEditingSupport(tableViewer, tableViewer.getTable()));
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new RepeatingTransactionLabelProvider());
	    return tableViewer;
	}
	
	@Override
	protected int getNewEntitySelectionColumn() {
		return 1;
	}

	@Override
	protected String getDeleteCommandId() {
		return DeleteRepeating.ID;
	}

}
