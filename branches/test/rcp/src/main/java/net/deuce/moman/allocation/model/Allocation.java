package net.deuce.moman.allocation.model;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;
import net.deuce.moman.service.ServiceNeeder;

import org.dom4j.Document;

public class Allocation extends AbstractEntity<Allocation> {

	private static final long serialVersionUID = 1L;
	
	public enum Properties implements EntityProperty {
	    enabled(Boolean.class), amount(Double.class), amountType(AmountType.class),
	    envelope(Envelope.class), limit(Double.class), limitType(LimitType.class),
	    proposed(Double.class), index(Integer.class), remainder(Double.class);
	    
	    private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
	private Boolean enabled = Boolean.TRUE;
	private Double amount = 0.0;
	private Envelope envelope = envelopeService.getRootEnvelope();
	private Double limit = 0.0;
	private AmountType amountType;
	private LimitType limitType;
	private Double proposed = 0.0;
	private Double remainder = 0.0;
	private Integer index;
	private AllocationSet allocationSet;
	
	public Allocation() {}
	
	public AllocationSet getAllocationSet() {
		return allocationSet;
	}

	@Override
	public Document toXml() {
		return buildXml(Properties.values());
	}
	
	public void setAllocationSet(AllocationSet allocationSet) {
		this.allocationSet = allocationSet;
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		if (propertyChanged(this.envelope, envelope)) {
			this.envelope = envelope;
			getMonitor().fireEntityChanged(this, Properties.envelope);
		}
	}
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public AmountType getAmountType() {
		return amountType;
	}

	public void setAmountType(AmountType amountType) {
		if (propertyChanged(this.amountType, amountType)) {
			this.amountType = amountType;
			getMonitor().fireEntityChanged(this, Properties.amountType);
		}
	}

	public LimitType getLimitType() {
		return limitType;
	}

	public void setLimitType(LimitType limitType) {
		if (propertyChanged(this.limitType, limitType)) {
			this.limitType = limitType;
			getMonitor().fireEntityChanged(this, Properties.limitType);
		}
	}

	@Override
	public int compareTo(Allocation o) {
		return compare(this, o);
	}

	@Override
	public int compare(Allocation o1, Allocation o2) {
		return o1.index.compareTo(o2.getIndex());
	}
	
	@Override
	public String toString() {
		return "Allocation("+index+")";
	}
	
	public boolean isEnabled() {
		return evaluateBoolean(enabled);
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		if (!this.enabled.equals(enabled)) {
			this.enabled = enabled;
			getMonitor().fireEntityChanged(this, Properties.enabled);
		}
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		if (!this.amount.equals(amount)) {
			this.amount = amount;
			getMonitor().fireEntityChanged(this, Properties.amount);
		}
	}

	public Double getLimit() {
		return limit;
	}
	
	public void setLimit(Double limit) {
		if (!this.limit.equals(limit)) {
			this.limit = limit;
			getMonitor().fireEntityChanged(this, Properties.limit);
		}
	}

	public Double getProposed() {
		return proposed;
	}
	
	public void setProposed(Double proposed) {
		if (!this.proposed.equals(proposed)) {
			this.proposed = proposed;
			getMonitor().fireEntityChanged(this, Properties.proposed);
		}
	}

	public Double getRemainder() {
		return remainder;
	}

	public void setRemainder(Double remainder) {
		if (!this.remainder.equals(remainder)) {
			this.remainder = remainder;
			getMonitor().fireEntityChanged(this, Properties.remainder);
		}
	}
	
}
