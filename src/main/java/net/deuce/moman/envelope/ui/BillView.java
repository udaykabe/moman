package net.deuce.moman.envelope.ui;

import java.util.Iterator;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.command.DeleteBill;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class BillView extends AbstractEntityTableView<Envelope> {
	
	public static final String ID = BillView.class.getName();
	
	private ServiceContainer serviceContainer;

	public BillView() {
		super(ServiceNeeder.instance().getEnvelopeService());
		serviceContainer = ServiceNeeder.instance().getServiceContainer();
	}
	
	@Override
	protected boolean isSettingServiceViewer() {
		return false;
	}
	
	private EnvelopeService getEnvelopeService() {
		return (EnvelopeService)super.getService();
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		tableViewer.setComparator(new BillViewerComparator());
		getEnvelopeService().setBillViewer(tableViewer);
		
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Name");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Due Day");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Frequency");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 3));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(100);
 	    
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new BillContentProvider());
	    tableViewer.setLabelProvider(new BillLabelProvider());
		return tableViewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
		Envelope parentEnvelope = null;
		Iterator<Envelope> itr = selection.iterator();
		while (itr.hasNext()) {
			Envelope bill = itr.next();
			if (parentEnvelope == null) {
				parentEnvelope = bill.getParent();
			} else if (parentEnvelope != bill.getParent()) {
				parentEnvelope = getEnvelopeService().getRootEnvelope();
				break;
			}
		}
		EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, parentEnvelope);
		
		dialog.create();
		dialog.open();
		if (parentEnvelope != dialog.getEnvelope()) {
			
			serviceContainer.startQueuingNotifications();
			try {
				itr = selection.iterator();
				while (itr.hasNext()) {
					Envelope bill = itr.next();
					Envelope oldParent = bill.getParent();
					if (oldParent != null) {
						oldParent.removeChild(bill);
					}
					bill.setParent(dialog.getEnvelope());
					dialog.getEnvelope().addChild(bill);
				}
			} finally {
				serviceContainer.stopQueuingNotifications();
			}
		}
	}

	@Override
	protected String getDeleteCommandId() {
		return DeleteBill.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{5};
	}

	protected IDoubleClickListener getDoubleClickListener(Shell shell) {
		return super.getDoubleClickListener(shell);
	}

	@Override
	protected List<Envelope> getEntities() {
		return getEnvelopeService().getOrderedBills(false);
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
