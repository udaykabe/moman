package net.deuce.moman.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.deuce.moman.model.account.Account;
import net.deuce.moman.model.account.AccountBuilder;
import net.deuce.moman.model.envelope.Bill;
import net.deuce.moman.model.envelope.BillBuilder;
import net.deuce.moman.model.envelope.Envelope;
import net.deuce.moman.model.envelope.EnvelopeBuilder;
import net.deuce.moman.model.fi.FinancialInstitution;
import net.deuce.moman.model.fi.FinancialInstitutionBuilder;
import net.deuce.moman.model.rules.Rule;
import net.deuce.moman.model.rules.TransactionRuleBuilder;
import net.deuce.moman.model.transaction.InternalTransaction;
import net.deuce.moman.model.transaction.TransactionBuilder;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class Registry {
	
	private static Registry __instance = null;
	
	public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
	public static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

	private Random random = new Random(System.currentTimeMillis());
	private Map<String, Account> accounts = new HashMap<String, Account>();
	private Map<String, Envelope> envelopes = new HashMap<String, Envelope>();
	private Map<String, Bill> bills = new HashMap<String, Bill>();
	private Map<String, FinancialInstitution> financialInstitutionsByName = new HashMap<String, FinancialInstitution>();
	private Map<String, FinancialInstitution> financialInstitutions = new HashMap<String, FinancialInstitution>();
	private Map<String, Transaction> transactions = new HashMap<String, Transaction>();
	private Map<String, Transaction> importedTransactions = new HashMap<String, Transaction>();
	private Map<String, Rule> transactionRules = new HashMap<String, Rule>();
	private Envelope rootEnvelope;
	private Envelope availableEnvelope;
	private File activeFile;
	
	private EntityMonitor<Account> accountMonitor = new EntityMonitor<Account>();
	private EntityMonitor<FinancialInstitution> financialInstitutionMonitor = new EntityMonitor<FinancialInstitution>();
	private EntityMonitor<Envelope> envelopeMonitor = new EntityMonitor<Envelope>();
	private EntityMonitor<Envelope> billMonitor = new EntityMonitor<Envelope>();
	private EntityMonitor<Transaction> transactionMonitor = new EntityMonitor<Transaction>();
	private EntityMonitor<Transaction> importedTransactionMonitor = new EntityMonitor<Transaction>();
	private EntityMonitor<Rule> transactionRulesMonitor = new EntityMonitor<Rule>();
	
	private EntityMonitor[] monitors = {
		accountMonitor,
		financialInstitutionMonitor,
		envelopeMonitor,
		billMonitor,
		transactionMonitor,
		importedTransactionMonitor,
		transactionRulesMonitor,
	};
	
	private TableViewer accountViewer;
	private TableViewer registerViewer;
	private TableViewer importViewer;
	private TreeViewer envelopeViewer;
	private TableViewer billViewer;
	private Font standardFont = new Font(Display.getCurrent(), new FontData[]{new FontData("Lucida Grande", 14, SWT.NORMAL)});
	
	private EntityBuilder[] entityBuilders = new EntityBuilder[]{
			new FinancialInstitutionBuilder(),
			new AccountBuilder(),
			new TransactionBuilder(),
			new EnvelopeBuilder(),
			new BillBuilder(),
			new TransactionRuleBuilder(),
	};
	
	private Registry() {}
	
	public static Registry instance() { 
		if (__instance == null) {
			__instance = new Registry();
			__instance.loadEntities(new File("/Users/nbolton/Documents/moman.xml"));
			//__instance.loadImportedTransactions(new File("/Users/nbolton/src/personal/moman-rcp/importedTransactions.xml"));
		}
		return __instance;
	}
	
	public Font getStandardFont() {
		return standardFont;
	}
	
	public TableViewer getAccountViewer() {
		return accountViewer;
	}

	public void setAccountViewer(TableViewer accountViewer) {
		this.accountViewer = accountViewer;
	}

	public TableViewer getImportViewer() {
		return importViewer;
	}

	public void setImportViewer(TableViewer importViewer) {
		this.importViewer = importViewer;
	}

	public void addAccountListener(EntityListener<Account> listener) {
		accountMonitor.addListener(listener);
	}
	
	public TableViewer getRegisterViewer() {
		return registerViewer;
	}

	public void setRegisterViewer(TableViewer registerViewer) {
		this.registerViewer = registerViewer;
	}

	public TableViewer getBillViewer() {
		return billViewer;
	}

	public void setBillViewer(TableViewer billViewer) {
		this.billViewer = billViewer;
	}

	public TreeViewer getEnvelopeViewer() {
		return envelopeViewer;
	}

	public void setEnvelopeViewer(TreeViewer envelopeViewer) {
		this.envelopeViewer = envelopeViewer;
	}

	public void addFinancialInstitutionListener(EntityListener<FinancialInstitution> listener) {
		financialInstitutionMonitor.addListener(listener);
	}
	
	public void removeFinancialInstitutionListener(EntityListener<FinancialInstitution> listener) {
		financialInstitutionMonitor.removeListener(listener);
	}
	
	public void addBillListener(EntityListener<Envelope> listener) {
		billMonitor.addListener(listener);
	}
	
	public void removeBillListener(EntityListener<Envelope> listener) {
		billMonitor.removeListener(listener);
	}
	
	public void addEnvelopeListener(EntityListener<Envelope> listener) {
		envelopeMonitor.addListener(listener);
	}
	
	public void removeEnvelopeListener(EntityListener<Envelope> listener) {
		envelopeMonitor.removeListener(listener);
	}
	
	public void addTransactionListener(EntityListener<Transaction> listener) {
		transactionMonitor.addListener(listener);
	}
	
	public void removeTransactionListener(EntityListener<Transaction> listener) {
		transactionMonitor.removeListener(listener);
	}
	
	public void addImportedTransactionListener(EntityListener<Transaction> listener) {
		importedTransactionMonitor.addListener(listener);
	}
	
	public void removeImportedTransactionListener(EntityListener<Transaction> listener) {
		importedTransactionMonitor.removeListener(listener);
	}
	
	public void addTransactionRulesListener(EntityListener<Rule> listener) {
		transactionRulesMonitor.addListener(listener);
	}
	
	public void removeTransactionRulesListener(EntityListener<Rule> listener) {
		transactionRulesMonitor.removeListener(listener);
	}
	
	public void notifyAccountListenersOfAdditions() {
		accountMonitor.fireEntityAdded(null);
	}
	
	public void notifyAccountListenersOfRemovals() {
		accountMonitor.fireEntityRemoved(null);
	}
	
	public void notifyAccountListenersOfChanges() {
		accountMonitor.fireEntityChanged(null);
	}
	
	public void notifyEnvelopeListenersOfAdditions() {
		envelopeMonitor.fireEntityAdded(null);
	}
	
	public void notifyEnvelopeListenersOfRemovals() {
		envelopeMonitor.fireEntityRemoved(null);
	}
	
	public void notifyEnvelopeListenersOfChanges() {
		envelopeMonitor.fireEntityChanged(null);
	}
	
	public void notifyFinancialInstitutionListenersOfAdditions() {
		financialInstitutionMonitor.fireEntityAdded(null);
	}
	
	public void notifyFinancialInstitutionListenersOfRemovals() {
		financialInstitutionMonitor.fireEntityRemoved(null);
	}
	
	public void notifyFinancialInstitutionListenersOfChanges() {
		financialInstitutionMonitor.fireEntityChanged(null);
	}
	
	public void notifyTransactionListenersOfAdditions() {
		transactionMonitor.fireEntityAdded(null);
	}
	
	public void notifyTransactionListenersOfRemovals() {
		transactionMonitor.fireEntityRemoved(null);
	}
	
	public void notifyTransactionListenersOfChanges() {
		transactionMonitor.fireEntityChanged(null);
	}
	
	public void notifyTransactionRulesListenersOfAdditions() {
		transactionRulesMonitor.fireEntityAdded(null);
	}
	
	public void notifyTransactionRulesListenersOfRemovals() {
		transactionRulesMonitor.fireEntityRemoved(null);
	}
	
	public void notifyTransactionRulesListenersOfChanges() {
		transactionRulesMonitor.fireEntityChanged(null);
	}
	
	public void notifyBillListenersOfAdditions() {
		billMonitor.fireEntityAdded(null);
	}
	
	public void notifyBillListenersOfRemovals() {
		billMonitor.fireEntityRemoved(null);
	}
	
	public void notifyBillListenersOfChanges() {
		billMonitor.fireEntityChanged(null);
	}
	
	public File getActiveFile() {
		return activeFile;
	}
	
	public void loadEntities(File f) {
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read(new URL("file:///"+f.getAbsolutePath()));
			Element root = document.getRootElement();
			
			for (EntityBuilder builder : entityBuilders) {
				builder.parseXml(this, root);
			}
			
			Envelope parent;
			for (Envelope env : getEnvelopes()) {
				
				if (env.getParentId() != null) {
					parent = getEnvelope(env.getParentId());
					env.setParent(parent);
					parent.addChild(env);
				}
				
				for (Transaction t : env.getTransactions()) {
					t.addSplit(env);
				}
			}
			
			for (Bill bill : getBills()) {
				parent = getEnvelope(bill.getParentId());
				bill.setParent(parent);
				parent.addChild(bill);
			}
			activeFile = f;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        
	}
	
	public void loadImportedTransactions(File f) {
		SAXReader reader = new SAXReader();
        try {
			Document document = reader.read(new URL("file:///"+f.getAbsolutePath()));
			Element root = document.getRootElement();
			new TransactionBuilder().parseImportXml(this, root);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        
	}
	
	public void saveEntities() {
		saveEntities(activeFile);
	}
	
	public void saveEntities(File f) {
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        
		try {
			Document doc = DocumentHelper.createDocument();
			doc.addElement("moman");
			
			for (EntityBuilder builder : entityBuilders) {
				builder.buildXml(this, doc);
			}
		
			writer = new XMLWriter(System.out, format);
		    writer.write(doc);
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
			new TransactionBuilder().buildImportXml(this, doc);
		
			writer = new XMLWriter(System.out, format);
		    writer.write(doc);
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
	public void setMonitor(boolean monitoring) {
		for (EntityMonitor em : monitors) {
			em.setMonitoring(monitoring);
		}
	}

	public List<Rule> getTransactionRules() {
		List<Rule> list = new ArrayList<Rule>(transactionRules.values());
		Collections.sort(list);
		return list;
	}
	
	public Rule getTransactionRule(String id) {
		Rule rule = transactionRules.get(id);
		if (rule == null) {
			throw new RuntimeException("No transaction rule exists with ID " + id);
		}
		return rule;
	}
	
	public void addTransactionRule(Rule rule) {
		if (rule.getId() == null) {
			rule.setId(createUuid());
		}
		
		if (rule.getId() != null && transactionRules.containsKey(rule.getId())) {
			throw new RuntimeException("Duplicate transaction rule uuid: " + rule.getId());
		}
		
		transactionRules.put(rule.getId(), rule);
		transactionRulesMonitor.fireEntityAdded(rule);
		rule.setMonitor(transactionRulesMonitor);
	}
	
	public void removeTransactionRule(Rule rule) {
		transactionRules.remove(rule.getId());
		transactionRulesMonitor.fireEntityRemoved(rule);
	}	
	
	public List<Account> getAccounts() {
		List<Account> list = new ArrayList<Account>(accounts.values());
		Collections.sort(list);
		return list;
	}
	
	public Account getAccount(String id) {
		Account account = accounts.get(id);
		if (account == null) {
			throw new RuntimeException("No account exists with ID " + id);
		}
		return account;
	}
	
	public void addAccount(Account account) {
		if (account.getId() == null) {
			account.setId(createUuid());
		}
		
		if (account.getId() != null && accounts.containsKey(account.getId())) {
			throw new RuntimeException("Duplicate account uuid: " + account.getId());
		}
		
		accounts.put(account.getId(), account);
		accountMonitor.fireEntityAdded(account);
		account.setMonitor(accountMonitor);
	}
	
	public void removeAccount(Account account) {
		accounts.remove(account.getId());
		accountMonitor.fireEntityRemoved(account);
	}
	
	public void setRootEnvelope(Envelope envelope) {
		this.rootEnvelope = envelope;
	}
	
	public Envelope getRootEnvelope() {
		return rootEnvelope;
	}
	
	public Envelope getAvailableEnvelope() {
		return availableEnvelope;
	}

	public void setAvailableEnvelope(Envelope availableEnvelope) {
		this.availableEnvelope = availableEnvelope;
	}

	public List<Envelope> getEnvelopes() {
		List<Envelope> list = new LinkedList<Envelope>(envelopes.values());
		Collections.sort(list);
		return list;
	}
	
	public Envelope getEnvelope(String id) {
		Envelope envelope = envelopes.get(id);
		if (envelope == null) {
			throw new RuntimeException("No envelope exists with ID " + id);
		}
		return envelope;
	}
	
	public void addEnvelope(Envelope envelope) {
		addEnvelope(envelope, null);
	}
	
	public void addEnvelope(Envelope envelope, Envelope parent) {
		if (envelope.getId() == null) {
			envelope.setId(createUuid());
		}
		
		if (envelope.getId() != null && envelopes.containsKey(envelope.getId())) {
			throw new RuntimeException("Duplicate envelope uuid: " + envelope.getId());
		}
		
		if (parent != null) {
			envelope.setParent(parent);
			for (Envelope child : parent.getChildren()) {
				if (!child.getId().equals(envelope.getId()) && child.getName().equals(envelope.getName())) {
					throw new RuntimeException("Duplicate envelope name: " + envelope.getName());
				}
			}
			parent.addChild(envelope);
		}
		
		envelopes.put(envelope.getId(), envelope);
		envelopeMonitor.fireEntityAdded(envelope);
		envelope.setMonitor(envelopeMonitor);
	}
	
	public void removeEnvelope(Envelope envelope) {
		
		if (!envelope.isEditable()) return;
		
		Envelope parent = envelope.getParent();
		if (parent != null) {
			parent.removeChild(envelope);
		}
		List<Envelope> children = new LinkedList<Envelope>(envelope.getChildren());
		
		for (Envelope child : children) {
			removeEnvelope(child);
		}
		
		for (Rule rule : transactionRules.values()) {
			if (rule.getEnvelope().getId().equals(envelope.getId())) {
				rule.setEnvelope(availableEnvelope);
			}
		}
		envelopes.remove(envelope.getId());
		envelopeMonitor.fireEntityRemoved(envelope);
	}
	
	public List<Bill> getBills() {
		List<Bill> list = new LinkedList<Bill>(bills.values());
		Collections.sort(list);
		return list;
	}
	
	public Envelope findEnvelope(String id) {
		Envelope env = envelopes.get(id);
		if (env == null) {
			env = bills.get(id);
		}
		return env;
	}
	
	public Bill getBill(String id) {
		Bill bill = bills.get(id);
		if (bill == null) {
			throw new RuntimeException("No bill exists with ID " + id);
		}
		return bill;
	}
	
	public void addBill(Bill bill) {
		addBill(bill, null);
	}
	
	public void addBill(Bill bill, Envelope parent) {
		if (bill.getId() == null) {
			bill.setId(createUuid());
		}
		
		if (bill.getId() != null && bills.containsKey(bill.getId())) {
			throw new RuntimeException("Duplicate bill uuid: " + bill.getId());
		}
		
		if (parent != null) {
			bill.setParent(parent);
			for (Envelope child : parent.getChildren()) {
				if (child.getName().equals(bill.getName())) {
					throw new RuntimeException("Duplicate bill name: " + bill.getName());
				}
			}
			parent.addChild(bill);
		}
		
		bills.put(bill.getId(), bill);
		billMonitor.fireEntityAdded(bill);
		bill.setMonitor((EntityMonitor<Envelope>)billMonitor);
	}
	
	public void removeBill(Bill bill) {
		
		Envelope parent = bill.getParent();
		parent.removeChild(bill);
		
		for (Rule rule : transactionRules.values()) {
			if (rule.getEnvelope().getId().equals(bill.getId())) {
				rule.setEnvelope(availableEnvelope);
			}
		}
		bills.remove(bill.getId());
		billMonitor.fireEntityRemoved(bill);
	}
	
	public FinancialInstitution getFinancialInstitution(String id) {
		FinancialInstitution fi = financialInstitutions.get(id);
		if (fi == null) {
			throw new RuntimeException("No financial institution exists with ID: " + id);
		}
		return fi;
	}
	
	public boolean doesFinancialInstitutionExistByName(String name) {
		return financialInstitutionsByName.containsKey(name);
	}
	
	public FinancialInstitution getFinancialInstitutionByName(String name) {
		FinancialInstitution fi = financialInstitutionsByName.get(name);
		if (fi == null) {
			throw new RuntimeException("No financial institution exists with name: " + name);
		}
		return fi;
	}
	
	public List<FinancialInstitution> getFinancialInstitutions() {
		return new LinkedList<FinancialInstitution>(financialInstitutions.values());
	}
	
	public void addFinancialInstitution(FinancialInstitution financialInstitution) {
		if (financialInstitution.getId() == null) {
			financialInstitution.setId(createUuid());
		}
		
		if (financialInstitution.getId() != null && financialInstitutions.containsKey(financialInstitution.getId())) {
			throw new RuntimeException("Duplicate financial institution uuid: " + financialInstitution.getId());
		}
		
		if (financialInstitutionsByName.containsKey(financialInstitution.getName())) {
			throw new RuntimeException("Duplicate financial institution name: " + financialInstitution.getName());
		}
		financialInstitutionsByName.put(financialInstitution.getName(), financialInstitution);
		
		financialInstitutions.put(financialInstitution.getId(), financialInstitution);
		financialInstitutionMonitor.fireEntityAdded(financialInstitution);
		financialInstitution.setMonitor(financialInstitutionMonitor);
	}
	
	public void removeFinancialInstitution(FinancialInstitution financialInstitution) {
		financialInstitutions.remove(financialInstitution.getId());
		financialInstitutionMonitor.fireEntityRemoved(financialInstitution);
	}
	
	public Transaction getTransaction(String id) {
		Transaction t = transactions.get(id);
		if (t == null) {
			throw new RuntimeException("No transaction exists with ID " + id);
		}
		return t;
	}
	
	public List<Transaction> getTransactions() {
		return new LinkedList<Transaction>(transactions.values());
	}
	
	public void addTransaction(Transaction transaction) {
		if (transaction.getId() == null) {
			transaction.setId(createUuid());
		}
		
		if (transaction.getId() != null && transactions.containsKey(transaction.getId())) {
			throw new RuntimeException("Duplicate transaction uuid: " + transaction.getId());
		}
		
		transactions.put(transaction.getId(), transaction);
		transactionMonitor.fireEntityAdded(transaction);
		transaction.setMonitor(transactionMonitor);
	}
	
	public void removeTransaction(Transaction transaction) {
		transactions.remove(transaction.getId());
		transactionMonitor.fireEntityRemoved(transaction);
	}
	
	public Transaction getImportedTransaction(String id) {
		Transaction t = importedTransactions.get(id);
		if (t == null) {
			throw new RuntimeException("No imported transaction exists with ID " + id);
		}
		return t;
	}
	
	public void addImportedTransaction(Transaction importedTransaction) {
		if (importedTransaction.getId() != null && importedTransactions.containsKey(importedTransaction.getId())) {
			throw new RuntimeException("Duplicate imported transaction uuid: " + importedTransaction.getId());
		}
		
		if (importedTransaction.getId() == null) {
			importedTransaction.setId(createUuid());
		}
		importedTransactions.put(importedTransaction.getId(), importedTransaction);
		importedTransactionMonitor.fireEntityAdded(importedTransaction);
		importedTransaction.setMonitor(importedTransactionMonitor);
	}
	
	public void removeImportedTransaction(Transaction transaction) {
		importedTransactions.remove(transaction.getId());
		importedTransactionMonitor.fireEntityRemoved(transaction);
	}
	
	public List<Transaction> getImportedTransactions() {
		List<Transaction> list = new ArrayList<Transaction>(importedTransactions.values());
		Collections.sort(list);
		return list;
	}
	
	public void setImportedTransactions(List<Transaction> importedTransactions) {
		this.importedTransactions.clear();
		if (importedTransactions != null) {
			for (Transaction t : importedTransactions) {
				if (t.getId() == null) {
					t.setId(createUuid());
				}
				this.importedTransactions.put(t.getId(), t);
			}
			importedTransactionMonitor.fireEntityAdded(null);
		} else {
			importedTransactionMonitor.fireEntityRemoved(null);
		}
	}
	
	private String createUuid() {
		return UUID.nameUUIDFromBytes(((random.nextInt()+"."+System.currentTimeMillis()).getBytes())).toString();
	}
	
}
