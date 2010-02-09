package net.deuce.moman.rcp.account;

import net.deuce.moman.command.account.Delete;
import net.deuce.moman.command.account.Edit;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.account.Account;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class AccountView extends ViewPart implements EntityListener<Account> {
	
	public static final String ID = AccountView.class.getName();
	
	private TableViewer tableViewer;

	public AccountView() {
		Registry.instance().addAccountListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		IContextService service = (IContextService)getSite().getService(IContextService.class); 
		service.activateContext("net.deuce.moman.context.main"); 
		
 		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);    
 		Registry.instance().setAccountViewer(tableViewer);
 		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(Edit.ID, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
 		});
 		tableViewer.getTable().addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.BS && e.stateMask == SWT.COMMAND) {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					try {
						handlerService.executeCommand(Delete.ID, null);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
 		});
   
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
 		column.getColumn().setText("enabled");
 	    column.getColumn().setWidth(30);
 	    column.setEditingSupport(new AccountEditingSupport(tableViewer, 0));
		
 		column = new TableViewerColumn(tableViewer, SWT.NONE);
 		column.getColumn().setText("name");
 	    column.getColumn().setWidth(200);
 	    //column.setEditingSupport(new AccountEditingSupport(tableViewer, 1));
		
 		tableViewer.getTable().setFont(Registry.instance().getStandardFont());
 		tableViewer.getTable().setHeaderVisible(false);
 		tableViewer.getTable().setLinesVisible(true);
 		
	    tableViewer.setContentProvider(new AccountContentProvider());
	    tableViewer.setLabelProvider(new AccountLabelProvider());
	    tableViewer.setInput(Registry.instance().getAccounts());
	}
	
	private void refresh() {
		tableViewer.setInput(Registry.instance().getAccounts());
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	@Override
	public void entityAdded(EntityEvent<Account> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Account> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Account> event) {
		refresh();
	}

}
