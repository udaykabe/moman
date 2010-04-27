package net.deuce.moman.envelope.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.envelope.command.Delete;
import net.deuce.moman.transaction.ui.RegisterView;
import net.deuce.moman.ui.ViewerRegistry;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.springframework.beans.factory.annotation.Autowired;

public class EnvelopeView extends ViewPart implements EntityListener<Envelope> {

	public static final String ID = EnvelopeView.class.getName();

	public static final String ENVELOPE_VIEWER_NAME = "envelope";

	private TreeViewer treeViewer;

    private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();

    private ViewerRegistry viewerRegistry = ViewerRegistry.instance();

	public EnvelopeView() {
		envelopeService.addEntityListener(this);
        TransactionService transactionService = ServiceProvider.instance().getTransactionService();
        TransactionListener transactionListener = new TransactionListener();
        transactionService.addEntityListener(transactionListener);
	}

	public void createPartControl(Composite parent) {
		EnvelopeLabelProvider labelProvider = new EnvelopeLabelProvider();

		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL);
		tree.setHeaderVisible(true);
		tree.setFont(RcpConstants.STANDARD_FONT);

		treeViewer = new TreeViewer(tree);
		treeViewer.setComparator(new EnvelopeViewerComparator());
		treeViewer.setContentProvider(new EnvelopeContentProvider());
		// treeViewer.setSorter(new TreePathViewerSorter());

		viewerRegistry.registerViewer(ENVELOPE_VIEWER_NAME, treeViewer);

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

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { EnvelopeTransfer
				.getInstance() };
		treeViewer.addDragSupport(operations, transferTypes,
				new EnvelopeDragListener(treeViewer));
		treeViewer.addDropSupport(operations, transferTypes,
				new EnvelopeDropListener(treeViewer));

		treeViewer.setLabelProvider(labelProvider);

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				treeViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
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

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite()
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					treeViewer.getTree().selectAll();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		treeViewer.addTreeListener(new ITreeViewerListener() {

			public void treeCollapsed(TreeExpansionEvent event) {
				((Envelope) event.getElement()).setExpanded(false);
			}

			public void treeExpanded(TreeExpansionEvent event) {
				((Envelope) event.getElement()).setExpanded(true);
			}
		});

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event
						.getSelection();
				if (selection.size() == 0) {
					Envelope env = envelopeService.getSelectedEnvelope();
					if (env != null
							&& envelopeService.entityExists(env.getId())) {
						env.setSelected(false);
					}
				} else {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getPages()[0].showView(RegisterView.ID, null,
								IWorkbenchPage.VIEW_ACTIVATE);
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getPages()[0].showView(EnvelopeView.ID, null,
								IWorkbenchPage.VIEW_ACTIVATE);
						((Envelope) selection.getFirstElement())
								.setSelected(true);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});

		refresh();

	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	private void refreshEnvelope(Envelope env) {
		if (env != null && env.isDirty()) {
			treeViewer.refresh(env);
			env.clearDirty();
			if (env.getParent() != null) {
				env.getParent().markDirty();
				refreshEnvelope(env.getParent());
			}
		}
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

	public void entityAdded(EntityEvent<Envelope> event) {
		try {
			treeViewer.getTree().setFocus();
			if (event != null && event.getEntity() != null) {
				treeViewer.setSelection(new StructuredSelection(
						new Object[] { event.getEntity() }));
				treeViewer.reveal(event.getEntity());
			} else {
				treeViewer.setSelection(new StructuredSelection(
						new Object[] { envelopeService.getRootEnvelope() }));
				treeViewer.reveal(envelopeService.getRootEnvelope());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		refresh();
	}

	public void entityChanged(EntityEvent<Envelope> event) {
		if (event == null
				|| (Envelope.Properties.expanded != event.getProperty() && Envelope.Properties.selected != event
						.getProperty())) {
			if (event != null && event.getEntity() != null) {
				refreshEnvelope(event.getEntity());
			} else {
				refresh();
			}
		}
	}

	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

	private class TransactionListener implements
			EntityListener<InternalTransaction> {

		public void entityAdded(EntityEvent<InternalTransaction> event) {
		}

		public void entityChanged(EntityEvent<InternalTransaction> event) {
			if (event != null
					&& (InternalTransaction.Properties.split == event
							.getProperty() || InternalTransaction.Properties.amount == event
							.getProperty())) {
				if (event.getEntity() != null) {
					for (Split split : event.getEntity().getSplit()) {
						refreshEnvelope(split.getEnvelope());
					}
				} else {
					refresh();
				}
			}
		}

		public void entityRemoved(EntityEvent<InternalTransaction> event) {
		}

	}
}
