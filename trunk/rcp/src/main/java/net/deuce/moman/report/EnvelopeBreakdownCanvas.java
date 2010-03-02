package net.deuce.moman.report;

import java.util.Arrays;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.report.EnvelopeDataSetResult.EnvelopeResult;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.event.StructureSource;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.swt.widgets.DateRangeCombo;

public class EnvelopeBreakdownCanvas extends SpendingCanvas {
	
	public EnvelopeBreakdownCanvas(SpendingComposite parent, DateRangeCombo combo, int style) {
		super(parent, combo, style);
		setDeepEnvelopeTransactions(true);
	}
	
	@Override
	protected EnvelopeSource getInitialEnvelopeSource() {
		EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
		Envelope root = envelopeService.getRootEnvelope();
		return new EnvelopeSource(null, null, root.getChildren(), "Root");
	}

	@Override
	protected String getChartTitle() {
		return "Envelope Breakdown";
	}

	@Override
	protected void handleCallback(StructureSource source, CallBackValue value, DataPointHints dataPointHints) {
		int index = dataPointHints.getIndex();
		
		EnvelopeSource envelopeSource = null;
		if (index < getResult().getEnvelopes().size()) {
			Envelope env = getResult().getEnvelopes().get(index).getEnvelope();
			if (env != peekSourceEnvelope().getEnvelope()) {
				if (env.getChildren().size() > 0) {
					envelopeSource = new EnvelopeSource(peekSourceEnvelope(), env, env.getChildren(), env.getName());
				} else {
					Envelope[] list = new Envelope[]{env};
					envelopeSource = new EnvelopeSource(peekSourceEnvelope(), env, Arrays.asList(list), env.getName());
				}
			}
		} else {
			EnvelopeSource currentSource = peekSourceEnvelope();
			try {
			List<Envelope> availableEnvelopes = currentSource.getEnvelope().getChildren();
			for (EnvelopeResult er : getResult().getEnvelopes()) {
				availableEnvelopes.remove(er.getEnvelope());
			}
			EnvelopeSource parentSource = currentSource.getParentSource();
			while (parentSource != null) {
				for (Envelope env : parentSource.getTopEnvelopes()) {
					availableEnvelopes.remove(env);
				}
				parentSource = parentSource.getParentSource();
			}
			envelopeSource = new EnvelopeSource(currentSource, currentSource.getEnvelope(), availableEnvelopes, currentSource.getEnvelope().getName() + " Other");
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		if (envelopeSource != null && envelopeSource.getAvailableEnvelopes().size() > 0) {
			pushSourceEnvelope(envelopeSource);
			regenerateChart();
		}
	}
}
