package net.deuce.moman.payee.model;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;

public class Payee extends AbstractEntity<Payee> {
	
	public enum Properties implements EntityProperty {
		NONE(null), ALL(null), description(String.class),
		envelope(Envelope.class), amount(Double.class);
		
		private Class<?> type;
		
		public Class<?> type() { return type; }
		
		private Properties(Class<?> type) { this.type = type; }
	}

	private static final long serialVersionUID = 1L;

	private String description;
	private Envelope envelope;
	private Double amount;

	public Payee() {
		super();
	}
	
	@Override
	public int compareTo(Payee o) {
		return compare(this, o);
	}

	@Override
	public int compare(Payee o1, Payee o2) {
		return o1.description.compareTo(o2.description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (propertyChanged(this.description, description)) {
			this.description = description;
			getMonitor().fireEntityChanged(this, Properties.description);
		}
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		if (propertyChanged(this.amount, amount)) {
			this.amount = amount;
			getMonitor().fireEntityChanged(this, Properties.amount);
		}
	}

}
