package net.deuce.moman.entity.model.payee.impl;

import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.deuce.moman.entity.model.payee.Payee;
import net.deuce.moman.entity.model.payee.PayeeFactory;

import org.springframework.stereotype.Component;

@Component("payeeFactory")
public class PayeeFactoryImpl extends EntityFactoryImpl<Payee> implements PayeeFactory {

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
