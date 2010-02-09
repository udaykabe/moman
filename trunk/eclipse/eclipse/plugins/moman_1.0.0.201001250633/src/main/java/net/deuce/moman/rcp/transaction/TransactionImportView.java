package net.deuce.moman.rcp.transaction;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.transaction.InternalTransaction;
import net.deuce.moman.rcp.envelope.EnvelopeSelectionDialog;

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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class TransactionImportView extends ViewPart implements EntityListener<Transaction> {
	
	public static final String ID = TransactionImportView.class.getName();
	
	private TableViewer importViewer;
	private boolean editingEnvelope;

	public TransactionImportView() {
		Registry.instance().addImportedTransactionListener(this);
	}

	@Override
	public void createPartControl(final Composite parent) {
		importViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Registry.instance().setImportViewer(importViewer);
		
				
		importViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (editingEnvelope) return;
				editingEnvelope = true;
				
				Point cursorLocation = Display.getCurrent().getCursorLocation();
				Rectangle tableBounds = importViewer.getTable().getParent().getParent().getBounds();
				Rectangle bounds = importViewer.getTable().getItem(0).getBounds(5);
				Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
				
				int x = cursorLocation.x;
				int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
				int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
				
				if (x >= minThreshold && x <= maxThreshold) {
					StructuredSelection selection = (StructuredSelection)importViewer.getSelection();
					Transaction transaction = (Transaction)selection.getFirstElement();
					Envelope envelope = transaction.getSplit().get(0);
					EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(parent.getShell(), envelope);
					
					dialog.create();
					dialog.open();
					if (envelope != dialog.getEnvelope()) {
						transaction.clearSplit();
						transaction.addSplit(dialog.getEnvelope());
						importViewer.refresh(transaction);
					}
				}
				editingEnvelope = false;
				
			}
		});
 		importViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
 		
 		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(importViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TableViewerEditor.create(importViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
   
        TableViewerColumn column = new TableViewerColumn(importViewer, SWT.CENTER);
 		column.getColumn().setText("Matched");
 		column.getColumn().setAlignment(SWT.CENTER);
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new TransactionEditingSupport(importViewer, 0));
		
        column = new TableViewerColumn(importViewer, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
		
 		column = new TableViewerColumn(importViewer, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
		
 		column = new TableViewerColumn(importViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(341);
 	    column.setEditingSupport(new TransactionImportEditingSupport(importViewer, 2));
		
 		column = new TableViewerColumn(importViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(87);
		
 		column = new TableViewerColumn(importViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
		
 		importViewer.getTable().setFont(Registry.instance().getStandardFont());
 		importViewer.getTable().setHeaderVisible(true);
 		importViewer.getTable().setLinesVisible(true);
 		
	    importViewer.setContentProvider(new TransactionContentProvider());
	    importViewer.setLabelProvider(new TransactionLabelProvider());
	    importViewer.setInput(Registry.instance().getImportedTransactions());
	}
	
	private void refresh() {
		importViewer.setInput(Registry.instance().getImportedTransactions());
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void entityAdded(EntityEvent<Transaction> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Transaction> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Transaction> event) {
		refresh();
	}

}
