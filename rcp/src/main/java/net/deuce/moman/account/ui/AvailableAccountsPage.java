package net.deuce.moman.account.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.fi.model.FinancialInstitution;
import net.deuce.moman.service.ServiceNeeder;
import net.sf.ofx4j.client.NoOFXResponseException;
import net.sf.ofx4j.server.OFXRequestException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class AvailableAccountsPage extends WizardPage implements Runnable {
	
	private TableViewer tableViewer;
	private List<Account> availableAccounts = new LinkedList<Account>();
	private IProgressMonitor progressMonitor = null;
	private boolean running = false;
	private Exception exception = null;


	public AvailableAccountsPage() {
		super("Available Accounts");
		setTitle("Please select which accounts to import");
	}
	
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		tableViewer.getTable().setLayoutData(gridData);
		   
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("Import");
 	    column.getColumn().setWidth(50);
 	    column.setEditingSupport(new AccountEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Nickname");
 	    column.getColumn().setWidth(300);
 	    column.setEditingSupport(new AccountEditingSupport(tableViewer, 1));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("Account #");
 	    column.getColumn().setWidth(200);
		
 		tableViewer.getTable().setFont(Constants.STANDARD_FONT);
 		tableViewer.getTable().setHeaderVisible(true);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new AccountContentProvider());
	    tableViewer.setLabelProvider(new AccountLabelProvider());
	    
	    ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
		TableViewerEditor.create(tableViewer, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		
		setControl(tableViewer.getTable());
		
		((WizardDialog)getWizard().getContainer()).addPageChangedListener(new IPageChangedListener() {
			@Override
			public void pageChanged(PageChangedEvent event) {
				if (event.getSelectedPage() == AvailableAccountsPage.this) {
					availableAccounts.clear();
					final FinancialInstitution fi = ((NewAccountWizard)getWizard()).getFinancialInstitution();
					try {
						getContainer().run(false, false, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
								progressMonitor = monitor;
								monitor.beginTask("Fetching Available accounts from " + fi.getName(), 100);
								monitor.worked(5);
								running = true;
								exception = null;
								Thread progressWorker = new Thread(AvailableAccountsPage.this);
								progressWorker.start();
								while (running) {
									progressMonitor.worked(5);
									Thread.sleep(1000);
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						progressMonitor = null;
					}
					
					if (exception != null) {
						if (exception instanceof NoOFXResponseException) {
							MessageDialog.openError(getShell(), "Download Error", 
								"No response from " + fi.getName());
						} else if (exception instanceof OFXRequestException) {
							MessageDialog.openError(getShell(), "Download Error", 
									"Invalid request to " + fi.getName() + ". Check your username and password and try again.");
						} else {
							MessageDialog.openError(getShell(), "Download Error", 
									"Unknown error from " + fi.getName() + ". Verify (with " + fi.getName() + ") that you have access to download transactions.");
						}
						((NewAccountWizard)getWizard()).getContainer().showPage(getPreviousPage());
					} else {
						tableViewer.setInput(availableAccounts);
						setPageComplete(true);
					}
				}
			}
		});
		
		List<Account> dummyAccountList = new LinkedList<Account>();
		
		for (int i=0; i<10; i++) {
			dummyAccountList.add(new Account());
		}
		tableViewer.setInput(dummyAccountList);
		
		setPageComplete(false);
	}
	
	private void fetchAvailableAccounts() {
		final FinancialInstitution fi = ((NewAccountWizard)getWizard()).getFinancialInstitution();
		final String username = ((NewAccountWizard)getWizard()).getUsername();
		final String password = ((NewAccountWizard)getWizard()).getPassword();
		
		running = true;
		try {
			for (Account a : ServiceNeeder.instance().
				getFinancialInstitutionService().getAvailableAccounts(
				fi, username, password)) {
				availableAccounts.add(a);
			}
			((NewAccountWizard)getWizard()).setAvailableAccounts(availableAccounts);
		} catch (Exception e) {
			e.printStackTrace();
			exception = e;
		} finally {
			running = false;
		}
	}
	
	@Override
	public void run() {
		fetchAvailableAccounts();
	}
}
