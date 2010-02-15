package net.deuce.moman.rule.model;

import java.util.List;

import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.rule.service.TransactionRuleService;
import net.deuce.moman.transaction.model.Split;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionRuleBuilder extends AbstractBuilder {
	
	@Autowired
	private TransactionRuleService transactionRuleService;

	@Autowired
	private EnvelopeService envelopeService;

	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		List<Element> envelopeElements = e.selectNodes("transactions/rule");
		for (Element n : envelopeElements) {
			processEnvelopeElement(n);
		}
	}

	@SuppressWarnings("unchecked")
	private void processEnvelopeElement(Element e) {

		Rule rule = new Rule();
		rule.setId(e.attributeValue("id"));
		rule.setEnabled(Boolean.valueOf(e.elementText("enabled")));
		rule.setExpression(e.elementText("expression"));
		rule.setConversion(e.elementText("conversion"));
		if (e.element("amount") != null) {
			rule.setAmount(Double.valueOf(e.elementText("amount")));
		}
		rule.setCondition(Condition.valueOf(e.elementText("condition")));
		
		List<Element> envelopeElements = e.selectNodes("split/envelope");
		if (envelopeElements != null) {
			for (Element ee : envelopeElements) {
				rule.addSplit(
						envelopeService.findEntity(ee.attributeValue("id")),
						Double.valueOf(ee.attributeValue("amount")));
			}
		}
		
		transactionRuleService.addEntity(rule);

	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().element("transactions");
		Element el;
		
		for (Rule rule : transactionRuleService.getEntities()) {
			el = root.addElement("rule");
			el.addAttribute("id", rule.getId());
			addOptionalBooleanElement(el, "enabled", rule.isEnabled());
			addOptionalElement(el, "amount", rule.getAmount());
			addElement(el, "expression", rule.getExpression());
			addElement(el, "conversion", rule.getConversion());
			addElement(el, "condition", rule.getCondition().name());
			Element sel = el.addElement("split");
			Element eel;
			for (Split item : rule.getSplit()) {
				eel = sel.addElement("envelope");
				eel.addAttribute("id", item.getEnvelope().getId());
				eel.addAttribute("amount", item.getAmount().toString());
			}
		}
	}
}
