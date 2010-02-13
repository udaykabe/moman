package net.deuce.moman.allocation.model;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class AllocationFactory extends EntityFactory<Allocation> {
	
	public Allocation buildEntity(String id, Integer index,
			Boolean enabled, Double amount, AmountType amountType,
			Envelope envelope, Double limit, LimitType limitType) {
		Allocation entity = super.buildEntity(Allocation.class, id);
		entity.setIndex(index);
		entity.setEnabled(enabled);
		entity.setAmount(amount);
		entity.setAmountType(amountType);
		entity.setEnvelope(envelope);
		entity.setLimit(limit);
		entity.setLimitType(limitType);
		return entity;
	}
	
	public Allocation newEntity(Integer index,
			Boolean enabled, Double amount, AmountType amountType,
			Envelope envelope, Double limit, LimitType limitType) {
		return buildEntity(createUuid(), index, enabled,
				amount, amountType, envelope, limit, limitType);
	}
	
}
