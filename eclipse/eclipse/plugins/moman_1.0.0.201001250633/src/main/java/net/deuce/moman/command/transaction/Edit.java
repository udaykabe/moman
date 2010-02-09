package net.deuce.moman.command.transaction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class Edit extends AbstractHandler {

	public static final String ID = "net.deuce.moman.command.transaction.edit";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println(getClass().getName());
		return null;
	}

}