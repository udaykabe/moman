package net.deuce.moman.model;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public abstract class AbstractBuilder implements EntityBuilder {

	protected void dumpElement(Element e) {
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
		try {
			writer = new XMLWriter(System.out, format);
		    writer.write(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	protected void addElement(Element el, String elementName, Object value) {
		el.addElement(elementName).setText(value != null ? value.toString() : "");
	}
	
	protected void addOptionalElement(Element el, String elementName, Object obj) {
		if (obj != null) {
			el.addElement(elementName).setText(obj.toString());
		}
	}

	protected void addOptionalBooleanElement(Element el, String elementName, Boolean booleanValue) {
		if (booleanValue != null && booleanValue.booleanValue()) {
			el.addElement(elementName).setText(booleanValue.toString());
		}
	}
}
