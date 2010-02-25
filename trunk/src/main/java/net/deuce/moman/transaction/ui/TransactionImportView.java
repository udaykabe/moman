package net.deuce.moman.transaction.ui;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TransactionImportView extends AbstractEntityTableView<InternalTransaction> {
	
	public static final String ID = TransactionImportView.class.getName();
	
	public TransactionImportView() {
		super(ServiceNeeder.instance().getImportService());
		ServiceNeeder.instance().getTransactionService().addEntityListener(this);
	}

	@Override
	protected boolean isSettingServiceViewer() {
		return false;
	}
	
	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		return new ImportTableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}

}
