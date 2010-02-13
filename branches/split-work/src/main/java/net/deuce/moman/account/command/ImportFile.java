package net.deuce.moman.account.command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.service.FileImportingTransactionProcessor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		List<Account> accounts = getAccounts(window);
		
		if (accounts.size() == 0) return null;
		
		FileReader fileReader = null;
		
		try {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			
			String lastDirectory = ServiceNeeder.instance().getServiceContainer().getLastUsedImportDirectory().getAbsolutePath();
			dialog.setFilterPath(lastDirectory);
	        dialog.setFilterExtensions(new String[] {"*.ofx"});
	        dialog.setFilterNames(new String[] {"OFX File"});
	        String fileSelected = dialog.open();
			
	        if (fileSelected != null) {
	        	
				ServiceNeeder.instance().getServiceContainer()
					.setLastUsedImportDirectory(new File(fileSelected).getParentFile());
				new FileImportingTransactionProcessor(window.getShell(),
						accounts.get(0), fileSelected).execute();
	        	
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExecutionException("Failed importing file", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
}
