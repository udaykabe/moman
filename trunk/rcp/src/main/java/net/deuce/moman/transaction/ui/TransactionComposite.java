package net.deuce.moman.transaction.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.RcpConstants;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.EntityEvent;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.model.transaction.Split;
import net.deuce.moman.entity.model.transaction.TransactionStatus;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionCellEditor;
import net.deuce.moman.transaction.command.Delete;
import net.deuce.moman.ui.Activator;
import net.deuce.moman.ui.SelectingTableViewer;
import net.deuce.moman.ui.ShiftKeyAware;
import net.deuce.moman.ui.ViewerRegistry;

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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.IHandlerService;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionComposite extends Composite implements
		EntityListener<InternalTransaction>, ShiftKeyAware {

	public static final String TRANSACTION_VIEWER_NAME = "transaction";

	private SelectingTableViewer tableViewer;

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

    private RegisterFilter filter = new RegisterFilter();
	private Text searchText;
	private boolean shiftDown = false;
	private List<EnvelopeSelectionCellEditor> envelopeSelectionCellEditors = new LinkedList<EnvelopeSelectionCellEditor>();

	public TransactionComposite(Composite parent, boolean settingServiceViewer,
			final boolean allowDeletes, boolean selectionListener, int style) {
		super(parent, style);

		transactionService.addEntityListener(this);

		if (selectionListener) {
            EnvelopeListener envelopeListener = new EnvelopeListener();
            EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();
            envelopeService.addEntityListener(envelopeListener);
            AccountListener accountListener = new AccountListener();
            AccountService accountService = ServiceProvider.instance().getAccountService();
            accountService.addEntityListener(accountListener);
		}

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);

		Composite topContainer = new Composite(this, SWT.NONE);
		topContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		topContainer.setLayoutData(new GridData((GridData.FILL_HORIZONTAL)));

		createTopControl(topContainer);

		tableViewer = createTableViewer(this);

		tableViewer.getTable()
				.setLayoutData(new GridData((GridData.FILL_BOTH)));

		if (settingServiceViewer) {
            ViewerRegistry viewerRegistry = ViewerRegistry.instance();
            viewerRegistry.registerViewer(TRANSACTION_VIEWER_NAME, tableViewer);
		}

		tableViewer.getTable().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (allowDeletes && e.keyCode == SWT.BS
						&& e.stateMask == SWT.COMMAND) {
					IWorkbenchWindow window = Activator.getDefault()
							.getWorkbench().getActiveWorkbenchWindow();
					IHandlerService handlerService = (IHandlerService) window
							.getService(IHandlerService.class);
					try {
						handlerService.executeCommand(getDeleteCommandId(),
								null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if (e.keyCode == 'a' && e.stateMask == SWT.COMMAND) {
					tableViewer.getTable().selectAll();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
		setupTableViewerEditor(tableViewer, actSupport);

		tableViewer.getTable().setFont(RcpConstants.STANDARD_FONT);
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

	protected void setupTableViewerEditor(TableViewer tableViewer,
			ColumnViewerEditorActivationStrategy strategy) {
		TableViewerEditor.create(tableViewer, strategy,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

	}

	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(
			TableViewer viewer) {
		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
	}

	public boolean setFocus() {
		return tableViewer.getTable().setFocus();
	}

	protected void createTopControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		searchText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.SEARCH
				| SWT.ICON_SEARCH);
		searchText.setEditable(true);
		searchText.setEnabled(true);
		searchText.setLayoutData(gridData);
	}

	protected TransactionStatus[] getAvailableTransactionStatuses() {
		return TransactionStatus.values();
	}

	protected SelectingTableViewer createTableViewer(Composite parent) {

		final SelectingTableViewer tableViewer = new SelectingTableViewer(
				parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.addFilter(filter);
		searchText.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent ke) {
				filter.setSearchText(searchText.getText());
				tableViewer.refresh();
			}

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					searchText.setText("");
				}
			}
		});

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Date");
		column.getColumn().setWidth(102);
		column.setEditingSupport(new TransactionDateSelectionEditingSupport(
				tableViewer, this));

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Status");
		column.getColumn().setWidth(40);
		column.setEditingSupport(new TransactionEditingSupport(tableViewer, 1,
				getAvailableTransactionStatuses()));

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Check");
		column.getColumn().setWidth(41);
		column.setEditingSupport(new TransactionEditingSupport(tableViewer, 2,
				null));

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Description");
		column.getColumn().setWidth(330);
		column.setEditingSupport(new TransactionEditingSupport(tableViewer, 3,
				null));

		column = new TableViewerColumn(tableViewer, SWT.LEFT);
		column.getColumn().setText("Envelope");
		column.getColumn().setWidth(175);
		TransactionEnvelopeSelectionEditingSupport editingSupport = new TransactionEnvelopeSelectionEditingSupport(
				tableViewer, this, this);
		column.setEditingSupport(editingSupport);
		envelopeSelectionCellEditors
				.add((EnvelopeSelectionCellEditor) editingSupport
						.getCellEditor(null));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Credit");
		column.getColumn().setWidth(87);
		column.setEditingSupport(new TransactionEditingSupport(tableViewer, 5,
				null));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Debit");
		column.getColumn().setWidth(87);
		column.setEditingSupport(new TransactionEditingSupport(tableViewer, 6,
				null));

		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
		column.getColumn().setText("Balance");
		column.getColumn().setWidth(87);

		tableViewer.setContentProvider(new TransactionContentProvider());
		tableViewer.setLabelProvider(new TransactionLabelProvider());

		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent ke) {
				shiftDown = ke.keyCode == SWT.SHIFT;
				for (EnvelopeSelectionCellEditor cellEditor : envelopeSelectionCellEditors) {
					cellEditor.setShiftDown(shiftDown);
				}
			}

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
		return transactionService;
	}

	protected List<InternalTransaction> getEntities() {
		return transactionService.getRegisterTransactions(false, false);
	}

	protected void refresh() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {

			public void run() {
				tableViewer.setInput(getEntities());
			}
		});
	}

	public void entityAdded(EntityEvent<InternalTransaction> event) {
		refresh();
		try {
			setFocus();
			if (event != null
					&& event.getEntity() != null
					&& event.getProperty() != InternalTransaction.Properties.imported) {
				tableViewer.setSelection(new StructuredSelection(
						new Object[] { event.getEntity() }));
				tableViewer.reveal(event.getEntity());

				TableItem[] selection = tableViewer.getTable().getSelection();
				if (selection.length > 0) {
					Rectangle bounds = selection[0].getBounds(3);
					ViewerCell viewerCell = tableViewer.getCell(new Point(
							bounds.x, bounds.y));
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
		if (event == null || event.getEntity() == null
				|| InternalTransaction.Properties.date == event.getProperty()) {
			refresh();
		} else {
			tableViewer.refresh(event.getEntity());
		}
	}

	public void entityRemoved(EntityEvent<InternalTransaction> event) {
		refresh();
	}

	private class EnvelopeListener implements EntityListener<Envelope> {

		public void entityAdded(EntityEvent<Envelope> event) {
		}

		public void entityChanged(EntityEvent<Envelope> event) {
			if (event != null
					&& Envelope.Properties.selected == event.getProperty()) {
				refresh();
			}
		}

		public void entityRemoved(EntityEvent<Envelope> event) {
		}

	}

	private class AccountListener implements EntityListener<Account> {

		public void entityAdded(EntityEvent<Account> event) {
		}

		public void entityChanged(EntityEvent<Account> event) {
			if (event != null
					&& Account.Properties.selected == event.getProperty()) {
				refresh();
			}
		}

		public void entityRemoved(EntityEvent<Account> event) {
		}

	}

	private static class RegisterFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s.toLowerCase() + ".*";
		}

		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}

			InternalTransaction transaction = (InternalTransaction) element;

			if (RcpConstants.CURRENCY_VALIDATOR.format(transaction.getAmount())
					.matches(searchString)) {
				return true;
			}
			if (transaction.getDescription().toLowerCase()
					.matches(searchString)) {
				return true;
			}
			if (transaction.getMemo().toLowerCase().matches(searchString)) {
				return true;
			}
			if (transaction.getCheck().matches(searchString)) {
				return true;
			}
			for (Split item : transaction.getSplit()) {
				if (item.getEnvelope().getName().toLowerCase().matches(
						searchString)) {
					return true;
				}
			}

			return false;
		}

	}
}
