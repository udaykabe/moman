package net.deuce.moman.rcp.envelope;

import java.util.Iterator;

import net.deuce.moman.command.envelope.DeleteBill;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Bill;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class BillView extends ViewPart implements EntityListener<Envelope> {
	
	public static final String ID = BillView.class.getName();
	
	private TableViewer tableViewer;
	private boolean editingEnvelope = false;

	public BillView() {
		Registry.instance().addBillListener(this);
	}

	public void createPartControl(final Composite parent) {
		
 		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);    
 		Registry.instance().setBillViewer(tableViewer);
 		
 		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (editingEnvelope) return;
				editingEnvelope = true;
				try {
					
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Rectangle tableBounds = tableViewer.getTable().getParent().getParent().getBounds();
					Rectangle bounds = tableViewer.getTable().getItem(0).getBounds(5);
					Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
					
					int x = cursorLocation.x;
					int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
					int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
					
					if (x >= minThreshold && x <= maxThreshold) {
						StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
						
						Envelope parentEnvelope = null;
						Iterator<Bill> itr = selection.iterator();
						while (itr.hasNext()) {
							Bill bill = itr.next();
							if (parentEnvelope == null) {
								parentEnvelope = bill.getParent();
							} else if (parentEnvelope != bill.getParent()) {
								parentEnvelope = Registry.instance().getRootEnvelope();
								break;
							}
						}
						EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(parent.getShell(), parentEnvelope);
						
						dialog.create();
						dialog.open();
						if (parentEnvelope != dialog.getEnvelope()) {
							
							Registry.instance().setMonitor(false);
							try {
								itr = selection.iterator();
								while (itr.hasNext()) {
									Bill bill = itr.next();
									Envelope oldParent = bill.getParent();
									if (oldParent != null) {
										oldParent.removeChild(bill);
									}
									bill.setParent(dialog.getEnvelope());
									dialog.getEnvelope().addChild(bill);
								}
							} finally {
								Registry.instance().setMonitor(true);
								Registry.instance().notifyBillListenersOfChanges();
								Registry.instance().notifyEnvelopeListenersOfChanges();
							}
						}
					}
				} finally {
					editingEnvelope = false;
				}
				
			}
		});
 		
 		tableViewer.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(DeleteBill.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
 		});
 		
 		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TableViewerEditor.create(tableViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
 		
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Enabled");
 	    column.getColumn().setWidth(30);
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
 	    column.getColumn().setWidth(70);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 3));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new BillEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(100);
 	    
 		tableViewer.getTable().setFont(Registry.instance().getStandardFont());
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new BillContentProvider());
	    tableViewer.setLabelProvider(new BillLabelProvider());
	    tableViewer.setInput(Registry.instance().getBills());
	}
	
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();		
	}
	
	private void refresh() {
		tableViewer.setInput(Registry.instance().getBills());
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

}
