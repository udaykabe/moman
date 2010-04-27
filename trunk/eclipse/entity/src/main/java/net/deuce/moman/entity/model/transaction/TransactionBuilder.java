package net.deuce.moman.entity.model.transaction;

import java.util.List;

import net.deuce.moman.entity.model.EntityBuilder;
import net.sf.ofx4j.domain.data.common.Transaction;

import org.dom4j.Document;
import org.dom4j.Element;

public interface TransactionBuilder extends EntityBuilder<InternalTransaction> {
	
	public List<Transaction> parseImportXml(Element e);
	
	public void buildImportXml(Document doc);

	public void parseTransaction(InternalTransaction transaction, Element e);
	
	public void buildTransactionXml(Element el, InternalTransaction trans);
}
