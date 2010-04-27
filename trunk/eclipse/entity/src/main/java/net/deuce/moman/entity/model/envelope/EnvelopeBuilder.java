package net.deuce.moman.entity.model.envelope;

import net.deuce.moman.entity.model.EntityBuilder;

import org.dom4j.Element;

public interface EnvelopeBuilder extends EntityBuilder<Envelope> {
	
	public void parseDefaultEnvelopesXml(Element e);
	
}
