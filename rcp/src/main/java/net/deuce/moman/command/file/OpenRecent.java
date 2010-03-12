package net.deuce.moman.command.file;

import java.io.File;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.service.ServiceManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenRecent extends AbstractHandler {

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		File file = new File(event.getParameter("file"));

		if (file.exists()) {
			serviceManager.loadEntities(file);
		}

		return null;
	}

}
