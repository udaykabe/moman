package net.deuce.moman.envelope.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;

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
	private EnvelopeService envelopeService;
	private boolean allowBills;

	public EnvelopeSelectionDialog(Shell parentShell, Envelope envelope) {
		super(parentShell);
		this.envelope = envelope;
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
	}
	
	public boolean isAllowBills() {
		return allowBills;
	}

	public void setAllowBills(boolean allowBills) {
		this.allowBills = allowBills;
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
		Tree tree = new Tree(parent, SWT.BORDER | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setFont(Constants.STANDARD_FONT);
		
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setText("Envelope");
		nameColumn.setWidth(200);
		
		TreeColumn balanceColumn = new TreeColumn(tree, SWT.RIGHT);
		balanceColumn.setText("Balance");
		balanceColumn.setWidth(100);
		
		treeViewer = new EnvelopeSelectionTreeViewer(tree);
		EnvelopeSelectionContentProvider contentProvider = new EnvelopeSelectionContentProvider();
		contentProvider.setAllowBills(allowBills);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setSorter(new TreePathViewerSorter());
		envelopeService.setViewer(treeViewer);
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
				if (selection != null && selection.size() == 1) {
					envelope = (Envelope)selection.getFirstElement();
					if (!invalidSelections.contains(envelope) && (allowBills || !envelopeService.getBills().contains(envelope))) {
						EnvelopeSelectionDialog.this.close();
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
		
		Envelope topLevel = ServiceNeeder.instance().getEnvelopeFactory()
			.createTopLevelEnvelope();
		treeViewer.setInput(topLevel);
		
		Object[] selection;
		if (envelope != null) {
			selection = new Object[]{envelope};
		} else {
			selection = new Object[]{envelopeService.getRootEnvelope()};
		}
		
		/*
		treeViewer.setExpandedElements(selection);
		*/
		treeViewer.expandAll();
		treeViewer.setSelection(new StructuredSelection(selection));
			
		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}
	
}
