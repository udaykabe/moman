package net.deuce.moman.command.file;

import java.io.File;

import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class SaveAs extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.saveAs";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();

		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		
		String lastDirectory = ServiceNeeder.instance().getServiceContainer().getLastUsedDirectory().getAbsolutePath();
        dialog.setFilterExtensions(new String[] {"*.xml"});
		dialog.setFilterPath(lastDirectory);
        dialog.setFilterNames(new String[] {"XML File"});
        dialog.setOverwrite(true);
        String fileSelected = dialog.open();

        if (fileSelected != null) {
        	if (!fileSelected.endsWith(".xml")) {
        		/*
        		StringBuffer sb = new StringBuffer(fileSelected);
        		sb.reverse();
        		int dotPos = sb.indexOf(".");
        		sb.replace(0, dotPos+1, "");
        		sb.reverse();
        		fileSelected = sb.toString() + ".xml";
        		*/
        		fileSelected = fileSelected + ".xml";
        		
        	}
        	serviceContainer.saveEntities(new File(fileSelected));
        }
		return null;
	}

}