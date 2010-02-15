package net.deuce.moman.model.version;

import org.dom4j.Document;

public class NoopDocumentConverter implements DocumentConverter {

	@Override
	public void convert(Document document) {
	}

}
