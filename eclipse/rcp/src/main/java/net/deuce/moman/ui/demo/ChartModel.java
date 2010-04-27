package net.deuce.moman.ui.demo;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.IDataRowExpressionEvaluator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;
import org.springframework.beans.factory.annotation.Autowired;

public class ChartModel {
	
	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public static void bindData(Chart chart, RunTimeContext context,
			Envelope envelope) {

		try {

			List<Envelope> nonZeroBalanceEnvelopes = new LinkedList<Envelope>();
			for (Envelope env : envelope.getChildren()) {
				if (env.getBalance() != 0.0) {
					nonZeroBalanceEnvelopes.add(env);
				}
			}
			final String[] names = new String[nonZeroBalanceEnvelopes.size()];
			final Double[] balances = new Double[nonZeroBalanceEnvelopes.size()];
			int i = 0;
			for (Envelope env : nonZeroBalanceEnvelopes) {
				names[i] = env.getChartLegendLabel();
				balances[i++] = env.getBalance();
			}

			Generator.instance().bindData(new IDataRowExpressionEvaluator() {

				private int row = 0;

				public void close() {
				}

				public Object evaluate(String expression) {
					if ("Envelope".equals(expression)) {
						return names[row];
					}
					return balances[row++];
				}

				public Object evaluateGlobal(String expression) {
					return null;
				}

				public boolean first() {
					return row == 0 && names.length > 0;
				}

				public boolean next() {
					return row < names.length - 1;
				}

			}, chart, context);
		} catch (ChartException e) {
			e.printStackTrace();
		}

	}

}
