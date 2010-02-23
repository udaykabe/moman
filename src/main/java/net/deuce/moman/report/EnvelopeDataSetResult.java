package net.deuce.moman.report;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;

public class EnvelopeDataSetResult extends DataSetResult {
	
	private List<EnvelopeResult> envelopeResults = new LinkedList<EnvelopeResult>();
	private double allOtherTotal;

	public EnvelopeDataSetResult(List<EnvelopeResult> results, double allOtherTotal,
			double minValue, double maxValue) {
		super(new LinkedList<Double>(), minValue, maxValue);
		this.envelopeResults.addAll(results);
		this.allOtherTotal = allOtherTotal;
	}

	public double getAllOtherTotal() {
		return allOtherTotal;
	}

	public void setAllOtherTotal(double allOtherTotal) {
		this.allOtherTotal = allOtherTotal;
	}

	public List<EnvelopeResult> getEnvelopes() {
		return envelopeResults;
	}

	public void setEnvelopes(List<EnvelopeResult> envelopeResults) {
		this.envelopeResults.clear();
		this.envelopeResults.addAll(envelopeResults);
	}
	
	public static class EnvelopeResult implements Comparable<EnvelopeResult> {
		
		private Envelope envelope;
		private Double value;
		
		public EnvelopeResult(Envelope envelope, Double value) {
			super();
			this.envelope = envelope;
			this.value = value;
		}

		public Envelope getEnvelope() {
			return envelope;
		}

		public void setEnvelope(Envelope envelope) {
			this.envelope = envelope;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}

		@Override
		public int compareTo(EnvelopeResult result) {
			return result.getValue().compareTo(value);
		}

		@Override
		public String toString() {
			return "EnvelopeResult [envelope=" + envelope + ", value=" + value
					+ "]";
		}
		
	}

}
