package net.deuce.moman.envelope.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvelopeBuilder extends AbstractBuilder {
	
	@Autowired
	private EnvelopeService envelopeService;
	
	@Autowired
	private TransactionService transactionService;

	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		
		String tid;
		Element el;
		
		List<Element> envelopeElements = e.selectNodes("envelopes/envelope");
		for (Element n : envelopeElements) {
			Envelope envelope = processEnvelopeElement(n);
			
			el = n.element("parent");
			if (el != null) {
				String parentId = el.attributeValue("id");
				envelope.setParentId(parentId);
			}
			
			if (envelope.isRoot()) {
				envelopeService.setRootEnvelope(envelope);
			} else if (envelope.isUnassigned()) {
				envelopeService.setUnassignedEnvelope(envelope);
			} else if (envelope.isAvailable()) {
				envelopeService.setAvailableEnvelope(envelope);
			} else if (envelope.isMonthly()) {
				envelopeService.setMonthlyEnvelope(envelope);
			}

			List<Element> transactionElements = n.selectNodes("transactions/transaction");
			if (transactionElements != null) {
				for (Element t : transactionElements) {
					tid = t.attributeValue("id");
					envelope.addTransaction(transactionService.getEntity(tid), false);
				}
			}
			
			envelopeService.addEntity(envelope);
			if (envelope.isSelected()) {
				envelopeService.setSelectedEnvelope(envelope);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void parseDefaultEnvelopesXml(Element e) {
		
		Element el;
		List<Element> envelopeElements = e.selectNodes("envelopes/envelope");
		Map<String, Envelope> cache = new HashMap<String, Envelope>();
		for (Element n : envelopeElements) {
			Envelope envelope = processEnvelopeElement(n);
			
			el = n.element("parent");
			if (el != null) {
				String parentId = el.attributeValue("id");
				envelopeService.addDefaultEnvelope(envelope, cache.get(parentId));
			} else {
				envelopeService.addDefaultEnvelope(envelope, null);
			}
			
			cache.put(envelope.getId(), envelope);
		}
	}
	
	protected Envelope processEnvelopeElement(Element e) {
		dumpElement(e);
		Envelope envelope = new Envelope();
		envelope.setId(e.attributeValue("id"));
		envelope.setEditable(Boolean.valueOf(e.elementText("editable")));
		envelope.setExpanded(Boolean.valueOf(e.elementText("expanded")));
		envelope.setSelected(Boolean.valueOf(e.elementText("selected")));
		envelope.setMonthly(Boolean.valueOf(e.elementText("monthly")));
		envelope.setRoot(Boolean.valueOf(e.elementText("root")));
		envelope.setUnassigned(Boolean.valueOf(e.elementText("unassigned")));
		envelope.setAvailable(Boolean.valueOf(e.elementText("available")));
		envelope.setIndex(Integer.valueOf(e.elementText("index")));
		envelope.setName(e.elementText("name"));
		envelope.setFrequency(Frequency.valueOf(e.elementText("frequency")));
		envelope.setBudget(Double.valueOf(e.elementText("budget")));
		envelope.setEnabled(Boolean.valueOf(e.elementText("enabled")));
		envelope.setDueDay(Integer.valueOf(e.elementText("dueDay")));
		return envelope;
	}
	
	protected Element buildEnvelope(Envelope env, Element root, String name) {
		Element el;
		Element tel;
		
		el = root.addElement(name);
		el.addAttribute("id", env.getId());
		addOptionalBooleanElement(el, "editable", env.isEditable());
		addOptionalBooleanElement(el, "selected", env.isSelected());
		addOptionalBooleanElement(el, "root", env.isRoot());
		addOptionalBooleanElement(el, "unassigned", env.isUnassigned());
		addOptionalBooleanElement(el, "monthly", env.isMonthly());
		addOptionalBooleanElement(el, "available", env.isAvailable());
		addOptionalBooleanElement(el, "expanded", env.isEnabled());
		addOptionalBooleanElement(el, "enabled", env.isEnabled());
		addElement(el, "name", env.getName());
		addElement(el, "index", env.getIndex());
		addElement(el, "frequency", env.getFrequency().name());
		addElement(el, "budget", env.getBudget().toString());
		
		addElement(el, "dueDay", env.getDueDay().toString());
		
		if (env.getParent() != null) {
			el.addElement("parent").addAttribute("id", env.getParent().getId());
		}
		tel = el.addElement("transactions");
		for (InternalTransaction trans : env.getAllTransactions()) {
			tel.addElement("transaction").addAttribute("id", trans.getId());
		}
		
		return el;
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement("envelopes");
		
		for (Envelope env : envelopeService.getEntities()) {
			buildEnvelope(env, root, "envelope");
		}
	}
}
