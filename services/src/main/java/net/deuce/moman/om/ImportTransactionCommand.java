package net.deuce.moman.om;

import net.deuce.moman.job.Command;

import java.util.Date;

public interface ImportTransactionCommand extends Command {
	
	public Account getAccount();
	
	public Date getLastDownloadedDate();
	
}
