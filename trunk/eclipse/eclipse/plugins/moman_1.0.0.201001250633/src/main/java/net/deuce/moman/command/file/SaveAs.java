package net.deuce.moman.command.file;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SaveAs extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.saveAs";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setFilterNames(new String[] {"XML File"});
        String fileSelected = dialog.open();

        if (fileSelected != null) {
        }
		return null;
	}

}