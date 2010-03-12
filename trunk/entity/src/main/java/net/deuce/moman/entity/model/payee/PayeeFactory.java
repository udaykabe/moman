package net.deuce.moman.entity.model.payee;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.envelope.Envelope;

public interface PayeeFactory extends EntityFactory<Payee> {

	public Payee buildEntity(String id, String description,
			Double amount, Envelope envelope);
	
	public Payee newEntity(String description,
			Double amount, Envelope envelope);
}
