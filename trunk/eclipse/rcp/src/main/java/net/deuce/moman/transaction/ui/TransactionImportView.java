package net.deuce.moman.transaction.ui;

import net.deuce.moman.command.importer.Delete;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.transaction.ImportService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionImportView extends
		AbstractEntityTableView<InternalTransaction> {

	public static final String ID = TransactionImportView.class.getName();

	public static final String IMPORT_VIEWER_NAME = "import";

	private ImportService importService = ServiceProvider.instance().getImportService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	public TransactionImportView() {
		super();
		transactionService.addEntityListener(this);
	}

	protected String getViewerName() {
		return IMPORT_VIEWER_NAME;
	}

	protected EntityService<InternalTransaction> getService() {
		return transactionService;
	}

	protected boolean isSettingServiceViewer() {
		return false;
	}

	protected SelectingTableViewer createTableViewer(Composite parent) {
		return new ImportTableViewer(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
	}

	protected String getDeleteCommandId() {
		return Delete.ID;
	}

}
