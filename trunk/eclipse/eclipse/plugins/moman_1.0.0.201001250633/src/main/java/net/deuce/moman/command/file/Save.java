package net.deuce.moman.command.file;

import net.deuce.moman.model.Registry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class Save extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.file.save";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Registry.instance().saveEntities();
		return null;
	}

}