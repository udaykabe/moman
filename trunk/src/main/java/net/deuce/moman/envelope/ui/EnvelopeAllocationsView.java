package net.deuce.moman.envelope.ui;

import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.EntityEvent;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.demo.SWTViewer;

import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.ActionType;
import org.eclipse.birt.chart.model.attribute.ActionValue;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.TriggerCondition;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.CallBackValueImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Action;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.Trigger;
import org.eclipse.birt.chart.model.data.impl.ActionImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.TriggerImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

public class EnvelopeAllocationsView extends ViewPart implements EntityListener<Envelope>, PaintListener {
	
	public static final String ID = EnvelopeAllocationsView.class.getName();
	
	private EnvelopeService envelopeService;
	private JFreeChart chart;
	private ChartComposite chartComposite;
	
	private boolean bNeedsGeneration = true;
	private Canvas canvas;
	
	private IDeviceRenderer idr = null;
	private Chart cm = null;

	public EnvelopeAllocationsView() {
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		envelopeService.addEntityListener(this);
	}

	public void createPartControl(final Composite parent) {
		
		/*
		final PluginSettings ps = PluginSettings.instance();
		try {
			idr = ps.getDevice("dv.SWT");
		} catch (ChartException pex)
		{
			pex.printStackTrace();
		}
		
		try {
		cm = createMyChart();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		canvas = new Canvas(parent, SWT.NONE);
		canvas.setLayoutData(new GridData( GridData.FILL_BOTH ));
		canvas.addPaintListener(this);
		*/
		
		parent.setLayout(new GridLayout());
		SWTViewer viewer = new SWTViewer(parent, SWT.NO_BACKGROUND);
		viewer.setLayoutData(new GridData( GridData.FILL_BOTH ));
		viewer.addPaintListener(viewer);
		
		/*
		RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        rowLayout.pack = false;
        rowLayout.justify = false;
        rowLayout.type = SWT.VERTICAL;
        rowLayout.marginLeft = 5;
        rowLayout.marginTop = 5;
        rowLayout.marginRight = 5;
        rowLayout.marginBottom = 5;
        rowLayout.spacing = 0;
        parent.setLayout(rowLayout);
        */
        
//		JFreeChart chart = createChart(createDataset());
//		chartComposite = new ChartComposite(parent, SWT.NONE, chart, true);
//		chartComposite.setLayout(layout);

		refresh();
		
		/*
		parent.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}
			@Override
			public void controlResized(ControlEvent e) {
				System.out.println("ZZZ size: "+ parent.getSize());
				int min = Math.min(parent.getSize().x, parent.getSize().y);
				chartComposite.setSize(min, min);
			}
		});
		*/
	}
	
	public void paintControl(PaintEvent pe)
	{
		if ( bNeedsGeneration )
		{
			bNeedsGeneration = false;
		idr.setProperty(IDeviceRenderer.GRAPHICS_CONTEXT, pe.gc);
		Composite co = (Composite) pe.getSource();
		Rectangle re = co.getClientArea();
		Bounds bo = BoundsImpl.create(re.x, re.y, re.width, re.height);
		bo.scale( 72d / idr.getDisplayServer().getDpiResolution() ); // BOUNDS MUST BE SPECIFIED IN POINTS
		// BUILD AND RENDER THE CHART
 
		Generator gr = Generator.instance();
		try {
			gr.render(idr, gr.build(idr.getDisplayServer(), cm, null, bo, null));
		} catch (Exception gex)
		{
			gex.printStackTrace();
		}
		}
	}
	
	private JFreeChart createChart(PieDataset dataset) {
		chart = ChartFactory.createPieChart("Envelope Allocations",
				dataset, true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		
		return chart;
	}
	
	private PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("One", new Double(43.2));
		dataset.setValue("Two", new Double(10.0));
		dataset.setValue("Three", new Double(27.5));
		dataset.setValue("Four", new Double(17.5));
		dataset.setValue("Five", new Double(11.0));
		dataset.setValue("Six", new Double(19.4));
		return dataset;
	}
	
	@Override
	public void setFocus() {
		chartComposite.setFocus();
	}
	
	private void refresh() {
	}

	@Override
	public void entityAdded(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityChanged(EntityEvent<Envelope> event) {
		refresh();
	}

	@Override
	public void entityRemoved(EntityEvent<Envelope> event) {
		refresh();
	}

	public Chart createMyChart() {
	    // bart charts are based on charts that contain axes
		ChartWithoutAxesImpl cwaBar = (ChartWithoutAxesImpl) ChartWithoutAxesImpl.create();
	    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
	    cwaBar.getBlock().getOutline().setVisible(true);
	    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
	    
	    // customize the plot
	    Plot p = cwaBar.getPlot();
	    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
	    p.getOutline().setVisible(false);
	 
	    cwaBar.getTitle().getLabel().getCaption().setValue("Simple Bar Chart");
	 
	    // customize the legend
	    Legend lg = cwaBar.getLegend();
	    lg.getText().getFont().setSize(16);
	    lg.getInsets().set(10, 5, 0, 0);
	    lg.setAnchor(Anchor.NORTH_LITERAL);
	 
	    Series seCategory = SeriesImpl.create();
	    
	    List<Envelope> nonZeroBalanceEnvelopes = new LinkedList<Envelope>();
	    List<Envelope> children = envelopeService.getRootEnvelope().getChildren();
	    for (Envelope env : envelopeService.getRootEnvelope().getChildren()) {
	    	if (env.getBalance() > 0.0) {
	    		nonZeroBalanceEnvelopes.add(env);
	    	}
	    }
	    String[] names = new String[nonZeroBalanceEnvelopes.size()];
	    Double[] balances = new Double[nonZeroBalanceEnvelopes.size()];
	    int i=0;
	    for (Envelope env : nonZeroBalanceEnvelopes) {
	    	names[i] = env.getName();
	    	balances[i++] = env.getBalance();
	    }

	    TextDataSet categoryValues = TextDataSetImpl.create(names);
	    seCategory.setDataSet(categoryValues);
	    
	    NumberDataSet orthoValues1 = NumberDataSetImpl.create(balances);

	    // CREATE THE PRIMARY DATASET
	    PieSeries ls = (PieSeries) PieSeriesImpl.create();
	    
	    ls.setSeriesIdentifier("My Pie Series");
	    ls.setSliceOutline(ColorDefinitionImpl.CREAM());
	    ls.getLabel().setVisible(true);
	    ls.setDataSet(orthoValues1);
	    ActionValue actionValue = CallBackValueImpl.create(getClass().getCanonicalName());
		Action action = ActionImpl.create(ActionType.CALL_BACK_LITERAL, actionValue);
		Trigger onclick = TriggerImpl.create(TriggerCondition.ONCLICK_LITERAL,action);
		ls.getTriggers().add(onclick);
	    
	    seCategory.getDataDefinition().add(QueryImpl.create("G"));
	    SeriesDefinition sdX = SeriesDefinitionImpl.create();

	    sdX.getSeriesPalette().update(0);

	    // SET THE COLORS IN THE PALETTE
	     

	    SeriesDefinition sdY = SeriesDefinitionImpl.create();

	    sdY.getSeriesPalette().update(1);

	    // SET THE COLORS IN THE PALETTE
	    sdY.setQuery(QueryImpl.create("Items"));
	    sdX.getSeriesDefinitions().add(sdY);
	    sdX.getSeries().add(seCategory);
	    sdY.getSeries().add(ls);
	    ((ChartWithoutAxesImpl)cwaBar).getSeriesDefinitions().add(sdX);
	    
	    
	    
	    return cwaBar;
	}
	
}
