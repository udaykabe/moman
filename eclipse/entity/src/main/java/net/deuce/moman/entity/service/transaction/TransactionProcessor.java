package net.deuce.moman.entity.service.transaction;

import java.util.Date;

import net.deuce.moman.entity.model.account.Account;

public interface TransactionProcessor extends Runnable {
	
	public Account getAccount();
	
	public boolean execute();
	
	public Date getLastDownloadedDate();
	
}
