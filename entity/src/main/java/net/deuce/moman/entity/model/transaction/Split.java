package net.deuce.moman.entity.model.transaction;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityProperty;
import net.deuce.moman.entity.model.envelope.Envelope;

import org.dom4j.Document;

public class Split extends AbstractEntity<Split> {
	
	public enum Properties implements EntityProperty {
        envelope(Envelope.class), amount(Double.class);
        
		private Class<?> ptype;
		
		public Class<?> type() { return ptype; }
		
		private Properties(Class<?> ptype) { this.ptype = ptype; }
    }
	
	private Envelope envelope;
	private Double amount;
	
	public Split(Envelope envelope, Double amount) {
		super();
		this.envelope = envelope;
		this.amount = amount;
	}

	
	public Document toXml() {
		return buildXml(Properties.values());
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

	
	public String toString() {
		return "Split [amount=" + amount + ", envelope=" + envelope + "]";
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((envelope == null) ? 0 : envelope.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Split other = (Split) obj;
		if (envelope == null) {
			if (other.envelope != null)
				return false;
		} else if (!envelope.equals(other.envelope))
			return false;
		return true;
	}

	
	public int compare(Split o1, Split o2) {
		return o1.compareTo(o2);
	}

	
	public int compareTo(Split o) {
		return envelope.compareTo(o.getEnvelope());
	}
	
	
}
