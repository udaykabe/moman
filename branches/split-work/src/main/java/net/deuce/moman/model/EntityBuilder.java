package net.deuce.moman.model;

import org.dom4j.Document;
import org.dom4j.Element;

public interface EntityBuilder {
	public void buildXml(Document doc);
	public void parseXml(Element e);
}
