package net.deuce.moman.account.command;

public class FullImport extends Import {

	public static final String ID = "net.deuce.moman.account.command.fullImport";

	protected boolean force() {
		return true;
	}
}
