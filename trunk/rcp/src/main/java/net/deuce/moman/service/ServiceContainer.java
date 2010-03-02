package net.deuce.moman.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.model.EnvelopeBuilder;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.fi.model.FinancialInstitutionBuilder;
import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.version.DocumentConverterFactory;
import net.deuce.moman.transaction.model.TransactionBuilder;
import net.deuce.moman.transaction.service.TransactionService;
import net.sf.ofx4j.domain.data.common.Transaction;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ServiceContainer {

	private File activeFile;
	private IPreferenceStore preferenceStore;
	
	@SuppressWarnings("unchecked")
	private List<EntityService> services = new LinkedList<EntityService>();
	private List<EntityBuilder> builders = new LinkedList<EntityBuilder>();
	
	@Autowired
	private EnvelopeService envelopeService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionBuilder transactionBuilder;
	
	@Autowired
	private FinancialInstitutionBuilder financialInstitutionBuilder;
	
	@Autowired
	private EnvelopeBuilder envelopeBuilder;
	
	public ServiceContainer() {
		
	}
	
	public IPreferenceStore getPreferenceStore() {
	    if (preferenceStore == null) {
	    	File f = new File(System.getProperty("user.home"), "Library/Application Support/Moman/preferences.properties");
	    	preferenceStore = new PreferenceStore(f.getAbsolutePath());
	    	
	    	f = new File(System.getProperty("user.home"), "Documents");
	    	preferenceStore.setDefault("lastUsedDirectory", f.getAbsolutePath());
	    	f = new File(System.getProperty("user.home"), "Downloads");
	    	preferenceStore.setDefault("lastUsedImportDirectory", f.getAbsolutePath());
	    }
	    return preferenceStore;
	}
	
	public List<String> getRecentlyOpenedFiles() {
		List<String> list = new LinkedList<String>();
		String recentlyOpenedFiles = getPreferenceStore().getString("recentlyOpenedFiles");
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
		getPreferenceStore().setValue("recentlyOpenedFiles", sb.toString());
	}
	
	public void clearRecentlyOpenedFiles() {
		getPreferenceStore().setValue("recentlyOpenedFiles", "");
	}
	
	public File getLastUsedDirectory() {
		return new File(getPreferenceStore().getString("lastUsedDirectory"));
	}
	
	public void setLastUsedDirectory(File f) {
		getPreferenceStore().setValue("lastUsedDirectory", f.getAbsolutePath());
	}
	
	public File getLastUsedImportDirectory() {
		return new File(getPreferenceStore().getString("lastUsedImportDirectory"));
	}
	
	public void setLastUsedImportDirectory(File f) {
		getPreferenceStore().setValue("lastUsedImportDirectory", f.getAbsolutePath());
	}
	
	public File getActiveFile() {
		return activeFile;
	}
	
	@SuppressWarnings("unchecked")
	public List<EntityService> getServices() {
		return services;
	}

	@SuppressWarnings("unchecked")
	public void setServices(List<EntityService> services) {
		this.services = services;
	}

	public List<EntityBuilder> getBuilders() {
		return builders;
	}

	public void setBuilders(List<EntityBuilder> builders) {
		this.builders = builders;
	}
	
	public void initialize() {
		loadDefaultEnvelopes();
		loadFinancialInstitutions();
		loadEntities(new File("/Users/nbolton/Documents/moman.xml"));
	}
	
	public void loadDefaultEnvelopes() {
		SAXReader reader = new SAXReader();
        try {
        	ClassPathResource resource = new ClassPathResource("default-envelopes.xml");
			Document document = reader.read(resource.getURL());
			Element root = document.getRootElement();
			
			envelopeBuilder.parseDefaultEnvelopesXml(root);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void loadFinancialInstitutions() {
		SAXReader reader = new SAXReader();
        try {
        	ClassPathResource resource = new ClassPathResource("financial-institutions.xml");
			Document document = reader.read(resource.getURL());
			Element root = document.getRootElement();
			
			financialInstitutionBuilder.parseXml(root);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void loadEntities(File f) {
        setLastUsedDirectory(f.getParentFile());
		List<String> ids = startQueuingNotifications();
		clearCaches();
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read(new URL("file:///"+f.getAbsolutePath()));
			Element root = document.getRootElement();
			
			DocumentConverterFactory.getInstance(
					Integer.valueOf(root.attributeValue("version")),
					Constants.MOMAN_DOCUMENT_VERSION).convert(document);
			
			for (EntityBuilder builder : builders) {
				builder.parseXml(root);
			}
			
			envelopeService.bindEnvelopes();
			transactionService.bindTransferTransactions();
			
			activeFile = f;
			
	        addRecentlyOpenedFiles(f.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			stopQueuingNotifications(ids);
		}
        
	}
	
	public List<Transaction> loadImportedTransactions(File f) {
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read(new URL("file:///"+f.getAbsolutePath()));
			Element root = document.getRootElement();
			return new TransactionBuilder().parseImportXml(root);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
	}
	
	public void saveEntities() {
		saveEntities(activeFile);
	}
	
	public void backupFile(File f) throws IOException {
		 Process p = new ProcessBuilder("/Users/nbolton/Documents/backup_moman.sh").start();
	}
	
	public void saveEntities(File f) {
		
		
        setLastUsedDirectory(f.getParentFile());
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        
        activeFile = f;
        
		try {
			backupFile(f);
		
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("moman");
			root.addAttribute("version", Constants.MOMAN_DOCUMENT_VERSION.toString());
			
			for (EntityBuilder builder : builders) {
				builder.buildXml(doc);
			}
		
//			writer = new XMLWriter(System.out, format);
//		    writer.write(doc);
		    
			writer = new XMLWriter(new FileOutputStream(f), format);
	        writer.write(doc);
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void saveImportTransactions(File f) {
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        
		try {
			Document doc = DocumentHelper.createDocument();
			doc.addElement("moman");
			transactionBuilder.buildImportXml(doc);
		
//			writer = new XMLWriter(System.out, format);
//		    writer.write(doc);
			writer = new XMLWriter(new FileOutputStream(f), format);
	        writer.write(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> startQueuingNotifications() {
		List<String> ids = new LinkedList<String>();
		for (EntityService service : services) {
			ids.add(service.startQueuingNotifications());
		}
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	public void stopQueuingNotifications(List<String> ids) {
		for (int i=0; i<services.size(); i++) {
			EntityService service = services.get(i);
			services.get(i).stopQueuingNotifications(ids.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void clearCaches() {
		for (EntityService service : services) {
			if (service.isClearable()) {
				service.clearCache();
			}
		}
	}
}
