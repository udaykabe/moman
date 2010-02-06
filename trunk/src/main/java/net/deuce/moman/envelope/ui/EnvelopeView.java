package net.deuce.moman.envelope.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.command.Delete;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class EnvelopeView extends ViewPart implements EntityListener<Envelope> {
	
	public static final String ID = EnvelopeView.class.getName();
	
	private TreeViewer treeViewer;
	private TransactionListener transactionListener = new TransactionListener();
	private EnvelopeService envelopeService;
	private EnvelopeFactory envelopeFactory;

	public EnvelopeView() {
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		envelopeFactory = ServiceNeeder.instance().getEnvelopeFactory();
		envelopeService.addEntityListener(this);
		ServiceNeeder.instance().getTransactionService().addEntityListener(transactionListener);
	}

	public void createPartControl(Composite parent) {
		EnvelopeLabelProvider labelProvider = new EnvelopeLabelProvider();
		
		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL);
		tree.setHeaderVisible(true);
		tree.setFont(Constants.STANDARD_FONT);
		
		treeViewer = new TreeViewer(tree);
		treeViewer.setComparator(new EnvelopeViewerComparator());
		treeViewer.setContentProvider(new EnvelopeContentProvider());
		//treeViewer.setSorter(new TreePathViewerSorter());
		envelopeService.setViewer(treeViewer);
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
 	    column.setEditingSupport(new EnvelopeEditingSupport(treeViewer, 0));
 	    
		column = new TreeViewerColumn(treeViewer, SWT.RIGHT);
		column.getColumn().setText("Balance");
		column.getColumn().setWidth(100);
		
		column = new TreeViewerColumn(treeViewer, SWT.RIGHT);
		column.getColumn().setText("Budgeted");
		column.getColumn().setWidth(100);
 	    column.setEditingSupport(new EnvelopeEditingSupport(treeViewer, 2));
		
		column = new TreeViewerColumn(treeViewer, SWT.RIGHT);
		column.getColumn().setText("Freqency");
		column.getColumn().setWidth(100);
 	    column.setEditingSupport(new EnvelopeEditingSupport(treeViewer, 3));
		
		int operations = DND.DROP_COPY| DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[]{EnvelopeTransfer.getInstance()};
		treeViewer.addDragSupport(operations, transferTypes , new EnvelopeDragListener(treeViewer));
		treeViewer.addDropSupport(operations, transferTypes, new EnvelopeDropListener(treeViewer));
		
		treeViewer.setLabelProvider(labelProvider);
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(treeViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TreeViewerEditor.create(treeViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
 		treeViewer.getTree().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					treeViewer.getTree().selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
 		
		treeViewer.addTreeListener(new ITreeViewerListener() {
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				((Envelope)event.getElement()).setExpanded(false);
			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				((Envelope)event.getElement()).setExpanded(true);
			}
		});
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				if (selection.size() == 0) {
					Envelope env = envelopeService.getSelectedEnvelope();
					if (env != null && envelopeService.entityExists(env.getId())) {
						env.setSelected(false);
					}
				} else {
					((Envelope)selection.getFirstElement()).setSelected(true);
				}
			}
		});
		
		refresh();
		
	}
	
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();		
	}
	
	private void refresh() {
		treeViewer.setInput(envelopeFactory.createTopLevelEnvelope());
		
		List<Envelope> expandedElements = new LinkedList<Envelope>();
		for (Envelope env : envelopeService.getEntities()) {
			if (env.isExpanded()) {
				expandedElements.add(env);
			}
		}
		treeViewer.setExpandedElements(expandedElements.toArray());
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		try {
			treeViewer.getTree().setFocus();
			if (event != null && event.getEntity() != null) {
				treeViewer.setSelection(new StructuredSelection(new Object[]{event.getEntity()}));
				treeViewer.reveal(event.getEntity());
			} else {
				treeViewer.setSelection(new StructuredSelection(new Object[]{envelopeService.getRootEnvelope()}));
				treeViewer.reveal(envelopeService.getRootEnvelope());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		if (event == null || (Envelope.Properties.expanded != event.getProperty() &&
				Envelope.Properties.selected != event.getProperty())) {
			refresh();
		}
	}

	@Override
	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

	private class TransactionListener implements EntityListener<InternalTransaction> {

		@Override
		public void entityAdded(EntityEvent<InternalTransaction> event) {
		}

		@Override
		public void entityChanged(EntityEvent<InternalTransaction> event) {
			if (event != null && (InternalTransaction.Properties.split == event.getProperty() ||
					InternalTransaction.Properties.amount == event.getProperty())) {
				refresh();
			}
		}

		@Override
		public void entityRemoved(EntityEvent<InternalTransaction> event) {
		}
		
	}
}
