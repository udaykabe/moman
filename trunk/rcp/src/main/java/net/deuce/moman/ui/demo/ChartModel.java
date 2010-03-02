package net.deuce.moman.ui.demo;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.IDataRowExpressionEvaluator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;


public class ChartModel
{
	public static void bindData(Chart chart, RunTimeContext context, Envelope envelope)
	{

			try {
				
				EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
				
				List<Envelope> nonZeroBalanceEnvelopes = new LinkedList<Envelope>();
			    for (Envelope env : envelope.getChildren()) {
			    	if (env.getBalance() != 0.0) {
			    		nonZeroBalanceEnvelopes.add(env);
			    	}
			    }
			    final String[] names = new String[nonZeroBalanceEnvelopes.size()];
			    final Double[] balances = new Double[nonZeroBalanceEnvelopes.size()];
			    int i=0;
			    for (Envelope env : nonZeroBalanceEnvelopes) {
			    	names[i] = env.getChartLegendLabel();
			    	balances[i++] = env.getBalance();
			    }
			    
				Generator.instance().bindData(new IDataRowExpressionEvaluator() {
					
					private int row = 0;

					@Override
					public void close() {
					}

					@Override
					public Object evaluate(String expression) {
						if ("Envelope".equals(expression)) {
							return names[row];
						}
						return balances[row++];
					}

					@Override
					public Object evaluateGlobal(String expression) {
						return null;
					}

					@Override
					public boolean first() {
						return row == 0 && names.length > 0;
					}

					@Override
					public boolean next() {
						return row < names.length-1;
					}
					
				}, chart, context);
			} catch (ChartException e) {
				e.printStackTrace();
			}

	}
	
}
