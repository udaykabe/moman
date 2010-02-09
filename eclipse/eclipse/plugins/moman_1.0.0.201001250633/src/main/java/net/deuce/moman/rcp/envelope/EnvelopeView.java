package net.deuce.moman.rcp.envelope;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.command.envelope.Delete;
import net.deuce.moman.command.envelope.Edit;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
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

	public EnvelopeView() {
		Registry.instance().addEnvelopeListener(this);
	}

	public void createPartControl(Composite parent) {
		EnvelopeLabelProvider labelProvider = new EnvelopeLabelProvider();
		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL);
		tree.setHeaderVisible(true);
		tree.setFont(Registry.instance().getStandardFont());
		
		treeViewer = new TreeViewer(tree);
		ViewerComparator comparator = new EnvelopeViewerComparator();
		treeViewer.setComparator(comparator);
		treeViewer.setContentProvider(new EnvelopeContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		//treeViewer.setSorter(new TreePathViewerSorter());
		Registry.instance().setEnvelopeViewer(treeViewer);
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
 	    
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
		
 		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(Edit.ID, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
 		});
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
		
		refresh();
		
	}
	
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();		
	}
	
	private void refresh() {
		Envelope topLevel = new Envelope();
		topLevel.setEditable(false);
		topLevel.addChild(Registry.instance().getRootEnvelope());
		treeViewer.setInput(topLevel);
		
		List<Envelope> expandedElements = new LinkedList<Envelope>();
		for (Envelope env : Registry.instance().getEnvelopes()) {
			if (env.isExpanded()) {
				expandedElements.add(env);
			}
		}
		treeViewer.setExpandedElements(expandedElements.toArray());
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
