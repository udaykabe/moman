package net.deuce.moman.transaction.model;

import java.text.ParseException;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.transaction.service.RepeatingTransactionService;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepeatingTransactionBuilder extends AbstractBuilder {
	
	@Autowired
	private RepeatingTransactionService repeatingTransactionService;
	
	@Autowired
	private TransactionBuilder transactionBuilder;

	
	protected void parseTransaction(RepeatingTransaction transaction, Element e) {
		try {
			transactionBuilder.parseTransaction(transaction, e);
			transaction.setDateDue(Constants.DATE_FORMAT.parse(e.elementText("date-due")));
			transaction.setOriginalDateDue(Constants.DATE_FORMAT.parse(e.elementText("original-date-due")));
			transaction.setFrequency(Frequency.valueOf(e.elementText("frequency")));
			transaction.setCount(Integer.valueOf(e.elementText("count")));
			transaction.setEnabled(Boolean.valueOf(e.elementText("enabled")));
		} catch (ParseException e1) {
			throw new RuntimeException(e1);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		RepeatingTransaction transaction;

		List<Element> nodes = e.selectNodes("repeating-transactions/transaction");
		for (Element n : nodes) {
			transaction = new RepeatingTransaction();
			parseTransaction(transaction, n);
			repeatingTransactionService.addEntity((RepeatingTransaction)transaction, false);
		}
	}
	
	protected void buildTransactionXml(Element el, RepeatingTransaction trans) {
		transactionBuilder.buildTransactionXml(el, trans);
		RepeatingTransaction repeatingTransaction = (RepeatingTransaction)trans;
		addElement(el, "enabled", repeatingTransaction.getEnabled());
		addElement(el, "date-due", Constants.DATE_FORMAT.format(repeatingTransaction.getDateDue()));
		addElement(el, "original-date-due", Constants.DATE_FORMAT.format(repeatingTransaction.getOriginalDateDue()));
		addElement(el, "frequency", repeatingTransaction.getFrequency().name());
		addElement(el, "count", repeatingTransaction.getCount().toString());
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("repeating-transactions");
		Element el;
		
		for (RepeatingTransaction trans : repeatingTransactionService.getEntities()) {
			el = root.addElement("transaction");
			buildTransactionXml(el, trans);
		}
	}

}
