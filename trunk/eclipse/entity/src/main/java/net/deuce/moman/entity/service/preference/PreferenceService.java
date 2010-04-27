package net.deuce.moman.entity.service.preference;

import java.io.File;
import java.util.List;

public interface PreferenceService {

	public List<String> getRecentlyOpenedFiles();
	
	public void addRecentlyOpenedFiles(String file);
	
	public void clearRecentlyOpenedFiles();
	
	public File getLastUsedDirectory();
	
	public void setLastUsedDirectory(File f);
	
	public File getLastUsedImportDirectory();
	
	public void setLastUsedImportDirectory(File f);
	
	public int getInt(String name);
	
	public String getString(String name);
}
