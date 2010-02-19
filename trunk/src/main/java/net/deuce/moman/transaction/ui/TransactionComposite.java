package net.deuce.moman.transaction.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionCellEditor;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.Delete;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.transaction.model.TransactionStatus;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.ui.SelectingTableViewer;
import net.deuce.moman.ui.ShiftKeyAware;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.IHandlerService;

public class TransactionComposite extends Composite
implements EntityListener<InternalTransaction>, ShiftKeyAware {
	
	private SelectingTableViewer tableViewer;
	private TransactionService service;
	private AccountListener accountListener = new AccountListener();
	private EnvelopeListener envelopeListener = new EnvelopeListener();
	private EnvelopeService envelopeService;
	private RegisterFilter filter = new RegisterFilter();
	private Text searchText;
	private boolean shiftDown = false;
	private List<EnvelopeSelectionCellEditor> envelopeSelectionCellEditors = new LinkedList<EnvelopeSelectionCellEditor>();

	public TransactionComposite(Composite parent, boolean settingServiceViewer,
			final IWorkbenchSite site, boolean selectionListener, int style) {
		super(parent, style);
		
		service = ServiceNeeder.instance().getTransactionService();
		service.addEntityListener(this);
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		
		if (selectionListener) {
			envelopeService.addEntityListener(envelopeListener);
			ServiceNeeder.instance().getAccountService().addEntityListener(accountListener);
		}

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
		
		tableViewer = createTableViewer(this);
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		tableViewer.getTable().setLayoutData(gridData);
		
		if (settingServiceViewer) {
			service.setViewer(tableViewer);
		}
		
		if (site != null) {
	 		tableViewer.getTable().addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
						IHandlerService handlerService = (IHandlerService) site.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(getDeleteCommandId(), null);
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
		}
 		
 		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
 		setupTableViewerEditor(tableViewer, actSupport);
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
 		refresh();
	}
	
	public boolean isShiftKeyDown() {
		return shiftDown;
	}

	public void setShiftKeyDown(boolean shiftDown) {
		this.shiftDown = shiftDown;
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
	public boolean setFocus() {
		return tableViewer.getTable().setFocus();
	}

	protected void createTopControl(Composite parent) {
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
	}
	
	protected TransactionStatus[] getAvailableTransactionStatuses() {
		return TransactionStatus.values();
	}
	
	protected SelectingTableViewer createTableViewer(Composite parent) {
		final SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.addFilter(filter);
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				filter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}
		});
				
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Date");
 	    column.getColumn().setWidth(102);
 	    column.setEditingSupport(new TransactionDateSelectionEditingSupport(tableViewer, this));
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Status");
 	    column.getColumn().setWidth(40);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 1, getAvailableTransactionStatuses()));
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 2, null));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(330);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 3, null));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(175);
 	    TransactionEnvelopeSelectionEditingSupport editingSupport =
 	    	new TransactionEnvelopeSelectionEditingSupport(tableViewer, this, this);
 	    column.setEditingSupport(editingSupport);
 	    envelopeSelectionCellEditors.add((EnvelopeSelectionCellEditor)editingSupport.getCellEditor(null));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Credit");
 	    column.getColumn().setWidth(87);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 5, null));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Debit");
 	    column.getColumn().setWidth(87);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 6, null));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Balance");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransactionLabelProvider());
	    
	    tableViewer.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				shiftDown = ke.keyCode == SWT.SHIFT;
				for (EnvelopeSelectionCellEditor cellEditor : envelopeSelectionCellEditors) {
					cellEditor.setShiftDown(shiftDown);
				}
			}
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.keyCode == SWT.SHIFT) {
					shiftDown = false;
					for (EnvelopeSelectionCellEditor cellEditor : envelopeSelectionCellEditors) {
						cellEditor.setShiftDown(shiftDown);
					}
				}
			}
		});
	    
		return tableViewer;
	}
	
	protected String getDeleteCommandId() {
		return Delete.ID;
	}
	
	protected TransactionService getService() {
		return service;
	}

	protected List<InternalTransaction> getEntities() {
		return service.getRegisterTransactions(false, false);
	}
	
	protected void refresh() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			@Override
			public void run() {
				tableViewer.setInput(getEntities());
			}
		});
	}
	
	public void entityAdded(EntityEvent<InternalTransaction> event) {
		refresh();
		try {
			setFocus();
			if (event != null && event.getEntity() != null) {
				tableViewer.setSelection(new StructuredSelection(new Object[]{event.getEntity()}));
				tableViewer.reveal(event.getEntity());
				
				TableItem[] selection = tableViewer.getTable().getSelection();
				if (selection.length > 0) {
					Rectangle bounds = selection[0].getBounds(3);
					ViewerCell viewerCell = tableViewer.getCell(new Point(bounds.x, bounds.y));
					if (viewerCell != null) {
						tableViewer.activateInitialCellEditor(viewerCell);
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void entityChanged(EntityEvent<InternalTransaction> event) {
		if (event == null || event.getEntity() == null || InternalTransaction.Properties.date == event.getProperty()) {
			refresh();
		} else {
			tableViewer.refresh(event.getEntity());
		}
	}
	
	@Override
	public void entityRemoved(EntityEvent<InternalTransaction> event) {
		refresh();
	}
	
	private class EnvelopeListener implements EntityListener<Envelope> {

		@Override
		public void entityAdded(EntityEvent<Envelope> event) {
		}

		@Override
		public void entityChanged(EntityEvent<Envelope> event) {
			if (event != null && Envelope.Properties.selected == event.getProperty()) {
				refresh();
			}
		}

		@Override
		public void entityRemoved(EntityEvent<Envelope> event) {
		}
		
	}
	
	private class AccountListener implements EntityListener<Account> {

		@Override
		public void entityAdded(EntityEvent<Account> event) {
		}

		@Override
		public void entityChanged(EntityEvent<Account> event) {
			if (event != null && Account.Properties.selected == event.getProperty()) {
				refresh();
			}
		}

		@Override
		public void entityRemoved(EntityEvent<Account> event) {
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
			for (Split item : transaction.getSplit()) {
				if (item.getEnvelope().getName().toLowerCase().matches(searchString)) {
					return true;
				}
			}

			return false;
		}
		
	}
}
