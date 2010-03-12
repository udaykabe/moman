package net.deuce.moman.entity.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

@SuppressWarnings("unchecked")
public interface EntityBuilder<E extends AbstractEntity> {
	public void buildXml(Document doc);
	public Document buildXml(List<E> entities);
	public void parseXml(Element e);
	public void printEntities(PrintWriter out, E entity) throws IOException;
	public void printEntities(PrintWriter out, List<E> entities) throws IOException;
}
