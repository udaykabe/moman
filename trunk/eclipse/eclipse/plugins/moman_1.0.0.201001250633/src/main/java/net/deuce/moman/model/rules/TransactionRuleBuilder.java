package net.deuce.moman.model.rules;

import java.util.List;

import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.Registry;

import org.dom4j.Document;
import org.dom4j.Element;

public class TransactionRuleBuilder implements EntityBuilder {

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		List<Element> envelopeElements = e.selectNodes("transactions/rule");
		for (Element n : envelopeElements) {
			processEnvelopeElement(registry, n);
		}
	}

	private void processEnvelopeElement(Registry registry, Element e) {

		Rule rule = new Rule();
		rule.setId(e.attributeValue("id"));
		rule.setEnabled(new Boolean(e.attributeValue("enabled")));
		rule.setExpression(e.elementText("expression"));
		rule.setConversion(e.elementText("conversion"));
		rule.setCondition(Condition.valueOf(e.elementText("condition")));
		rule.setEnvelope(Registry.instance().getEnvelope(e.element("envelope").attributeValue("id")));
		
		registry.addTransactionRule(rule);

	}
	
	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().element("transactions");
		Element el;
		
		for (Rule rule : registry.getTransactionRules()) {
			el = root.addElement("rule");
			el.addAttribute("id", rule.getId());
			el.addAttribute("enabled", Boolean.toString(rule.isEnabled()));
			el.addElement("expression").setText(rule.getExpression());
			el.addElement("conversion").setText(rule.getConversion());
			el.addElement("condition").setText(rule.getCondition().name());
			el.addElement("envelope").addAttribute("id", rule.getEnvelope().getId());
		}
	}
}
