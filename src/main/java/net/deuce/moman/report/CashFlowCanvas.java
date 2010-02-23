package net.deuce.moman.report;

import net.deuce.moman.Constants;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.PaletteImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;

public class CashFlowCanvas extends AbstractTransactionReportCanvas {

	public CashFlowCanvas(Composite parent, DateRangeCombo combo, int style) {
		super(parent, combo, style);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Chart doCreateChart() {
		Chart chart = ChartWithAxesImpl.create();
		chart.getTitle().getLabel().getCaption().setValue("Cash Flow");
        chart.getTitle().getLabel().getCaption().getFont().setSize(Constants.CHART_TITLE_FONT_SIZE);
        chart.getTitle().getLabel().getCaption().getFont().setName(Constants.CHART_TITLE_FONT_NAME);
        
        chart.getLegend().setVisible(false);
        
        Axis xAxis = ((ChartWithAxes) chart).getPrimaryBaseAxes()[0];

	    Axis yAxis = ((ChartWithAxes) chart).getPrimaryOrthogonalAxis(xAxis);
        
	    getDateRange().calcDateRanges();
	    TextDataSet categoryValues = TextDataSetImpl.create(getDateRange().chartLabels());
	    
	    Series seCategory = SeriesImpl.create();
        seCategory.setDataSet(categoryValues);

	    SeriesDefinition sdX = SeriesDefinitionImpl.create();
        sdX.getSeriesPalette().update(1);

        xAxis.getSeriesDefinitions().add(sdX);
        sdX.getSeries().add(seCategory);
        
        DataSetResult incomeResult = createDataSet(false);
        DataSetResult expenseResult = createDataSet(true);
        
        NumberDataSet orthoValuesDataSet1 = NumberDataSetImpl.create(incomeResult.getResult());
        NumberDataSet orthoValuesDataSet2 = NumberDataSetImpl.create(expenseResult.getResult());

        BarSeries bs1 = (BarSeries) BarSeriesImpl.create();
        bs1.setDataSet(orthoValuesDataSet1);
        addTrigger(bs1);
        BarSeries bs2 = (BarSeries) BarSeriesImpl.create();
        bs2.setDataSet(orthoValuesDataSet2);
        addTrigger(bs2);

        SeriesDefinition sdY = SeriesDefinitionImpl.create();
        yAxis.getSeriesDefinitions().add(sdY);
        sdY.getSeries().add(bs1);
        sdY.getSeries().add(bs2);
        
        Palette seriesPalette = PaletteImpl.create(ColorDefinitionImpl.GREEN());
        seriesPalette.getEntries().add(ColorDefinitionImpl.BLUE());
        sdY.setSeriesPalette(seriesPalette);
        
        yAxis.getScale().setStep(roundToNearestPowerOf10(Math.max(incomeResult.getMaxValue(), expenseResult.getMaxValue())));
        
        yAxis.getScale().setMin(NumberDataElementImpl.create(0.0));
		return chart;
	}
	
	private double roundToNearestPowerOf10(double val) {
		return Math.ceil(val/1000.0)*1000.0;
		/*
		for (int i=7; i>=1; i--) {
			double thres = Math.pow(10, i);
			if (val > thres) {
				return Math.ceil(val/thres)*thres;
			}
		}
		return 10.0;
		*/
	}

}
