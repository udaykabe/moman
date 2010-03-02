package net.deuce.moman.command.file;

import java.io.File;

import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class Open extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.open";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		
		String lastDirectory = ServiceNeeder.instance().getServiceContainer().getLastUsedDirectory().getAbsolutePath();
		dialog.setFilterPath(lastDirectory);
        dialog.setFilterExtensions(new String[] {"*.xml"});
        dialog.setFilterNames(new String[] {"XML File"});
        String fileSelected = dialog.open();

        if (fileSelected != null) {
        	ServiceNeeder.instance().getServiceContainer().loadEntities(new File(fileSelected));
        }
		return null;
	}

}