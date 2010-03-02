package net.deuce.moman.model;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

@SuppressWarnings("unchecked")
public interface EntityBuilder<E extends AbstractEntity> {
	public void buildXml(Document doc);
	public Document buildXml(List<E> entities);
	public void parseXml(Element e);
}
