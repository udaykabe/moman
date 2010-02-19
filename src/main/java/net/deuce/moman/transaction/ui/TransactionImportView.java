package net.deuce.moman.transaction.ui;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TransactionImportView extends AbstractEntityTableView<InternalTransaction> {
	
	public static final String ID = TransactionImportView.class.getName();
	
	public TransactionImportView() {
		super(ServiceNeeder.instance().getImportService());
	}

	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Matched");
 		column.getColumn().setAlignment(SWT.CENTER);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new TransactionImportEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(341);
 	    column.setEditingSupport(new TransactionImportEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new TransactionEnvelopeSelectionEditingSupport(tableViewer, null, tableViewer.getTable()));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Credit");
 	    column.getColumn().setWidth(87);
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Debit");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransactionImportLabelProvider());
	    return tableViewer;
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

}
