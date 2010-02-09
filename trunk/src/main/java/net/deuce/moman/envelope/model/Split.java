package net.deuce.moman.envelope.model;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;

public class Split extends AbstractEntity<Split> {

	private static final long serialVersionUID = 1L;
	
	public enum Properties implements EntityProperty {
	    envelope(Envelope.class), amount(Double.class);
	    
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private Envelope envelope;
	private Double amount;
	
	public Split() {}
	
	public Split(Envelope envelope, Double amount) {
		this.envelope = envelope;
		this.amount = amount;
	}
	
	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		if (this.envelope != envelope) {
			this.envelope = envelope;
			getMonitor().fireEntityChanged(this, Properties.envelope);
		}
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		if (this.amount != amount) {
			this.amount = amount;
			getMonitor().fireEntityChanged(this, Properties.amount);
		}
	}

	@Override
	public int compareTo(Split o) {
		return compare(this, o);
	}

	@Override
	public int compare(Split o1, Split o2) {
		return o1.envelope.getName().compareTo(o2.getEnvelope().getName());
	}
	
	@Override
	public String toString() {
		return "Split("+envelope.getName()+", " + amount +")";
	}
	
}
