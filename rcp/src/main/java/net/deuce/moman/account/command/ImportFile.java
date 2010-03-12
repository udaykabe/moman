package net.deuce.moman.account.command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.preference.PreferenceService;
import net.deuce.moman.transaction.ui.SwtFileImportingTransactionProcessor;

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

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private PreferenceService preferenceService = ServiceProvider.instance().getPreferenceService();

	public ImportFile() {
		super(false);
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Account account = null;
		if (accountService.getEntities().size() == 1) {
			account = accountService.getEntities().get(0);
		} else {
			SelectAccountDialog dialog = new SelectAccountDialog(window
					.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				account = dialog.getEntity();
			}
		}

		if (account != null) {
			FileReader fileReader = null;
			try {
				Shell shell = HandlerUtil.getActiveWorkbenchWindow(event)
						.getShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);

				String lastDirectory = preferenceService
						.getLastUsedImportDirectory().getAbsolutePath();
				fileDialog.setFilterPath(lastDirectory);
				fileDialog.setFilterExtensions(new String[] { "*.ofx" });
				fileDialog.setFilterNames(new String[] { "OFX File" });
				String fileSelected = fileDialog.open();

				if (fileSelected != null) {

					preferenceService.setLastUsedImportDirectory(new File(
							fileSelected).getParentFile());
					new SwtFileImportingTransactionProcessor(window.getShell(),
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
