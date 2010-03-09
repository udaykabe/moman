package net.deuce.moman.model.version;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public class DocumentConverter2 implements DocumentConverter {

	@SuppressWarnings("unchecked")
	@Override
	public void convert(Document document) {
		Map<String, String> transactionMap = new HashMap<String, String>();
		List<Element> envelopes = (List<Element>)document.selectNodes("/moman/envelopes/envelope");
		
		if (envelopes != null) {
			for (Element el : envelopes) {
				List<Attribute> transactionIds = (List<Attribute>)el.selectNodes("transactions/transaction/@id");
				String eid = el.attributeValue("id");
				for (Attribute id : transactionIds) {
					transactionMap.put(id.getText(), eid);
				}
				Element transactionsElement = (Element) el.selectSingleNode("transactions");
				el.remove(transactionsElement);
			}
		}
		
		List<Element> transactions = (List<Element>)document.selectNodes("/moman/transactions/transaction");
		if (transactions != null) {
			Element envelopeElement;
			String tid;
			String amount;
			for (Element el : transactions) {
				tid = el.attributeValue("id");
				amount = el.elementText("amount");
				envelopeElement = el.addElement("split").addElement("envelope");
				envelopeElement.addAttribute("id", transactionMap.get(tid));
				envelopeElement.addAttribute("amount", amount);
			}
		}
		
		List<Element> rules = (List<Element>)document.selectNodes("/moman/transactions/rule");
		if (rules != null) {
			Element envelopeElement;
			Element splitElement;
			for (Element el : rules) {
				envelopeElement = (Element) el.selectSingleNode("envelope");
				el.remove(envelopeElement);
				envelopeElement.addAttribute("amount", "0");
				splitElement = el.addElement("split");
				splitElement.add(envelopeElement);
			}
		}
		
		transactions = (List<Element>)document.selectNodes("/moman/repeating-transactions/transaction");
		if (transactions != null) {
			Element envelopeElement;
			String tid;
			String amount;
			for (Element el : transactions) {
				tid = el.attributeValue("id");
				amount = el.elementText("amount");
				envelopeElement = el.addElement("split").addElement("envelope");
				envelopeElement.addAttribute("id", transactionMap.get(tid));
				envelopeElement.addAttribute("amount", amount);
			}
		}
	}

}
