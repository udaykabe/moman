package net.deuce.moman.model.envelope;


public abstract class BaseEnvelopeContentProvider {

	protected Envelope getEnvelope(Object o) {
		if (! (o instanceof Envelope) ) {
			throw new RuntimeException("Tree element is not an envelope");
		}
		return (Envelope)o;
	}
	
}
