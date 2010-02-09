package net.deuce.moman.model.envelope;

import java.util.List;

import net.deuce.moman.model.Registry;

import org.dom4j.Document;
import org.dom4j.Element;

public class BillBuilder extends EnvelopeBuilder {
	
	@Override
	protected Element buildEnvelope(Envelope env, Element root, String name) {
		Element el = super.buildEnvelope(env, root, name);
		
		Bill bill = (Bill)env;
		el.addAttribute("enabled", Boolean.toString(bill.isEnabled()));
		el.addElement("dueDay").setText(Integer.toString(bill.getDueDay()));
		
		return el;
	}

	@Override
	protected void processEnvelopeElement(Registry registry, Element e, Envelope envelope) {
		super.processEnvelopeElement(registry, e, envelope);
		
		Bill bill = (Bill)envelope;
		bill.setEnabled(new Boolean(e.attributeValue("enabled")));
		bill.setDueDay(new Integer(e.elementText("dueDay")));
	}

	@SuppressWarnings("unchecked")
	public void parseXml(Registry registry, Element e) {
		List<Element> envelopeElements = e.selectNodes("bills/bill");
		for (Element n : envelopeElements) {
			Bill bill = new Bill();
			processEnvelopeElement(registry, n, bill);
			registry.addBill(bill);
		}
	}

	public void buildXml(Registry registry, Document doc) {
		
		Element root = doc.getRootElement().addElement("bills");
		
		for (Envelope env : registry.getBills()) {
			buildEnvelope(env, root, "bill");
		}
	}
}
