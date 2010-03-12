package net.deuce.moman.report;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.report.EnvelopeDataSetResult.EnvelopeResult;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.event.StructureSource;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.swt.widgets.DateRangeCombo;

public class SpendingCanvas extends AbstractEnvelopeReportPieCanvas {

    private SpendingComposite spendingComposite;

	public SpendingCanvas(SpendingComposite parent, DateRangeCombo combo,
			int style) {
		super(parent, combo, style);
		this.spendingComposite = parent;
	}

	private List<String> buildCategories(EnvelopeDataSetResult dataSetResult) {
		List<String> categories = new LinkedList<String>();
		for (EnvelopeResult result : dataSetResult.getEnvelopes()) {
			categories.add(result.getEnvelope().getName());
		}

		if (dataSetResult.getAllOtherTotal() != 0.0) {
			categories.add("All Others");
		}
		return categories;
	}

	public List<Double> buildEnvelopeValues(EnvelopeDataSetResult dataSetResult) {
		List<Double> amounts = new LinkedList<Double>();
		for (EnvelopeResult result : dataSetResult.getEnvelopes()) {
			amounts.add(result.getValue());
		}
		if (dataSetResult.getAllOtherTotal() != 0.0) {
			amounts.add(dataSetResult.getAllOtherTotal());
		}
		return amounts;
	}

	protected void handleCallback(StructureSource source, CallBackValue value,
			DataPointHints dataPointHints) {
		int index = dataPointHints.getIndex();

		EnvelopeSource envelopeSource = null;
		if (index < getResult().getEnvelopes().size()) {
			Envelope env = getResult().getEnvelopes().get(index).getEnvelope();
			if (env != peekSourceEnvelope().getEnvelope()) {
				if (env.getChildren().size() > 0) {
					envelopeSource = new EnvelopeSource(peekSourceEnvelope(),
							env, env.getChildren(), env.getName());
				} else {
					Envelope[] list = new Envelope[] { env };
					envelopeSource = new EnvelopeSource(peekSourceEnvelope(),
							env, Arrays.asList(list), env.getName());
				}
			}
		} else {
			List<Envelope> availableEnvelopes = getTopEnvelopeSource()
					.getAvailableEnvelopes();
			for (EnvelopeResult er : getResult().getEnvelopes()) {
				availableEnvelopes.remove(er.getEnvelope());
			}
			for (EnvelopeSource es : getSourceEnvelopes()) {
				if (es != getTopEnvelopeSource()) {
					availableEnvelopes.remove(es.getEnvelope());
				}
				availableEnvelopes.removeAll(es.getTopEnvelopes());
			}
			envelopeSource = new EnvelopeSource(peekSourceEnvelope(), null,
					availableEnvelopes, "Other");
		}

		if (envelopeSource != null
				&& envelopeSource.getAvailableEnvelopes().size() > 0) {
			pushSourceEnvelope(envelopeSource);
			regenerateChart();
		}
	}

	protected String getChartTitle() {
		return "Spending Overview";
	}

	@SuppressWarnings("deprecation")
	protected Chart doCreateChart() {
        Chart chart = ChartWithoutAxesImpl.create();
		chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());
		chart.getBlock().getOutline().setVisible(true);
		chart.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);

		chart.getTitle().getLabel().getCaption().setValue(getChartTitle());

		if (hasSourceEnvelopes()) {
			spendingComposite.setSourceEnvelopes(getSourceEnvelopes());
		} else {
			spendingComposite.setSourceEnvelopes(null);
		}

		// customize the plot
		Plot p = chart.getPlot();
		p.getClientArea().setBackground(
				ColorDefinitionImpl.create(255, 255, 225));
		p.getOutline().setVisible(false);

		// customize the legend
		Legend lg = chart.getLegend();
		lg.getText().getFont().setSize(16);
		lg.getInsets().set(10, 5, 0, 0);
		lg.setAnchor(Anchor.NORTH_LITERAL);
		lg.setShowPercent(true);

		EnvelopeDataSetResult result = (EnvelopeDataSetResult) createDataSet(true);
		peekSourceEnvelope().getTopEnvelopes().clear();

		for (EnvelopeResult er : result.getEnvelopes()) {
			peekSourceEnvelope().getTopEnvelopes().add(er.getEnvelope());
		}

		NumberDataSet orthoValuesDataSet1 = NumberDataSetImpl
				.create(buildEnvelopeValues(result));

		getDateRange().calcDateRanges();

		Series seCategory = SeriesImpl.create();

		// CREATE THE PRIMARY DATASET
		PieSeries pieSeries = (PieSeries) PieSeriesImpl.create();

		pieSeries.setSeriesIdentifier("");
		pieSeries.setSliceOutline(ColorDefinitionImpl.CREAM());
		pieSeries.getLabel().setVisible(true);

		TextDataSet categoryValues = TextDataSetImpl
				.create(buildCategories(result));
		seCategory.setDataSet(categoryValues);
		SeriesDefinition sdX = SeriesDefinitionImpl.create();

		sdX.getSeriesPalette().update(0);

		// SET THE COLORS IN THE PALETTE

		SeriesDefinition sdY = SeriesDefinitionImpl.create();

		sdY.getSeriesPalette().update(1);

		// SET THE COLORS IN THE PALETTE
		sdX.getSeriesDefinitions().add(sdY);
		sdX.getSeries().add(seCategory);
		sdX.getRunTimeSeries().add(seCategory);
		sdY.getSeries().add(pieSeries);
		pieSeries.setDataSet(orthoValuesDataSet1);
		sdY.getRunTimeSeries().add(pieSeries);
		((ChartWithoutAxes) chart).getSeriesDefinitions().add(sdX);

		addTrigger(pieSeries);

		return chart;
	}

}
