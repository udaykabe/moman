package net.deuce.moman.account.ui;

import net.deuce.moman.account.command.Delete;
import net.deuce.moman.account.command.Edit;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractEntityTableView;
import net.deuce.moman.ui.SelectingTableViewer;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;

public class AccountView extends AbstractEntityTableView<Account> {
	
	public static final String ID = AccountView.class.getName();
	
	public AccountView() {
		super(ServiceNeeder.instance().getAccountService());
	}
	
	@Override
	protected boolean getLinesVisible() {
		return false;
	}
	
	@Override
	protected SelectingTableViewer createTableViewer(Composite parent) {
		SelectingTableViewer tableViewer = new SelectingTableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);    
		   
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("enabled");
 	    column.getColumn().setWidth(30);
 	    column.setEditingSupport(new AccountEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("name");
 	    column.getColumn().setWidth(200);
 	    //column.setEditingSupport(new AccountEditingSupport(tableViewer, 1));
		
	    tableViewer.setContentProvider(new AccountContentProvider());
	    tableViewer.setLabelProvider(new AccountLabelProvider());
		return tableViewer;
	}
	
	@Override
	protected ColumnViewerEditorActivationStrategy createColumnViewerEditorActivationStrategy(TableViewer viewer) {
 		return new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
	}
	
	@Override
	protected boolean getHeaderVisible() {
		return false;
	}

	@Override
	protected String getDeleteCommandId() {
		return Delete.ID;
	}
	
	@Override
	protected IDoubleClickListener getDoubleClickListener(Shell shell) {
		return new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(Edit.ID, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
 		};
	}

	@Override
	public void createPartControl(Composite parent) {
		IContextService service = (IContextService)getSite().getService(IContextService.class); 
		service.activateContext("net.deuce.moman.context.main"); 
		super.createPartControl(parent);
	}
	
	@Override
	public void entityAdded(EntityEvent<Account> event) {
		super.entityAdded(event);
	}
}
