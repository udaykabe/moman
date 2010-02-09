package net.deuce.moman.model.envelope;

import java.util.List;

import net.deuce.moman.model.EntityBuilder;
import net.deuce.moman.model.Registry;
import net.deuce.moman.model.transaction.InternalTransaction;

import org.dom4j.Document;
import org.dom4j.Element;

public class EnvelopeBuilder implements EntityBuilder {

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		List<Element> envelopeElements = e.selectNodes("envelopes/envelope");
		for (Element n : envelopeElements) {
			Envelope envelope = new Envelope();
			processEnvelopeElement(registry, n, envelope);
			registry.addEnvelope(envelope);
		}
	}

	@SuppressWarnings("unchecked")
	protected void processEnvelopeElement(Registry registry, Element e, Envelope envelope) {

		String tid;
		Element el;
		
		envelope.setId(e.attributeValue("id"));
		envelope.setEditable(new Boolean(e.attributeValue("editable")));
		envelope.setExpanded(new Boolean(e.attributeValue("expanded")));
		envelope.setName(e.elementText("name"));
		envelope.setFrequency(Frequency.valueOf(e.elementText("frequency")));
		envelope.setBudget(new Float(e.elementText("budget")));
		
		el = e.element("parent");
		if (el != null) {
			String parentId = el.attributeValue("id");
			envelope.setParentId(parentId);
		} else {
			registry.setRootEnvelope(envelope);
		}

		List<Element> transactionElements = e.selectNodes("transactions/transaction");
		if (transactionElements != null) {
			for (Element t : transactionElements) {
				tid = t.attributeValue("id");
				envelope.getTransactions().add(registry.getTransaction(tid));
			}
		}

	}
	
	protected Element buildEnvelope(Envelope env, Element root, String name) {
		Element el;
		Element tel;
		
		el = root.addElement(name);
		el.addAttribute("id", env.getId());
		el.addAttribute("editable", Boolean.toString(env.isEditable()));
		if (env.isExpanded()) {
			el.addAttribute("expanded", "true");
		}
		el.addElement("name").setText(env.getName());
		el.addElement("frequency").setText(env.getFrequency().name());
		el.addElement("budget").setText(Float.toString(env.getBudget()));
		
		if (env.getParent() != null) {
			el.addElement("parent").addAttribute("id", env.getParent().getId());
		}
		tel = el.addElement("transactions");
		for (Transaction trans : env.getTransactions()) {
			tel.addElement("transaction").addAttribute("id", trans.getId());
		}
		
		return el;
	}
	
	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("envelopes");
		
		for (Envelope env : registry.getEnvelopes()) {
			buildEnvelope(env, root, "envelope");
		}
	}
}
