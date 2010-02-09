package net.deuce.moman.rcp.envelope;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePathViewerSorter;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class EnvelopeSelectionDialog extends Dialog {
	
	private TreeViewer treeViewer;
	private Envelope envelope = null;
	private List<Envelope> invalidSelections = new LinkedList<Envelope>();

	public EnvelopeSelectionDialog(Shell parentShell, Envelope envelope) {
		super(parentShell);
		this.envelope = envelope;
	}
	
	public void addInvalidSelection(Envelope e) {
		invalidSelections.add(e);
	}
	
	public void setInvalidSelection(List<Envelope> l) {
		if (l != null) {
			this.invalidSelections = new LinkedList<Envelope>(l);
		}
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		return Display.getCurrent().getCursorLocation();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		EnvelopeLabelProvider labelProvider = new EnvelopeLabelProvider();
		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL);
		tree.setHeaderVisible(true);
		tree.setFont(Registry.instance().getStandardFont());
		
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setText("Envelope");
		nameColumn.setWidth(200);
		
		TreeColumn balanceColumn = new TreeColumn(tree, SWT.RIGHT);
		balanceColumn.setText("Balance");
		balanceColumn.setWidth(100);
		
		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new EnvelopeSelectionContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setSorter(new TreePathViewerSorter());
		Registry.instance().setEnvelopeViewer(treeViewer);
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
				if (selection != null && selection.size() == 1) {
					envelope = (Envelope)selection.getFirstElement();
					if (!invalidSelections.contains(envelope) && !Registry.instance().getBills().contains(envelope)) {
						EnvelopeSelectionDialog.super.close();
					}
				}
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
		
		Envelope topLevel = new Envelope();
		topLevel.setEditable(false);
		topLevel.addChild(Registry.instance().getRootEnvelope());
		treeViewer.setInput(topLevel);
		
		Object[] selection;
		if (envelope != null) {
			selection = new Object[]{envelope};
		} else {
			selection = new Object[]{Registry.instance().getRootEnvelope()};
		}
		
		treeViewer.setExpandedElements(selection);
		treeViewer.setSelection(new StructuredSelection(selection));
			
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}
	
}
