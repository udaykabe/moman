package net.deuce.moman.transaction.ui;

import java.util.List;

import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TransferComposite extends TransactionComposite {

	public TransferComposite(Composite parent, int style) {
		super(parent, false, false, true, style);
	}

	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(341);
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransferLabelProvider());
		return tableViewer;
	}

	@Override
	protected List<InternalTransaction> getEntities() {
		return getService().getRegisterTransactions(false, true);
	}

}
