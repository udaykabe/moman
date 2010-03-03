package net.deuce.moman.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<E extends AbstractEntity> implements EntityBuilder<E> {

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
	
	public void printEntities(PrintWriter out, E entity) throws IOException {
		printEntities(out, (List<E>)Arrays.asList(new AbstractEntity[]{entity}));
	}
	
	public void printEntities(PrintWriter out, List<E> entities) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(out, format);
        writer.write(buildXml(entities));
	}
	
	public Document buildXml(List<E> entities) {
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(getRootElementName());
		for (E entity : entities) {
			buildEntity(entity, root);
		}

		return doc;
	}

	protected abstract String getRootElementName();
	protected abstract Element buildEntity(E entity, Element parent);

}
