package net.deuce.moman.entity.model.version;

import java.util.LinkedList;
import java.util.List;

public class DocumentConverterFactory {

	@SuppressWarnings("unchecked")
	public static DocumentConverter getInstance(int sourceVersion, int targetVersion) {
		if (targetVersion < sourceVersion) {
			throw new RuntimeException("sourceVersion must be less than or equals to the targetVersion");
		}
			
		if (sourceVersion == targetVersion) {
			return new NoopDocumentConverter();
		}
		
		List<DocumentConverter> converters = new LinkedList<DocumentConverter>();
		
		for (int version=sourceVersion+1; version<=targetVersion; version++) {
			Class<DocumentConverter> converter;
			try {
				converter = (Class<DocumentConverter>) Class.forName(
						DocumentConverter.class.getPackage().getName() + ".DocumentConverter" + version);
				converters.add(converter.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return new CompositeDocumentConverter(converters);
	}
}
