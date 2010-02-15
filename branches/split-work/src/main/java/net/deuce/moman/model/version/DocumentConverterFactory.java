package net.deuce.moman.model.version;

public class DocumentConverterFactory {

	public static DocumentConverter getInstance(int sourceVersion, int targetVersion) {
		if (sourceVersion == 1 && targetVersion == 2) {
			return new DocumentConverter1_2();
		}
		return new NoopDocumentConverter();
	}
}
