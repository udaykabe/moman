package net.deuce.moman.model;

import org.dom4j.Document;
import org.dom4j.Element;

public interface EntityBuilder {
	public void buildXml(Registry registry, Document doc);
	public void parseXml(Registry registry, Element e);
}
