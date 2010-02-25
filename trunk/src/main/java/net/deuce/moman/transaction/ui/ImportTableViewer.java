package net.deuce.moman.transaction.ui;

import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ImportTableViewer extends SelectingTableViewer {

	public ImportTableViewer(Composite parent, int style) {
		super(parent, style);
		
		
        TableViewerColumn column = new TableViewerColumn(this, SWT.CENTER);
 		column.getColumn().setText("Matched");
 		column.getColumn().setAlignment(SWT.CENTER);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new TransactionImportEditingSupport(this, 0));
		
 		column = new TableViewerColumn(this, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
		
        column = new TableViewerColumn(this, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
		
 		column = new TableViewerColumn(this, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(341);
 	    column.setEditingSupport(new TransactionImportEditingSupport(this, 2));
		
 		column = new TableViewerColumn(this, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new TransactionEnvelopeSelectionEditingSupport(this, null, getTable()));
		
 		column = new TableViewerColumn(this, SWT.RIGHT);
 		column.getColumn().setText("Credit");
 	    column.getColumn().setWidth(87);
		
 		column = new TableViewerColumn(this, SWT.RIGHT);
 		column.getColumn().setText("Debit");
 	    column.getColumn().setWidth(87);
		
	    setContentProvider(new TransactionContentProvider());
	    setLabelProvider(new TransactionImportLabelProvider());
	}

}
