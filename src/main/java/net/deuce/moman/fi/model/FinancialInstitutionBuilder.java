package net.deuce.moman.fi.model;

import java.util.List;

import net.deuce.moman.fi.service.FinancialInstitutionService;
import net.deuce.moman.model.AbstractBuilder;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinancialInstitutionBuilder extends AbstractBuilder<FinancialInstitution> {

	@Autowired
	private FinancialInstitutionService financialInstitutionService;
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		FinancialInstitution fi;
		
		List<Element> nodes = e.selectNodes("financialInstitutions/financialInstitution");
		for (Element n : nodes) {
			fi = new FinancialInstitution();
			fi.setId(n.attributeValue("id"));
			fi.setName(n.elementText("name"));
			fi.setUrl(n.elementText("url"));
			fi.setFinancialInstitutionId(n.elementText("fid"));
			fi.setOrganization(n.elementText("org"));
			financialInstitutionService.addEntity(fi);
		}
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement(getRootElementName());
		
		for (FinancialInstitution fi : financialInstitutionService.getEntities()) {
			buildEntity(fi, root);
		}
	}

	@Override
	protected Element buildEntity(FinancialInstitution fi, Element parent) {
		Element el = parent.addElement("financialInstitution");
		addElement(el, "id", fi.getId());
		addElement(el, "name", fi.getName());
		addElement(el, "url", fi.getUrl());
		addElement(el, "fid", fi.getFinancialInstitutionId());
		addElement(el, "org", fi.getOrganization());
		return el;
	}

	@Override
	protected String getRootElementName() {
		return "financialInstitutions";
	}
}
