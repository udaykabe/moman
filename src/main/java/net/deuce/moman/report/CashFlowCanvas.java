package net.deuce.moman.report;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.Constants;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.util.DataDateRange;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.event.StructureSource;
import org.eclipse.birt.chart.event.StructureType;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.ActionType;
import org.eclipse.birt.chart.model.attribute.ActionValue;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Marker;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.TriggerCondition;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.PaletteImpl;
import org.eclipse.birt.chart.model.attribute.impl.TooltipValueImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Action;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.render.IActionRenderer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;

public class CashFlowCanvas extends AbstractTransactionReportCanvas
implements IActionRenderer {
	
	private static final ColorDefinition[] ACCOUNT_COLORS = new ColorDefinition[] {
	    ColorDefinitionImpl.RED(),
	    ColorDefinitionImpl.CREAM(),
	    ColorDefinitionImpl.YELLOW(),
	    ColorDefinitionImpl.CYAN(),
	    ColorDefinitionImpl.ORANGE(),
	    ColorDefinitionImpl.PINK(),
	    ColorDefinitionImpl.BLACK(),
	    ColorDefinitionImpl.GREY(),
	};
	
	private static final String INCOME_LABEL = "Income";
	private static final String EXPENSE_LABEL = "Expense";
	
	private Palette palette;
	private DataSetResult incomeResult;
	private DataSetResult expenseResult;
	private List<NumberDataSet> balanceDataSets = new LinkedList<NumberDataSet>();
	private SeriesDefinition sdY;

	public CashFlowCanvas(Composite parent, DateRangeCombo combo, int style) {
		super(parent, combo, style);
		
		ServiceNeeder.instance().getAccountService().addEntityListener(new EntityListener<Account>() {
			@Override
			public void entityRemoved(EntityEvent<Account> event) {
				adjustLegends();
			}
			@Override
			public void entityChanged(EntityEvent<Account> event) {
				adjustLegends();
			}
			@Override
			public void entityAdded(EntityEvent<Account> event) {
				adjustLegends();
			}
		});
	}
	
	private void adjustLegends() {
		palette = null;
		
		int index = 0;
        for (NumberDataSet ds : balanceDataSets) {
        	addPaletteColor(ACCOUNT_COLORS[index++]);
        }
    	addPaletteColor(ColorDefinitionImpl.GREEN());
    	addPaletteColor(ColorDefinitionImpl.BLUE());
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Chart doCreateChart() {
		Chart chart = ChartWithAxesImpl.create();
		chart.getTitle().getLabel().getCaption().setValue("Cash Flow");
        chart.getTitle().getLabel().getCaption().getFont().setSize(Constants.CHART_TITLE_FONT_SIZE);
        chart.getTitle().getLabel().getCaption().getFont().setName(Constants.CHART_TITLE_FONT_NAME);
        
        chart.getLegend().setVisible(true);
        chart.getLegend().setItemType(LegendItemType.SERIES_LITERAL);
        
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
        
        incomeResult = createDataSet(false);
        expenseResult = createDataSet(true);
        
        NumberDataSet orthoValuesDataSet1 = NumberDataSetImpl.create(incomeResult.getResult());
        NumberDataSet orthoValuesDataSet2 = NumberDataSetImpl.create(expenseResult.getResult());
        
        balanceDataSets.clear();
        
        double maxValue = 0.0;
    	maxValue = Math.max(maxValue, incomeResult.getMaxValue());
    	maxValue = Math.max(maxValue, expenseResult.getMaxValue());
        
    	List<Account> selectedAccounts =
    		ServiceNeeder.instance().getAccountService().getSelectedAccounts();
        for (Account account : selectedAccounts) {
        	DataSetResult result = createBalanceDataSet(account);
        	maxValue = Math.max(maxValue, result.getMaxValue());
        	balanceDataSets.add(NumberDataSetImpl.create(result.getResult()));
        }

        sdY = SeriesDefinitionImpl.create();
        yAxis.getSeriesDefinitions().add(sdY);
        
        int index = 0;
        for (NumberDataSet ds : balanceDataSets) {
        	LineSeries lineSeries = (LineSeries)LineSeriesImpl.create();
	        lineSeries.setSeriesIdentifier(selectedAccounts.get(index).getNickname() + " Balance");
        	lineSeries.setDataSet(ds);
        	addTrigger(lineSeries);
        	for ( int i = 0; i < lineSeries.getMarkers( ).size( ); i++ ) {
        		( (Marker) lineSeries.getMarkers( ).get( i ) ).setType( MarkerType.CIRCLE_LITERAL );
//        		( (Marker) lineSeries.getMarkers( ).get( i ) ).setSize( 10 );
    		}

        	sdY.getSeries().add(lineSeries);
        }
        
        BarSeries bs1 = (BarSeries) BarSeriesImpl.create();
        bs1.setSeriesIdentifier(INCOME_LABEL);
        bs1.setDataSet(orthoValuesDataSet1);
        addTrigger(bs1);
        BarSeries bs2 = (BarSeries) BarSeriesImpl.create();
        bs2.setSeriesIdentifier(EXPENSE_LABEL);
        bs2.setDataSet(orthoValuesDataSet2);
        addTrigger(bs2);
        
        sdY.getSeries().add(bs1);
        sdY.getSeries().add(bs2);
        
        adjustLegends();
        sdY.setSeriesPalette(palette);
        
        yAxis.getScale().setStep(roundToNearestPowerOf10(maxValue));
        yAxis.getScale().setMin(NumberDataElementImpl.create(0.0));
		return chart;
	}
	
	private void addPaletteColor(ColorDefinition colorDefinition) {
		if (palette == null) {
	        palette = PaletteImpl.create(colorDefinition);
		} else {
	        palette.getEntries().add(colorDefinition);
		}
	}
	
	private DataSetResult createBalanceDataSet(Account account) {
		List<Double> dataSet = new LinkedList<Double>();
		double maxSum = 0.0;
		double minSum = Double.MAX_VALUE;
		
		TransactionService transactionService = ServiceNeeder.instance().getTransactionService();
		
		for (DataDateRange ddr : getDateRange().dataDateRanges()) {
			double sum = 0.0;
			
			List<InternalTransaction> transactions =
				transactionService.getAccountTransactions(account, ddr, true);
			if (transactions.size() > 0) {
				sum = transactions.get(0).getBalance();
			}
			
			if (sum > maxSum) {
				maxSum = sum;
			}
			if (sum < minSum) {
				minSum = sum;
			}

			dataSet.add(sum);
		}

		return new DataSetResult(null, dataSet, minSum, maxSum);
	}
	
	@Override
	protected IActionRenderer getActionRenderer() {
		return this;
	}
	
	@Override
	protected void addTrigger(Series series) {
		super.addTrigger(series);
		ActionValue actionValue = TooltipValueImpl.create(-1, getClass().getCanonicalName());
		series.getTriggers().add(createTrigger(actionValue, ActionType.SHOW_TOOLTIP_LITERAL, TriggerCondition.ONMOUSEOVER_LITERAL));
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

	@Override
	public void processAction(Action action, StructureSource source) {
		if (action.getType() == ActionType.SHOW_TOOLTIP_LITERAL &&
			source.getType() == StructureType.SERIES_DATA_POINT) {	
			DataPointHints dph = (DataPointHints)source.getSource();
			String displayValue = dph.getSeriesDisplayValue() + " " +
				Constants.CURRENCY_VALIDATOR.format(Constants.CURRENCY_VALIDATOR.validate(dph.getDisplayValue()));
			TooltipValueImpl tooltip = (TooltipValueImpl)action.getValue();
			if (tooltip != null) {
				tooltip.setText(displayValue);	
			}
		}
	}

	@Override
	protected void handleCallback(StructureSource source, CallBackValue value, DataPointHints dataPointHints) {
		int index = dataPointHints.getIndex();
		TransactionService transactionService = ServiceNeeder.instance().getTransactionService();

		if (INCOME_LABEL.equals(dataPointHints.getSeriesDisplayValue())) {
			transactionService.setCustomTransactionList(incomeResult.getDataPointTransactions(index));
		} else {
			transactionService.setCustomTransactionList(expenseResult.getDataPointTransactions(index));
		}
	}
	
}
