package net.deuce.moman.model.transaction;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.Registry;

import org.dom4j.Document;
import org.dom4j.Element;

public class TransactionBuilder implements EntityBuilder {

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		try {
			Transaction transaction;
			Element el;

			List<Element> nodes = e.selectNodes("transactions/transaction");
			for (Element n : nodes) {
				transaction = new Transaction();
				transaction.setId(n.attributeValue("id"));
				
				el = n.element("extid");
				if (el != null) {
					String val = el.getTextTrim();
					if (val != null && val.length() > 0) {
						transaction.setExternalId(val);
					}
				}
				transaction.setAmount(new Float(n.elementText("amount")));
				transaction.setType(n.elementText("type"));
				transaction.setDate(Registry.DATE_FORMAT.parse(n.elementText("date")));
				transaction.setDescription(n.elementText("desc"));
				transaction.setMemo(n.elementText("memo"));
				transaction.setCheck(n.elementText("check"));
				transaction.setRef(n.elementText("ref"));
				registry.addTransaction(transaction);
			}
		} catch (ParseException pe) {
			throw new RuntimeException(pe);
		}
	}
	
	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("transactions");
		Element el;
		
		for (Transaction trans : registry.getTransactions()) {
			el = root.addElement("transaction");
			el.addAttribute("id", trans.getId());
			el.addElement("amount").setText(""+trans.getAmount());
			el.addElement("type").setText(trans.getType());
			el.addElement("date").setText(Registry.DATE_FORMAT.format(trans.getDate()));
			el.addElement("desc").setText(trans.getDescription());
			addOptionalElement(el, "extid", trans.getExternalId());
			addElement(el, "memo", trans.getMemo());
			addElement(el, "check", trans.getCheck());
			addElement(el, "ref", trans.getRef());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseImportXml(Registry registry, Element e) {
		try {
			Transaction transaction;
			Element el;

			List<Transaction> transactions = new LinkedList<Transaction>();
			List<Element> nodes = e.selectNodes("transactions/transaction");
			for (Element n : nodes) {
				transaction = new Transaction();
				transaction.setId(n.attributeValue("id"));
				
				el = n.element("extid");
				if (el != null) {
					String val = el.getTextTrim();
					if (val != null && val.length() > 0) {
						transaction.setExternalId(val);
					}
				}
				transaction.setAmount(new Float(n.elementText("amount")));
				transaction.setType(n.elementText("type"));
				transaction.setDate(Registry.DATE_FORMAT.parse(n.elementText("date")));
				transaction.setDescription(n.elementText("desc"));
				transaction.addSplit(Registry.instance().getAvailableEnvelope());
				transactions.add(transaction);
			}
			registry.setImportedTransactions(transactions);
		} catch (Exception pe) {
			pe.printStackTrace();
			throw new RuntimeException(pe);
		} 
	}
	
	public void buildImportXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("transactions");
		Element el;
		
		for (Transaction trans : registry.getImportedTransactions()) {
			el = root.addElement("transaction");
			el.addAttribute("id", trans.getId());
			el.addElement("amount").setText(""+trans.getAmount());
			el.addElement("type").setText(trans.getType());
			el.addElement("date").setText(Registry.DATE_FORMAT.format(trans.getDate()));
			el.addElement("desc").setText(trans.getDescription());
			addOptionalElement(el, "extid", trans.getExternalId());
			addElement(el, "memo", trans.getMemo());
			addElement(el, "check", trans.getCheck());
			addElement(el, "ref", trans.getRef());
		}
	}
	
	private void addElement(Element el, String elementName, String textValue) {
		el.addElement(elementName).setText(textValue != null ? textValue : "");
	}
	
	private void addOptionalElement(Element el, String elementName, String textValue) {
		if (textValue != null) {
			el.addElement(elementName).setText(textValue);
		}
	}
}
