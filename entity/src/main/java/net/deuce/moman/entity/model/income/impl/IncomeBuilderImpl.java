package net.deuce.moman.entity.model.income.impl;

import java.text.ParseException;
import java.util.List;

import net.deuce.moman.entity.model.AbstractBuilder;
import net.deuce.moman.entity.model.Frequency;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.model.income.IncomeBuilder;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.util.Constants;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("incomeBuilder")
public class IncomeBuilderImpl extends AbstractBuilder<Income> implements IncomeBuilder {
	
	@Autowired
	private IncomeService incomeService;
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		Income income;

		try {
			List<Element> nodes = e.selectNodes("income-list/income");
			for (Element n : nodes) {
				income = new Income();
				income.setId(n.attributeValue("id"));
				income.setEnabled(Boolean.valueOf(n.elementText("enabled")));
				income.setName(n.elementText("name"));
				income.setAmount(Double.valueOf(n.elementText("amount")));
				income.setNextPayday(Constants.SHORT_DATE_FORMAT.parse(n.elementText("next-payday")));
				income.setFrequency(Frequency.valueOf(n.elementText("frequency")));
				incomeService.addEntity(income);
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	public void buildXml(Document doc) {

		Element root = doc.getRootElement().addElement(getRootElementName());

		for (Income income : incomeService.getEntities()) {
			buildEntity(income, root);
		}
	}

	
	protected Element buildEntity(Income income, Element parent) {
		Element el = parent.addElement("income");
		el.addAttribute("id", income.getId());
		addOptionalBooleanElement(el, "enabled", income.isEnabled());
		addElement(el, "name", income.getName());
		addElement(el, "amount", Utils.formatDouble(income.getAmount()));
		addElement(el, "frequency", income.getFrequency().name());
		addElement(el, "next-payday", Constants.SHORT_DATE_FORMAT.format(income.getNextPayday()));
		return el;
	}

	
	protected String getRootElementName() {
		return "income-list";
	}
}
