package net.deuce.moman.allocation.model;

import java.util.List;

import net.deuce.moman.allocation.service.AllocationSetService;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.income.model.Income;
import net.deuce.moman.income.service.IncomeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllocationSetBuilder extends AbstractBuilder {
	
	@Autowired
	private EnvelopeService envelopeService;
	
	@Autowired
	private IncomeService incomeService;
	
	@Autowired
	private AllocationSetService allocationSetService;
	
	@Autowired
	private AllocationSetFactory allocationSetFactory;
	
	@Autowired
	private AllocationFactory allocationFactory;
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		
		List<Element> allocationSetElements = e.selectNodes("allocation-sets/allocation-set");
		for (Element n : allocationSetElements) {
			AllocationSet allocationSet = processAllocationSetElement(n);
			parseAllocationXml(n, allocationSet);
			
			allocationSetService.addEntity(allocationSet);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseAllocationXml(Element e, AllocationSet allocationSet) {
		
		List<Element> allocationElements = e.selectNodes("allocations/allocation");
		if (allocationElements != null) {
			for (Element el : allocationElements) {
				allocationSet.addAllocation(processAllocationElement(el, allocationSet));
			}
		}
	}
	
	protected AllocationSet processAllocationSetElement(Element e) {
//		dumpElement(e);
		
		Income income = null;
		Element el = e.element("income");
		if (el != null) {
			income = incomeService.getEntity(el.attributeValue("id"));
		}
		return allocationSetFactory.buildEntity(
				e.attributeValue("id"), e.elementText("name"), income );
	}
	
	protected Allocation processAllocationElement(Element e, AllocationSet allocationSet) {
//		dumpElement(e);
		Allocation allocation = allocationFactory.buildEntity(
				e.attributeValue("id"),
				Integer.valueOf(e.elementText("index")),
				Boolean.valueOf(e.elementText("enabled")),
				Double.valueOf(e.elementText("amount")),
				AmountType.valueOf(e.elementText("amount-type")),
				envelopeService.getEntity(e.element("envelope").attributeValue("id")),
				Double.valueOf(e.elementText("limit")),
				LimitType.valueOf(e.elementText("limit-type"))
				);
		allocation.setAllocationSet(allocationSet);
		return allocation;
	}
	
	protected Element buildAllocationSet(AllocationSet allocationSet, Element root, String name) {
		Element el;
		Element sel;
		Element ael;
		
		el = root.addElement(name);
		el.addAttribute("id", allocationSet.getId());
		addElement(el, "name", allocationSet.getName());
		if (allocationSet.getIncome() != null) {
			el.addElement("income").addAttribute("id", allocationSet.getIncome().getId());
		}
		
		sel = el.addElement("allocations");
		for (Allocation allocation : allocationSet.getAllocations()) {
			ael = sel.addElement("allocation");
			ael.addAttribute("id", allocation.getId());
			addElement(ael, "index", allocation.getIndex());
			addElement(ael, "amount", Utils.formatDouble(allocation.getAmount()));
			addElement(ael, "amount-type", allocation.getAmountType().name());
			addElement(ael, "limit", Utils.formatDouble(allocation.getLimit()));
			addElement(ael, "limit-type", allocation.getLimitType().name());
			addElement(ael, "enabled", allocation.getEnabled());
			ael.addElement("envelope").addAttribute("id", allocation.getEnvelope().getId());
		}
		
		return el;
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("allocation-sets");
		
		for (AllocationSet allocationSet : allocationSetService.getEntities()) {
			buildAllocationSet(allocationSet, root, "allocation-set");
		}
	}
}
