package net.deuce.moman.entity.model.version;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;

public class CompositeDocumentConverter implements DocumentConverter {
	
	private List<DocumentConverter> converters = new LinkedList<DocumentConverter>();
	
	public CompositeDocumentConverter(List<DocumentConverter> converters) {
		this.converters.addAll(converters);
	}

	
	public void convert(Document document) {
		for (DocumentConverter converter : converters) {
			converter.convert(document);
		}
	}

}
