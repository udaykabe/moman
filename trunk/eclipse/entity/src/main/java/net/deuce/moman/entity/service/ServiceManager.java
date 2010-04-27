package net.deuce.moman.entity.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sf.ofx4j.domain.data.common.Transaction;

public interface ServiceManager {

	public File getActiveFile();
	
	public void initialize();
	
	public void loadDefaultEnvelopes();
	
	public void loadFinancialInstitutions();
	
	public void loadEntities(File f);
	
	public List<Transaction> loadImportedTransactions(File f);
	
	public void saveEntities();
	
	public void backupFile(File f) throws IOException;
	
	public void saveEntities(File f);
	
	public void saveImportTransactions(File f);
	
	public List<String> startQueuingNotifications();
	
	public void stopQueuingNotifications(List<String> ids);
	
	public void clearCaches();
	
}
