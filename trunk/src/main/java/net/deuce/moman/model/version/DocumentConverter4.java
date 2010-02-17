package net.deuce.moman.model.version;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocumentConverter4 implements DocumentConverter {

	@SuppressWarnings("unchecked")
	@Override
	public void convert(Document document) {
		List<Element> accounts = (List<Element>)document.selectNodes("/moman/accounts/account");
		for (Element el : accounts) {
			el.addElement("last-reconciled-ending-balance").setText("0.0");
		}
		
	}

}
