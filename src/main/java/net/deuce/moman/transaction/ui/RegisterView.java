package net.deuce.moman.transaction.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.envelope.ui.SplitSelectionDialog;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.Delete;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.model.Split;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.DateSelectionDialog;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RegisterView extends AbstractEntityTableView<InternalTransaction>  {
	
	public static final String ID = RegisterView.class.getName();
	
	private AccountListener accountListener = new AccountListener();
	private EnvelopeListener envelopeListener = new EnvelopeListener();
	private EnvelopeService envelopeService;
	private RegisterFilter filter = new RegisterFilter();
	private Text searchText;
	private boolean shiftDown = false;

	public RegisterView() {
		super(ServiceNeeder.instance().getTransactionService());
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		ServiceNeeder.instance().getAccountService().addEntityListener(accountListener);
		envelopeService.addEntityListener(envelopeListener);
	}

	@Override
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

	protected boolean hasTopControl() {
		return true;
	}

	@Override
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
		
        column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Check");
 	    column.getColumn().setWidth(41);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Description");
 	    column.getColumn().setWidth(330);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 2));
		
 		column = new TableViewerColumn(tableViewer, SWT.LEFT);
 		column.getColumn().setText("Envelope");
 	    column.getColumn().setWidth(175);
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Credit");
 	    column.getColumn().setWidth(87);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 4));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Debit");
 	    column.getColumn().setWidth(87);
 	    column.setEditingSupport(new TransactionEditingSupport(tableViewer, 5));
		
 		column = new TableViewerColumn(tableViewer, SWT.RIGHT);
 		column.getColumn().setText("Balance");
 	    column.getColumn().setWidth(87);
		
	    tableViewer.setContentProvider(new TransactionContentProvider());
	    tableViewer.setLabelProvider(new TransactionLabelProvider());
	    
	    tableViewer.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				shiftDown = ke.keyCode == SWT.SHIFT;
			}
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.keyCode == SWT.SHIFT) {
					shiftDown = false;
				}
			}
		});
	    
		return tableViewer;
	}
	
	@Override
	protected int getNewEntitySelectionColumn() {
		return 2;
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
	
	private void handleEnvelopeDoubleClicked(final StructuredSelection selection, Shell shell) {
		InternalTransaction transaction = (InternalTransaction)selection.getFirstElement();
		List<Split> split = transaction.getSplit();
		
		if (shiftDown || split.size() > 1) {
			handleSplitSelectionDialog(selection, shell, transaction, split);
		} else {
			handleEnvelopeSelectionDialog(selection, shell, transaction, split);
		}
	}
	
	private void handleEnvelopeSelectionDialog(final StructuredSelection selection,
			Shell shell, InternalTransaction transaction, List<Split> split) {
		final EnvelopeSelectionDialog dialog = new EnvelopeSelectionDialog(shell, split.get(0).getEnvelope());
		dialog.setAllowBills(true);
		dialog.create();
		dialog.open();
		if (split.get(0).getEnvelope() != dialog.getEnvelope()) {
			BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
				@SuppressWarnings("unchecked")
				public void run() {
					ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
					try {
						Iterator<InternalTransaction> itr = selection.iterator();
						while (itr.hasNext()) {
							InternalTransaction transaction = itr.next();
						
							transaction.clearSplit();
							
							transaction.addSplit(dialog.getEnvelope(), transaction.getAmount(), true);
							
							getViewer().refresh(transaction);
						}
					} finally {
						ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
					}
				}
			});
		}
	}
	
	private void handleSplitSelectionDialog(final StructuredSelection selection,
			Shell shell, InternalTransaction transaction, List<Split> split) {
		final SplitSelectionDialog dialog = new SplitSelectionDialog(shell, transaction.getAmount(), split);
		
		dialog.setAllowBills(true);
		dialog.create();
		int status = dialog.open();
		final List<Split> result = dialog.getSplit();
		if (status == Window.OK) {
			if (!split.equals(result)) {
				BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						ServiceNeeder.instance().getServiceContainer().startQueuingNotifications();
						try {
							Iterator<InternalTransaction> itr = selection.iterator();
							while (itr.hasNext()) {
								InternalTransaction transaction = itr.next();
							
								transaction.clearSplit();
								
								for (Split item : result) {
									if (transaction.getAmount() < 0.0) {
										item.setAmount(-item.getAmount());
									}
									transaction.addSplit(item, true);
								}
								
								getViewer().refresh(transaction);
							}
						} finally {
							ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
						}
					}
				});
			}
		}
		shiftDown = false;
	}

	@Override
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

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

	@Override
	protected int[] getDoubleClickableColumns() {
		return new int[]{0,3};
	}
	
	protected TransactionService getTransactionService() {
		return (TransactionService)super.getService();
	}

	@Override
	protected List<InternalTransaction> getEntities() {
		return getTransactionService().getRegisterTransactions(false, false);
	}

	@Override
	public void entityChanged(EntityEvent<InternalTransaction> event) {
		if (event == null || event.getEntity() == null || InternalTransaction.Properties.date == event.getProperty()) {
			refresh();
		} else {
			getViewer().refresh(event.getEntity());
		}
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
