package net.deuce.moman.envelope.ui;

import net.deuce.moman.entity.model.envelope.Envelope;

public abstract class BaseEnvelopeContentProvider {

	protected Envelope getEnvelope(Object o) {
		if (!(o instanceof Envelope)) {
			throw new RuntimeException("Tree element is not an envelope");
		}
		return (Envelope) o;
	}

}
