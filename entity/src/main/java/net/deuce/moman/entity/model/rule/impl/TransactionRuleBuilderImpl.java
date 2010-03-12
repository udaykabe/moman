package net.deuce.moman.entity.model.rule.impl;

import java.util.List;

import net.deuce.moman.entity.model.AbstractBuilder;
import net.deuce.moman.entity.model.rule.Condition;
import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.model.rule.TransactionRuleBuilder;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.rule.TransactionRuleService;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("transactionRuleBuilder")
public class TransactionRuleBuilderImpl extends AbstractBuilder<Rule> implements TransactionRuleBuilder {
	
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
		
		rule.setEnvelope(envelopeService.findEntity(e.element("envelope").attributeValue("id")));
		
		transactionRuleService.addEntity(rule);

	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().element(getRootElementName());
		
		for (Rule rule : transactionRuleService.getEntities()) {
			buildEntity(rule, root);
		}
	}

	
	protected Element buildEntity(Rule rule, Element parent) {
		Element el = parent.addElement("rule");
		el.addAttribute("id", rule.getId());
		addOptionalBooleanElement(el, "enabled", rule.isEnabled());
		addOptionalElement(el, "amount", Utils.formatDouble(rule.getAmount()));
		addElement(el, "expression", rule.getExpression());
		addElement(el, "conversion", rule.getConversion());
		addElement(el, "condition", rule.getCondition().name());
		el.addElement("envelope").addAttribute("id", rule.getEnvelope().getId());
		return el;
	}

	
	protected String getRootElementName() {
		return "transactions";
	}
}
