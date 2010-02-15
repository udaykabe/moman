package net.deuce.moman.transaction.model;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.transaction.service.ImportService;
import net.deuce.moman.transaction.service.TransactionService;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionType;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionBuilder extends AbstractBuilder {
	
	@Autowired
	private TransactionService transactionService;

	@Autowired
	private EnvelopeService envelopeService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ImportService importService;
	
	@SuppressWarnings("unchecked")
	protected void parseTransaction(InternalTransaction transaction, Element e) {
		try {
			transaction.setId(e.attributeValue("id"));
			transaction.setAccount(accountService.getEntity(e.element("account").attributeValue("id")));
			transaction.setType(TransactionType.valueOf(e.elementText("type")));
			transaction.setAmount(Double.valueOf(e.elementText("amount")));
			
			if (e.element("date") != null) {
				transaction.setDate(Constants.DATE_FORMAT.parse(e.elementText("date")));
			}
			transaction.setDescription(e.elementText("desc"));
			transaction.setInitialBalance(Boolean.valueOf(e.elementText("initial-balance")));
			transaction.setMemo(e.elementText("memo"));
			transaction.setCheck(e.elementText("check"));
			transaction.setRef(e.elementText("ref"));
			
			String val = e.elementText("extid");
			if (val != null && val.length() > 0) {
				transaction.setExternalId(val);
			}
			
			val = e.elementText("balance");
			if (val != null && val.length() > 0) {
				transaction.setBalance(Double.valueOf(val));
			}
			
			Element el = e.element("etransfer");
			if (el != null) {
				transaction.setTransferTransactionId(el.attributeValue("id"));
			}
			
			List<Element> envelopeElements = e.selectNodes("split/envelope");
			if (envelopeElements != null) {
				for (Element ee : envelopeElements) {
					transaction.addSplit(
							envelopeService.findEntity(ee.attributeValue("id")),
							Double.valueOf(ee.attributeValue("amount")));
				}
			}
		} catch (Exception pe) {
			pe.printStackTrace();
			throw new RuntimeException(pe);
		} 
	}
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		InternalTransaction transaction;

		List<Element> nodes = e.selectNodes("transactions/transaction");
		for (Element n : nodes) {
			transaction = new InternalTransaction();
			parseTransaction(transaction, n);
			transactionService.addEntity(transaction, false, false);
		}
	}
	
	protected void buildTransactionXml(Element el, InternalTransaction trans) {
		el.addAttribute("id", trans.getId());
		el.addElement("account").addAttribute("id", trans.getAccount().getId());
		addElement(el, "amount", trans.getAmount().toString());
		addElement(el, "type", trans.getType().name());
		if (trans.getDate() != null) {
			addElement(el, "date", Constants.DATE_FORMAT.format(trans.getDate()));
		}
		addElement(el, "desc", trans.getDescription());
		addOptionalElement(el, "extid", trans.getExternalId());
		addOptionalBooleanElement(el, "initial-balance", trans.isInitialBalance());
		addOptionalElement(el, "balance", trans.getBalance());
		addElement(el, "memo", trans.getMemo());
		addElement(el, "check", trans.getCheck());
		addElement(el, "ref", trans.getRef());
		if (trans.getTransferTransaction() != null) {
			el.addElement("etransfer").addAttribute("id", trans.getTransferTransaction().getId());
		}
		Element sel = el.addElement("split");
		Element eel;
		for (Split item : trans.getSplit()) {
			eel = sel.addElement("envelope");
			eel.addAttribute("id", item.getEnvelope().getId());
			eel.addAttribute("amount", item.getAmount().toString());
		}
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("transactions");
		Element el;
		
		for (InternalTransaction trans : transactionService.getEntities()) {
			el = root.addElement("transaction");
			buildTransactionXml(el, trans);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Transaction> parseImportXml(Element e) {
		Transaction transaction;

		try {
			List<Transaction> transactions = new LinkedList<Transaction>();
			List<Element> nodes = e.selectNodes("transactions/transaction");
			for (Element n : nodes) {
				transaction = new Transaction();
				transaction.setId(n.elementText("extid"));
				transaction.setAmount(new Double(n.elementText("amount")));
				transaction.setTransactionType(TransactionType.valueOf(n.elementText("type")));
				transaction.setDatePosted(Constants.DATE_FORMAT.parse(n.elementText("date")));
				transaction.setName(n.elementText("desc"));
				transaction.setCheckNumber(n.elementText("check"));
				transaction.setReferenceNumber(n.elementText("ref"));
				transaction.setMemo(n.elementText("memo"));
				transactions.add(transaction);
			}
			
			return transactions;
		} catch (Exception pe) {
			pe.printStackTrace();
			throw new RuntimeException(pe);
		} 
	}
	
	public void buildImportXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("transactions");
		Element el;
		
		for (InternalTransaction trans : importService.getEntities()) {
			el = root.addElement("transaction");
			el.addAttribute("id", trans.getId());
			addElement(el, "amount", trans.getAmount().toString());
			addElement(el, "type", trans.getType());
			addElement(el, "date", Constants.DATE_FORMAT.format(trans.getDate()));
			addElement(el, "desc", trans.getDescription());
			addOptionalBooleanElement(el, "extid", trans.isInitialBalance());
			addOptionalElement(el, "extid", trans.getExternalId());
			addElement(el, "memo", trans.getMemo());
			addElement(el, "check", trans.getCheck());
			addElement(el, "ref", trans.getRef());
		}
	}
	
}
