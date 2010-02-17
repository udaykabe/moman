package net.deuce.moman.account.command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.service.FileImportingTransactionProcessor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ImportFile extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.account.command.importFile";
	
	public ImportFile() {
		super(false);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		AccountService accountService = ServiceNeeder.instance().getAccountService();
		Account account = null;
		if (accountService.getEntities().size() == 1) {
			account = accountService.getEntities().get(0);
		} else {
			SelectAccountDialog dialog = new SelectAccountDialog(window.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				account = dialog.getEntity();
			}
		}
		
		if (account != null) {
			FileReader fileReader = null;
			try {
				Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				
				String lastDirectory = ServiceNeeder.instance().getServiceContainer().getLastUsedImportDirectory().getAbsolutePath();
				fileDialog.setFilterPath(lastDirectory);
		        fileDialog.setFilterExtensions(new String[] {"*.ofx"});
		        fileDialog.setFilterNames(new String[] {"OFX File"});
		        String fileSelected = fileDialog.open();
				
		        if (fileSelected != null) {
		        	
					ServiceNeeder.instance().getServiceContainer()
						.setLastUsedImportDirectory(new File(fileSelected).getParentFile());
					new FileImportingTransactionProcessor(window.getShell(),
							account, fileSelected).execute();
		        	
		        }
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExecutionException(e.getMessage(), e);
			} finally {
				if (fileReader != null) {
					try {
						fileReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
}
