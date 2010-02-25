package net.deuce.moman.transaction.ui;

import net.deuce.moman.Constants;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.service.TransactionService;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ImportMatchingDialog extends TitleAreaDialog {
	
	private AccountService accountService;
	private TransactionService transactionService;
	
	public ImportMatchingDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Import Matching");
		accountService = ServiceNeeder.instance().getAccountService();
		transactionService = ServiceNeeder.instance().getTransactionService();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		initializeDialogUnits(composite);
		createForm(composite);
		return composite;
	}
	
	private void createForm(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		TableViewer tableViewer = 
			new ImportTableViewer(container, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setInput(ServiceNeeder.instance().getImportService().getOrderedEntities(false));
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ColumnViewerEditorActivationStrategy actSupport = createColumnViewerEditorActivationStrategy(tableViewer);
 		setupTableViewerEditor(tableViewer, actSupport);
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		tableViewer.getTable().setSize(1000, 300);
 		
		container = new Composite(parent, SWT.NONE);
		TransactionComposite register = new TransactionComposite(container, false, true, false, SWT.NONE);
		register.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 		register.setSize(1000, 300);
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
	protected void createButtonsForButtonBar(Composite parent) {
	}
}
