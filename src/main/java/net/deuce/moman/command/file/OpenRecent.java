package net.deuce.moman.command.file;

import java.io.File;

import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenRecent extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		File file = new File(event.getParameter("file"));
		
		if (file.exists()) {
	    	ServiceNeeder.instance().getServiceContainer().loadEntities(file);
		}

		return null;
	}

}
