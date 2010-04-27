package net.deuce.moman.entity.model.envelope;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.Frequency;

import org.springframework.stereotype.Component;

@Component("envelopeFactory")
public interface EnvelopeFactory extends EntityFactory<Envelope> {
	
	public Envelope buildEntity(String id, Integer index, String name,
			Frequency frequency, Double budget,  Envelope parent,
			Boolean selected, Boolean expanded, Boolean enabled, Integer dueDay);
	
	public Envelope newEntity(Integer index, String name, Frequency frequency, Double budget, 
			Envelope parent, Boolean selected, Boolean expanded,
			Boolean enabled, Integer dueDay);
	
	public Envelope createTopLevelEnvelope();
	
	public Envelope cloneEnvelope(Envelope envelope, Envelope parent);
}
