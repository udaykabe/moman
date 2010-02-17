package net.deuce.moman.model.version;

import java.util.List;

import net.deuce.moman.transaction.model.TransactionStatus;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocumentConverter3 implements DocumentConverter {

	@SuppressWarnings("unchecked")
	@Override
	public void convert(Document document) {
		List<Element> transactions = (List<Element>)document.selectNodes("/moman/transactions/transaction");
		for (Element el : transactions) {
			TransactionStatus status = el.element("extid") != null ?
					TransactionStatus.cleared : TransactionStatus.open;
			el.addElement("status").setText(status.name());
		}
		
		transactions = (List<Element>)document.selectNodes("/moman/repeating-transactions/transaction");
		for (Element el : transactions) {
			el.addElement("status").setText(TransactionStatus.open.name());
		}
	}

}
