package net.deuce.moman.entity.model.allocation.impl;

import java.util.List;

import net.deuce.moman.entity.model.AbstractBuilder;
import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationFactory;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AllocationSetBuilder;
import net.deuce.moman.entity.model.allocation.AllocationSetFactory;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.entity.service.income.IncomeService;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("allocationSetBuilder")
public class AllocationSetBuilderImpl extends AbstractBuilder<AllocationSet> 
implements AllocationSetBuilder {
	
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
	
	
	protected Element buildEntity(AllocationSet allocationSet, Element root) {
		Element el;
		Element sel;
		
		el = root.addElement("allocation-set");
		el.addAttribute("id", allocationSet.getId());
		addElement(el, "name", allocationSet.getName());
		if (allocationSet.getIncome() != null) {
			el.addElement("income").addAttribute("id", allocationSet.getIncome().getId());
		}
		
		sel = el.addElement("allocations");
		for (Allocation allocation : allocationSet.getAllocations()) {
			buildAllocation(allocation, sel);
		}
		
		return el;
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement(getRootElementName());
		
		for (AllocationSet allocationSet : allocationSetService.getEntities()) {
			buildEntity(allocationSet, root);
		}
	}
	
	protected Element buildAllocation(Allocation allocation, Element parent) {
		Element el = parent.addElement("allocation");
		el.addAttribute("id", allocation.getId());
		addElement(el, "index", allocation.getIndex());
		addElement(el, "amount", Utils.formatDouble(allocation.getAmount()));
		addElement(el, "amount-type", allocation.getAmountType().name());
		addElement(el, "limit", Utils.formatDouble(allocation.getLimit()));
		addElement(el, "limit-type", allocation.getLimitType().name());
		addElement(el, "enabled", allocation.getEnabled());
		el.addElement("envelope").addAttribute("id", allocation.getEnvelope().getId());
		return el;
	}

	
	protected String getRootElementName() {
		return "allocation-sets";
	}
}
