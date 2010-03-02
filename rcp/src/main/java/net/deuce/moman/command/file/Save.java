package net.deuce.moman.command.file;

import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;

public class Save extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.save";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();
		if (serviceContainer.getActiveFile() == null) {
			IHandlerService handlerService =
				(IHandlerService) HandlerUtil.getActiveSite(event).getService(IHandlerService.class);
			try {
				handlerService.executeCommand(SaveAs.ID, null);
			} catch (Exception e) {
				throw new ExecutionException("Save command failed", e);
			}
		} else {
			serviceContainer.saveEntities();
		}
		return null;
	}

}