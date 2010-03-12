package net.deuce.moman.action;

import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.internal.actions.CommandAction;
import org.eclipse.ui.services.IServiceLocator;

public class MomanCommandAction extends CommandAction implements
		IWorkbenchAction {

	public MomanCommandAction(IServiceLocator serviceLocator, String commandIdIn) {
		super(serviceLocator, commandIdIn);
	}

}
