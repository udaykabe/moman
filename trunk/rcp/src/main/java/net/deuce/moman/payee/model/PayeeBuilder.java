package net.deuce.moman.payee.model;

import java.util.List;

import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractBuilder;
import net.deuce.moman.payee.service.PayeeService;
import net.deuce.moman.util.Utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayeeBuilder extends AbstractBuilder<Payee> {
	
	@Autowired
	private PayeeService payeeService;
	
	@Autowired
	private PayeeFactory payeeFactory;
	
	@Autowired
	private EnvelopeService envelopeService;
	
	@SuppressWarnings("unchecked")
	public void parseXml(Element e) {
			Payee payee;

		List<Element> nodes = e.selectNodes("payees/payee");
		for (Element n : nodes) {
			payee = payeeFactory.buildEntity(
					n.attributeValue("id"),
					n.elementText("description"),
					Double.valueOf(n.elementText("amount")),
					envelopeService.getEntity(n.element("envelope").attributeValue("id"))
					);
			payeeService.addEntity(payee);
		}
	}
	
	public void buildXml(Document doc) {
		
		Element root = doc.getRootElement().addElement(getRootElementName());
		
		for (Payee payee : payeeService.getEntities()) {
			buildEntity(payee, root);
		}
	}

	@Override
	protected Element buildEntity(Payee payee, Element parent) {
		Element el = parent.addElement("payee");
		el.addAttribute("id", payee.getId());
		addElement(el, "description", payee.getDescription());
		addElement(el, "amount", Utils.formatDouble(payee.getAmount()));
		el.addElement("envelope").addAttribute("id", payee.getEnvelope().getId());
		return el;
	}

	@Override
	protected String getRootElementName() {
		return "payees";
	}
}
