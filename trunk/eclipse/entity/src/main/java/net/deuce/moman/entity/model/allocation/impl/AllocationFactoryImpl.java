package net.deuce.moman.entity.model.allocation.impl;

import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationFactory;
import net.deuce.moman.entity.model.allocation.AmountType;
import net.deuce.moman.entity.model.allocation.LimitType;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;

import org.springframework.stereotype.Component;

@Component("allocationFactory")
public class AllocationFactoryImpl extends EntityFactoryImpl<Allocation> 
implements AllocationFactory {
	
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
