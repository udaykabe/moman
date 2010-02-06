package net.deuce.moman.payee.model;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class PayeeFactory extends EntityFactory<Payee> {

	public Payee buildEntity(String id, String description,
			Double amount, Envelope envelope) {
		Payee entity = super.buildEntity(Payee.class, id);
		entity.setDescription(description);
		entity.setEnvelope(envelope);
		entity.setAmount(amount);
		return entity;
	}
	
	public Payee newEntity(String description,
			Double amount, Envelope envelope) {
		return buildEntity(createUuid(), description, amount, envelope);
	}
}
