package net.deuce.moman.envelope.model;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.deuce.moman.Constants;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnvelopeBuilder extends AbstractBuilder {
	
	@Autowired
	private EnvelopeService envelopeService;
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
		
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
			} else if (envelope.isSavingsGoals()) {
				envelopeService.setSavingsGoalsEnvelope(envelope);
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
			try {
//				dumpElement(e);
				Envelope envelope = new Envelope();
				envelope.setId(e.attributeValue("id"));
				envelope.setEditable(Boolean.valueOf(e.elementText("editable")));
				envelope.setExpanded(Boolean.valueOf(e.elementText("expanded")));
				envelope.setSelected(Boolean.valueOf(e.elementText("selected")));
				envelope.setMonthly(Boolean.valueOf(e.elementText("monthly")));
				envelope.setSavingsGoals(Boolean.valueOf(e.elementText("savings-goals")));
				envelope.setRoot(Boolean.valueOf(e.elementText("root")));
				envelope.setUnassigned(Boolean.valueOf(e.elementText("unassigned")));
				envelope.setAvailable(Boolean.valueOf(e.elementText("available")));
				envelope.setIndex(Integer.valueOf(e.elementText("index")));
				envelope.setName(e.elementText("name"));
				envelope.setFrequency(Frequency.valueOf(e.elementText("frequency")));
				envelope.setBudget(Double.valueOf(e.elementText("budget")));
				envelope.setEnabled(Boolean.valueOf(e.elementText("enabled")));
				envelope.setDueDay(Integer.valueOf(e.elementText("dueDay")));
				String val = e.elementText("savings-goal-date");
				if (val != null) {
					envelope.setSavingsGoalDate(Constants.SHORT_DATE_FORMAT.parse(val));
				}
				return envelope;
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new RuntimeException(pe);
		}
	}
	
	protected Element buildEnvelope(Envelope env, Element root, String name) {
		Element el;
		
		el = root.addElement(name);
		el.addAttribute("id", env.getId());
		addElement(el, "editable", env.isEditable());
		addOptionalBooleanElement(el, "selected", env.isSelected());
		addOptionalBooleanElement(el, "root", env.isRoot());
		addOptionalBooleanElement(el, "unassigned", env.isUnassigned());
		addOptionalBooleanElement(el, "monthly", env.isMonthly());
		addOptionalBooleanElement(el, "savings-goals", env.isSavingsGoals());
		addOptionalBooleanElement(el, "available", env.isAvailable());
		addOptionalBooleanElement(el, "expanded", env.isEnabled());
		addOptionalBooleanElement(el, "enabled", env.isEnabled());
		addElement(el, "name", env.getName());
		addElement(el, "index", env.getIndex());
		addElement(el, "frequency", env.getFrequency().name());
		addElement(el, "budget", Utils.formatDouble(env.getBudget()));
		
		addElement(el, "dueDay", env.getDueDay().toString());
		if (env.getSavingsGoalDate() != null) {
			addElement(el, "savings-goal-date", Constants.SHORT_DATE_FORMAT.format(env.getSavingsGoalDate()));
		}
		
		if (env.getParent() != null) {
			el.addElement("parent").addAttribute("id", env.getParent().getId());
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
