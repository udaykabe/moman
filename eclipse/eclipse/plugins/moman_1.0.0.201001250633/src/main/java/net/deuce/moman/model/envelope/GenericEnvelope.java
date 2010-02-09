package net.deuce.moman.model.envelope;

import net.deuce.moman.model.EntityMonitor;
import net.deuce.moman.model.MomanEntity;

public class GenericEnvelope<E extends Envelope> extends MomanEntity {

	private static final long serialVersionUID = 1L;

	private transient EntityMonitor<E> monitor = new EntityMonitor<E>();
	
	public EntityMonitor<E> getMonitor() {
		return this.monitor;
	}
	
	public void setMonitor(EntityMonitor<E> monitor) {
		this.monitor = monitor;
	}
	
}
