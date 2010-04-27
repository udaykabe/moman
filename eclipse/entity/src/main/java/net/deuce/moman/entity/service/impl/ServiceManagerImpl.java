package net.deuce.moman.entity.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.model.EntityBuilder;
import net.deuce.moman.entity.model.ModelConstants;
import net.deuce.moman.entity.model.envelope.EnvelopeBuilder;
import net.deuce.moman.entity.model.fi.FinancialInstitutionBuilder;
import net.deuce.moman.entity.model.transaction.TransactionBuilder;
import net.deuce.moman.entity.model.version.DocumentConverterFactory;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.preference.PreferenceService;
import net.deuce.moman.entity.service.transaction.TransactionService;
import net.sf.ofx4j.domain.data.common.Transaction;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

@SuppressWarnings("unchecked")
public class ServiceManagerImpl implements ServiceManager {

	private File activeFile;
	
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
	
	@Autowired
	private PreferenceService preferenceService;
	
	public ServiceManagerImpl() {
		
	}
	
	public File getActiveFile() {
		return activeFile;
	}
	
	public List<EntityService> getServices() {
		return services;
	}

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
		//loadEntities(new File("/Users/nbolton/Documents/moman.xml"));
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
		preferenceService.setLastUsedDirectory(f.getParentFile());
		List<String> ids = startQueuingNotifications();
		clearCaches();
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read(new URL("file://"+f.getAbsolutePath()));
			Element root = document.getRootElement();
			
			DocumentConverterFactory.getInstance(
					Integer.valueOf(root.attributeValue("version")),
					ModelConstants.MOMAN_DOCUMENT_VERSION).convert(document);
			
			for (EntityBuilder builder : builders) {
				builder.parseXml(root);
			}
			
			envelopeService.bindEnvelopes();
			transactionService.bindTransferTransactions();
			
			activeFile = f;
			
	        preferenceService.addRecentlyOpenedFiles(f.getAbsolutePath());

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
			return transactionBuilder.parseImportXml(root);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        
	}
	
	public void saveEntities() {
		saveEntities(activeFile);
	}
	
	public void backupFile(File f) throws IOException {
//		 new ProcessBuilder("/Users/nbolton/Documents/backup_moman.sh").start();
	}
	
	public void saveEntities(File f) {
		
		
        preferenceService.setLastUsedDirectory(f.getParentFile());
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        
        activeFile = f;
        
		try {
			backupFile(f);
		
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("moman");
			root.addAttribute("version", ModelConstants.MOMAN_DOCUMENT_VERSION.toString());
			
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
	
	public List<String> startQueuingNotifications() {
		List<String> ids = new LinkedList<String>();
		for (EntityService service : services) {
			ids.add(service.startQueuingNotifications());
		}
		return ids;
	}
	
	public void stopQueuingNotifications(List<String> ids) {
		for (int i=0; i<services.size(); i++) {
			services.get(i).stopQueuingNotifications(ids.get(i));
		}
	}
	
	public void clearCaches() {
		for (EntityService service : services) {
			if (service.isClearable()) {
				service.clearCache();
			}
		}
	}
}
