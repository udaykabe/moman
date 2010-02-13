package net.deuce.moman.envelope.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.model.Split;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SplitSelectionDialog extends Dialog {
	
	private TableViewer splitViewer;
	private List<Envelope> split = new LinkedList<Envelope>();
	private List<Envelope> invalidSelections = new LinkedList<Envelope>();
	private EnvelopeService envelopeService;
	private boolean allowBills;
	private boolean editingEnvelope = false;

	public SplitSelectionDialog(Shell parentShell, List<Envelope> split) {
		super(parentShell);
		this.split.addAll(split);
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
	
	public List<Envelope> getSplit() {
		return split;
	}

	public void setSplit(List<Envelope> split) {
		this.split.clear();
		this.split.addAll(split);
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		return Display.getCurrent().getCursorLocation();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		Composite topContainer = createAddRemoveButtons(parent);
		topContainer.setLayoutData(gridData);
		
		createSplitTable(parent);
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		
		splitViewer.getTable().setLayoutData(gridData);
		return parent;
	}
	
	private Composite createAddRemoveButtons(Composite parent) {
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		
		Button addButton = new Button(container, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(gridData);
		
		Button removeButton = new Button(container, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(gridData);
		
		return container;
	}
	
	private void createSplitTable(Composite parent) {
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		
		splitViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		   
        TableViewerColumn column = new TableViewerColumn(splitViewer, SWT.CENTER);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
		
 		column = new TableViewerColumn(splitViewer, SWT.NONE);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(100);
 	    column.setEditingSupport(new SplitEditingSupport(splitViewer, 1));
		
	    splitViewer.setContentProvider(new SplitContentProvider());
	    splitViewer.setLabelProvider(new SplitLabelProvider());

	    splitViewer.addDoubleClickListener(getDoubleClickListener(parent.getShell()));
		
 		splitViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					splitViewer.getTable().selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
 		
 		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(splitViewer);
 		setupTableViewerEditor(splitViewer, actSupport);
		
 		splitViewer.getTable().setFont(Constants.STANDARD_FONT);
 		splitViewer.getTable().setHeaderVisible(true);
 		splitViewer.getTable().setLinesVisible(true);
	}
	
	protected int[] getDoubleClickableColumns() {
		return new int[]{0};
	}
	
	protected IDoubleClickListener getDoubleClickListener(final Shell shell) {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				int[] columns = getDoubleClickableColumns();
				if (columns.length == 0) return;
				if (editingEnvelope) return;
				editingEnvelope = true;
				
				try {
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Rectangle tableBounds = splitViewer.getTable().getParent().getParent().getBounds();
					Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
					
					int x = cursorLocation.x;
					
					for (int i=0; i<columns.length; i++) {
						Rectangle bounds = splitViewer.getTable().getItem(0).getBounds(columns[i]);
						int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
						int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
				
						if (x >= minThreshold && x <= maxThreshold) {
							StructuredSelection selection = (StructuredSelection)splitViewer.getSelection();
							doubleClickHandler(columns[i], selection, shell);
						}
					}
				} finally {
					editingEnvelope = false;
				}
				
			}

		};
	}
	
	protected void doubleClickHandler(int column, final StructuredSelection selection, Shell shell) {
		Split split = (Split)selection.getFirstElement();
		final EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, split.getEnvelope());
		
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (split.getEnvelope() != dialog.getEnvelope()) {
			split.setEnvelope(dialog.getEnvelope());
			splitViewer.refresh(split);
		}
	}
	
	protected void setupTableViewerEditor(TableViewer tableViewer, ColumnViewerEditorActivationStrategy strategy) {
		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
   
	}
	
	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(TableViewer viewer) {
 		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}
	
}
