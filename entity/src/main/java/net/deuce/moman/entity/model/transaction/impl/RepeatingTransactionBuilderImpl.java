package net.deuce.moman.entity.model.transaction.impl;

import java.text.ParseException;
import java.util.List;

import net.deuce.moman.entity.model.AbstractBuilder;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.transaction.RepeatingTransaction;
import net.deuce.moman.entity.model.transaction.RepeatingTransactionBuilder;
import net.deuce.moman.entity.model.transaction.TransactionBuilder;
import net.deuce.moman.entity.service.transaction.RepeatingTransactionService;
import net.deuce.moman.util.Constants;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("repeatingTransactionBuilder")
public class RepeatingTransactionBuilderImpl extends AbstractBuilder<RepeatingTransaction>
implements RepeatingTransactionBuilder {
	
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
		
		Element root = doc.getRootElement().addElement(getRootElementName());
		
		for (RepeatingTransaction trans : repeatingTransactionService.getEntities()) {
			buildEntity(trans, root);
		}
	}

	
	protected Element buildEntity(RepeatingTransaction entity, Element parent) {
		Element el = parent.addElement("transaction");
		buildTransactionXml(el, entity);
		return el;
	}

	
	protected String getRootElementName() {
		return "repeating-transactions";
	}

}
