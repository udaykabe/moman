package net.deuce.moman.transaction.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.deuce.moman.envelope.ui.SplitSelectionDialog;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.DeleteRepeating;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.DateSelectionDialog;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class RepeatingTransactionView extends AbstractEntityTableView<RepeatingTransaction> {
	
	public static final String ID = RepeatingTransactionView.class.getName();
	
	public RepeatingTransactionView() {
		super(ServiceNeeder.instance().getRepeatingTransactionService());
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
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
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new RepeatingTransactionLabelProvider());
	    return tableViewer;
	}

	@Override
	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
		switch (column) {
		case 6:
			handleDateDoubleClicked(selection, shell);
			break;
		case 2:
			handleEnvelopeDoubleClicked(selection, shell);
			break;
		}
	}
	
	private void handleDateDoubleClicked(StructuredSelection selection, Shell shell) {
		RepeatingTransaction transaction = (RepeatingTransaction)selection.getFirstElement();
		
		DateSelectionDialog dialog = new DateSelectionDialog(shell, transaction.getDate());
		dialog.open();
		Date date = dialog.getDate();
        if (date != null) {
        	transaction.setDate(date, true);
        }
	}
	
	private void handleEnvelopeDoubleClicked(final StructuredSelection selection, Shell shell) {
		InternalTransaction transaction = (InternalTransaction)selection.getFirstElement();
		List<Split> split = transaction.getSplit();
		final SplitSelectionDialog dialog = new SplitSelectionDialog(shell, transaction.getAmount(), split);
		
		dialog.setAllowBills(true);
		dialog.create();
		int status = dialog.open();
		final List<Split> result = dialog.getSplit();
		if (status == Window.OK) {
			if (!split.equals(result)) {
				BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
						try {
							Iterator<InternalTransaction> itr = selection.iterator();
							while (itr.hasNext()) {
								InternalTransaction transaction = itr.next();
							
								transaction.clearSplit();
								
								for (Split item : result) {
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
		return DeleteRepeating.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{2,6};
	}

}
