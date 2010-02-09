package net.deuce.moman.transaction.ui;

import java.util.Date;
import java.util.Iterator;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.Delete;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.DateSelectionDialog;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.IHandlerService;

public class TransactionComposite extends Composite {
	
	public static final int NO_SHOW_SEARCH = 1 << 0;
	public static final int NO_SHOW_HEADER = 1 << 1;
	public static final int NO_SHOW_LINES = 1 << 2;
	
	private TableViewer tableViewer;
	private IWorkbenchSite site;
	private RegisterFilter filter = new RegisterFilter();
	private Text searchText;
	private boolean editingEntity;


	public TransactionComposite(Composite parent, int style, IWorkbenchSite site) {
		super(parent, style);
		this.site = site;
		initialize(style);
	}

	private void initialize(int style) {
		if ((style | NO_SHOW_SEARCH) == 0) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			setLayout(gridLayout);
			
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
				
			Composite topContainer = new Composite(this, SWT.NONE);
			topContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
			topContainer.setLayoutData(gridData);
			
			createTopControl(topContainer);
		}
		
		tableViewer = createTableViewer(this);
		
		if ((style | NO_SHOW_SEARCH) == 0) {
			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.grabExcessVerticalSpace = true;
			gridData.verticalAlignment = GridData.FILL;
			tableViewer.getTable().setLayoutData(gridData);
		}
		
		tableViewer.addDoubleClickListener(getDoubleClickListener(getShell()));
				
 		tableViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
 		
 		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
 		setupTableViewerEditor(tableViewer, actSupport);
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible((style | NO_SHOW_HEADER) == 0);
 		tableViewer.getTable().setLinesVisible((style | NO_SHOW_LINES) == 0);
	}
	
	protected Control createTopControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label searchLabel = new Label(container, SWT.NONE);
		searchLabel.setText("Search:");
		
		searchText = new Text(container, SWT.BORDER);
		searchText.setEditable(true);
		searchText.setEnabled(true);
		searchText.setLayoutData(gridData);
		
		
		return container;
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
	
	protected IDoubleClickListener getDoubleClickListener(final Shell shell) {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				int[] columns = getDoubleClickableColumns();
				if (columns.length == 0) return;
				if (editingEntity) return;
				editingEntity = true;
				
				try {
					Point cursorLocation = Display.getCurrent().getCursorLocation();
					Rectangle tableBounds = tableViewer.getTable().getParent().getParent().getBounds();
					Rectangle shellBounds = Display.getCurrent().getActiveShell().getBounds();
					
					int x = cursorLocation.x;
					
					for (int i=0; i<columns.length; i++) {
						Rectangle bounds = tableViewer.getTable().getItem(0).getBounds(columns[i]);
						int minThreshold = tableBounds.x+shellBounds.x+bounds.x;
						int maxThreshold = tableBounds.x+shellBounds.x+bounds.x+bounds.width;
				
						if (x >= minThreshold && x <= maxThreshold) {
							StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
							doubleClickHandler(columns[i], selection, shell);
						}
					}
				} finally {
					editingEntity = false;
				}
				
			}

		};
	}
	
	protected TableViewer createTableViewer(Composite parent) {
		final TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.addFilter(filter);
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				filter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}

		});
				
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(341);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(200);
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Amount");
 	    column.getColumn().setWidth(87);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Balance");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransactionLabelProvider());
		return tableViewer;
	}
	
	private void handleDateDoubleClicked(StructuredSelection selection, Shell shell) {
		InternalTransaction transaction = (InternalTransaction)selection.getFirstElement();
		
		DateSelectionDialog dialog = new DateSelectionDialog(shell, transaction.getDate());
		dialog.open();
		Date date = dialog.getDate();
        if (date != null) {
        	transaction.setDate(date, true);
        }
	}
	
	protected int[] getDoubleClickableColumns() {
		return new int[]{0,3};
	}
	
	private void handleEnvelopeDoubleClicked(final StructuredSelection selection, Shell shell) {
		InternalTransaction transaction = (InternalTransaction)selection.getFirstElement();
		Envelope envelope = transaction.getSplit().get(0);
		final EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, envelope);
		
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (envelope != dialog.getEnvelope()) {
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
					try {
						Iterator<InternalTransaction> itr = selection.iterator();
						while (itr.hasNext()) {
							InternalTransaction transaction = itr.next();
							Envelope oldEnvelope = transaction.getSplit().get(0);
							
							// oldEnvelope to new Envelope move
							transaction.removeSplit(oldEnvelope, true);
							transaction.addSplit(dialog.getEnvelope(), true);
							
							oldEnvelope.clearBalance();
							dialog.getEnvelope().clearBalance();
							
							tableViewer.refresh(transaction);
						}
					} finally {
						ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
					}
				}
			});
		}
	}

	protected void doubleClickHandler(int column, StructuredSelection selection, Shell shell) {
		switch (column) {
		case 0:
			handleDateDoubleClicked(selection, shell);
			break;
		case 3:
			handleEnvelopeDoubleClicked(selection, shell);
			break;
		}
	}
	
	private static class RegisterFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s.toLowerCase() + ".*";
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			
			InternalTransaction transaction = (InternalTransaction)element;
			
			if (Constants.CURRENCY_VALIDATOR.format(transaction.getAmount()).matches(searchString)) {
				return true;
			}
			if (transaction.getDescription().toLowerCase().matches(searchString)) {
				return true;
			}
			if (transaction.getMemo().toLowerCase().matches(searchString)) {
				return true;
			}
			if (transaction.getCheck().matches(searchString)) {
				return true;
			}
			for (Envelope env : transaction.getSplit()) {
				if (env.getName().toLowerCase().matches(searchString)) {
					return true;
				}
			}

			return false;
		}
		
	}
}
