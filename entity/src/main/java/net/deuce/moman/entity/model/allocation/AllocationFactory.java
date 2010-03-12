package net.deuce.moman.entity.model.allocation;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.envelope.Envelope;

public interface AllocationFactory extends EntityFactory<Allocation> {
	
	public Allocation buildEntity(String id, Integer index,
			Boolean enabled, Double amount, AmountType amountType,
			Envelope envelope, Double limit, LimitType limitType);
	
	public Allocation newEntity(Integer index,
			Boolean enabled, Double amount, AmountType amountType,
			Envelope envelope, Double limit, LimitType limitType);
}
