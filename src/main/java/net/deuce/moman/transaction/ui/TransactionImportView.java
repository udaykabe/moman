package net.deuce.moman.transaction.ui;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.envelope.ui.SplitSelectionDialog;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.ui.AbstractEntityTableView;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class TransactionImportView extends AbstractEntityTableView<InternalTransaction> {
	
	public static final String ID = TransactionImportView.class.getName();
	
	public TransactionImportView() {
		super(ServiceNeeder.instance().getImportService());
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
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
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransactionImportLabelProvider());
	    return tableViewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doubleClickHandler(int column,
			final StructuredSelection selection, Shell shell) {
		InternalTransaction transaction = (InternalTransaction)selection.getFirstElement();
		List<Split> split = transaction.getSplit();
		final SplitSelectionDialog dialog = new SplitSelectionDialog(shell, transaction.getAmount(), split);
		
		dialog.setAllowBills(true);
		dialog.create();
		if (dialog.open() == Window.OK) {
			if (split != dialog.getSplit()) {
				BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
					public void run() {
						ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
						try {
							Iterator<InternalTransaction> itr = selection.iterator();
							while (itr.hasNext()) {
								InternalTransaction transaction = itr.next();
							
								transaction.clearSplit();
								
								for (Split item : dialog.getSplit()) {
									transaction.addSplit(item, true);
								}
								
								getViewer().refresh(transaction);
							}
						} finally {
							ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
						}
					}
				});
			}
		}
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{4};
	}

}
