package net.deuce.moman.transaction.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.envelope.ui.EnvelopeSelectionDialog;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.command.Delete;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.DateSelectionDialog;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class RegisterView extends AbstractEntityTableView<InternalTransaction> {
	
	public static final String ID = RegisterView.class.getName();
	
	private AccountListener accountListener = new AccountListener();
	private EnvelopeListener envelopeListener = new EnvelopeListener();
	private EnvelopeService envelopeService;

	public RegisterView() {
		super(ServiceNeeder.instance().getTransactionService());
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		ServiceNeeder.instance().getAccountService().addEntityListener(accountListener);
		envelopeService.addEntityListener(envelopeListener);
	}

	@Override
	protected TableViewer createTableViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
				
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
							transaction.clearSplit();
							transaction.addSplit(dialog.getEnvelope());
							getViewer().refresh(transaction);
							oldEnvelope.clearBalance();
							dialog.getEnvelope().clearBalance();
							
							// oldEnvelope to new Envelope transfer
							Account account = transaction.getAccount();
							envelopeService.transfer(account, account, 
									oldEnvelope, dialog.getEnvelope(), 
									-transaction.getAmount());
							
						}
					} finally {
						ServiceNeeder.instance().getServiceContainer().stopQueuingNotifications();
					}
				}
			});
		}
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
}
