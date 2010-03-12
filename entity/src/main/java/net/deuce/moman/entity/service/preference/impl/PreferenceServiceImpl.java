package net.deuce.moman.entity.service.preference.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.deuce.moman.entity.service.preference.PreferenceService;

import org.springframework.stereotype.Component;

@Component("preferenceService")
public class PreferenceServiceImpl implements PreferenceService {
	
	private Properties properties = null;
	private Properties defaultProperties = null;
	
	// TODO make platform independent
	private File file = new File(System.getProperty("user.home"),
					"Library/Application Support/Moman/preferences.properties");
	
	private void loadPreferencesIfNecessary() {
		if (properties == null) {
			properties = new Properties();
			defaultProperties = new Properties();
			
			if (file.exists()) {
				FileReader reader = null;
				
				try {
					reader = new FileReader(file);
					properties.load(reader);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					if (reader != null) try { reader.close(); } catch (Exception e) {}
				}
			}

			File f = new File(System.getProperty("user.home"), "Documents");
			defaultProperties.setProperty("lastUsedDirectory", f.getAbsolutePath());
			f = new File(System.getProperty("user.home"), "Downloads");
			defaultProperties.setProperty("lastUsedImportDirectory", f.getAbsolutePath());
		}
	}
	
	private void save() {
		
		loadPreferencesIfNecessary();
		
		FileWriter writer = null;
		
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			writer = new FileWriter(file);
			properties.store(writer, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) try { writer.close(); } catch (Exception e) {}
		}
	}
	
	public String getString(String name) {
		loadPreferencesIfNecessary();
		String value = properties.getProperty(name);
		if (value == null) {
			value = defaultProperties.getProperty(name);
		}
		return value;
	}
	
	public int getInt(String name) {
		int value = 0;
		String strValue = getString(name);
		if (strValue != null) {
			value = Integer.valueOf(strValue);
		}
		return value;
	}

	public List<String> getRecentlyOpenedFiles() {
		loadPreferencesIfNecessary();
		List<String> list = new LinkedList<String>();
		String recentlyOpenedFiles = getString("recentlyOpenedFiles");
		if (recentlyOpenedFiles != null) {
			String[] split = recentlyOpenedFiles.split(",");
			for (String file : split) {
				list.add(file);
			}
		}
		return list;
	}

	public void addRecentlyOpenedFiles(String file) {
		List<String> list = getRecentlyOpenedFiles();
		while (list.size() >= 5) {
			list.remove(list.size() - 1);
		}
		list.add(0, file);

		StringBuffer sb = new StringBuffer();
		for (String s : list) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			sb.append(s);
		}
		properties.setProperty("recentlyOpenedFiles", sb.toString());
		save();
	}

	public void clearRecentlyOpenedFiles() {
		loadPreferencesIfNecessary();
		properties.setProperty("recentlyOpenedFiles", "");
		save();
	}

	public File getLastUsedDirectory() {
		return new File(getString("lastUsedDirectory"));
	}

	public void setLastUsedDirectory(File f) {
		loadPreferencesIfNecessary();
		properties.setProperty("lastUsedDirectory", f.getAbsolutePath());
		save();
	}

	public File getLastUsedImportDirectory() {
		return new File(getString("lastUsedImportDirectory"));
	}

	public void setLastUsedImportDirectory(File f) {
		loadPreferencesIfNecessary();
		properties.setProperty("lastUsedImportDirectory", f.getAbsolutePath());
		save();
	}

}
