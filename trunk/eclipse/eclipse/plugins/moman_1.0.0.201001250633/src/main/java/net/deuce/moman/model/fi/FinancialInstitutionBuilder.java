package net.deuce.moman.model.fi;

import java.util.List;

import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.Registry;

import org.dom4j.Document;
import org.dom4j.Element;

public class FinancialInstitutionBuilder implements EntityBuilder {

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		FinancialInstitution fi;
		
		List<Element> nodes = e.selectNodes("financialInstitutions/financialInstitution");
		for (Element n : nodes) {
			fi = new FinancialInstitution();
			fi.setId(n.attributeValue("id"));
			fi.setName(n.elementText("name"));
			fi.setUrl(n.elementText("url"));
			fi.setFinancialInstitutionId(n.elementText("fid"));
			fi.setOrganization(n.elementText("org"));
			registry.addFinancialInstitution(fi);
		}
	}
	
	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("financialInstitutions");
		Element el;
		
		for (FinancialInstitution fi : registry.getFinancialInstitutions()) {
			el = root.addElement("financialInstitution");
			el.addAttribute("id", fi.getId());
			el.addElement("name").setText(fi.getName());
			el.addElement("url").setText(fi.getUrl());
			el.addElement("fid").setText(fi.getFinancialInstitutionId());
			el.addElement("org").setText(fi.getOrganization());
		}
	}
}
