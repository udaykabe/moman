package net.deuce.moman.ui.demo;

import java.util.HashMap;
import java.util.Map;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.device.ICallBackNotifier;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.factory.RunTimeContext;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.ActionType;
import org.eclipse.birt.chart.model.attribute.ActionValue;
import org.eclipse.birt.chart.model.attribute.Anchor;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.CallBackValue;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.TriggerCondition;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.CallBackValueImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Action;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.Trigger;
import org.eclipse.birt.chart.model.data.impl.ActionImpl;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TriggerImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.chart.util.PluginSettings;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ibm.icu.util.ULocale;


public class SWTViewer extends Canvas implements ICallBackNotifier, PaintListener
{

	private IDeviceRenderer idr = null;

	private Chart cm = null;

	private static Combo cbType = null;

	private static Button btn = null;

	private GeneratedChartState gcs = null;

	private boolean bNeedsGeneration = true;
	
	private Map contextMap;

	private RunTimeContext context;

	private Envelope envelope;

	private boolean drilldown = true;
	
	private PieSeries pieSeries;

	/**
	 * Get the connection with SWT device to render the graphics.
	 */
	public SWTViewer( Composite parent, int style )
	{
		super( parent, style );
		
		addPaintListener(this);
		
		contextMap = new HashMap();
		
		final PluginSettings ps = PluginSettings.instance( );
		try
		{
			Envelope unassigned = ServiceNeeder.instance().getEnvelopeService().getUnassignedEnvelope();
			envelope = ServiceNeeder.instance().getEnvelopeService().getRootEnvelope();
			idr = ps.getDevice( "dv.SWT" );//$NON-NLS-1$
			idr.setProperty( IDeviceRenderer.UPDATE_NOTIFIER, this );
			cm = createChart();
			context = Generator.instance().prepare(cm, null, null,
					ULocale.getDefault());
			addInteractivity(cm, this.getClass());
			
		}
		catch ( ChartException ex )
		{
			ex.printStackTrace( );
		}

	}
	
	public Chart createChart() {
	    // bart charts are based on charts that contain axes
		ChartWithoutAxesImpl cwaBar = (ChartWithoutAxesImpl) ChartWithoutAxesImpl.create();
	    cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());
	    cwaBar.getBlock().getOutline().setVisible(true);
	    cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
	    
	    // customize the plot
	    Plot p = cwaBar.getPlot();
	    p.getClientArea().setBackground(ColorDefinitionImpl.create(255, 255, 225));
	    p.getOutline().setVisible(false);
	 
	    if (envelope != null) {
		    cwaBar.getTitle().getLabel().getCaption().setValue(envelope.getChartLegendLabel());
	    }
	 
	    // customize the legend
	    Legend lg = cwaBar.getLegend();
	    lg.getText().getFont().setSize(16);
	    lg.getInsets().set(10, 5, 0, 0);
	    lg.setAnchor(Anchor.NORTH_LITERAL);
	 
	    Series seCategory = SeriesImpl.create();
	    
	    // CREATE THE PRIMARY DATASET
	    pieSeries = (PieSeries) PieSeriesImpl.create();
	    
	    pieSeries.setSeriesIdentifier("");
	    pieSeries.setSliceOutline(ColorDefinitionImpl.CREAM());
	    pieSeries.getLabel().setVisible(false);
	    
	    seCategory.getDataDefinition().add(QueryImpl.create("Envelope"));
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
	    pieSeries.getDataDefinition().add(QueryImpl.create("Value"));
	    sdY.getRunTimeSeries().add(pieSeries);
	    ((ChartWithoutAxesImpl)cwaBar).getSeriesDefinitions().add(sdX);
	    
	    return cwaBar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl( PaintEvent e )
	{
		// Bind data before rendering
		ChartModel.bindData(cm, context, envelope);
		
		Rectangle d = this.getClientArea( );
		Image imgChart = new Image( this.getDisplay( ), d );
		GC gcImage = new GC( imgChart );
		idr.setProperty( IDeviceRenderer.GRAPHICS_CONTEXT, gcImage );

		Bounds bo = BoundsImpl.create( 0, 0, d.width, d.height );
		bo.scale( 72d / idr.getDisplayServer( ).getDpiResolution( ) );

		Generator gr = Generator.instance( );
		if ( bNeedsGeneration )
		{
			bNeedsGeneration = false;
			try
			{
				gcs = gr.build( idr.getDisplayServer( ),
						cm,
						bo,
						null,
						null,
						null );
			}
			catch ( ChartException ce )
			{
				ce.printStackTrace( );
			}
		}

		try
		{
			gr.render( idr, gcs );
			GC gc = e.gc;
			gc.drawImage( imgChart, d.x, d.y );
		}
		catch ( ChartException ce )
		{
			ce.printStackTrace( );
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.device.swing.IUpdateNotifier#getDesignTimeModel()
	 */
	public Chart getDesignTimeModel( )
	{
		return cm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.device.swing.IUpdateNotifier#getRunTimeModel()
	 */
	public Chart getRunTimeModel( )
	{
		return gcs.getChartModel( );
	}

	public Object peerInstance( )
	{
		return this;
	}

	public void regenerateChart( )
	{
		bNeedsGeneration = true;
		redraw( );
	}

	public void repaintChart( )
	{
		redraw( );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.device.IUpdateNotifier#getContext(java.lang.Object)
	 */
	public Object getContext( Object key )
	{
		return contextMap.get( key );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.device.IUpdateNotifier#putContext(java.lang.Object,
	 *      java.lang.Object)
	 */
	public Object putContext( Object key, Object value )
	{
		return contextMap.put( key, value );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.device.IUpdateNotifier#removeContext(java.lang.Object)
	 */
	public Object removeContext( Object key )
	{
		return contextMap.remove( key );
	}
	
	public void callback( Object event, Object source, CallBackValue value )
	{
		if (source instanceof WrappedStructureSource) {
			boolean refresh = false;
			DataPointHints hint = (DataPointHints) ((WrappedStructureSource) source).getSource();

			// Change query
			// query = "Select * from SampleData where Category='"+
			// hint.getBaseValue()+"'";

			for (Envelope env : envelope.getChildren()) {
				if (hint.getBaseDisplayValue().equals(env.getChartLegendLabel())) {
					
					int count = 0;
					for (Envelope child : env.getChildren()) {
						if (child.getBalance() != 0) {
							count++;
						}
					}
					if (count > 0) {
						envelope = env;
					}
					break;
				}
			}

			if (refresh) {
				// Change title
				cm.getTitle().getLabel().getCaption().setValue(envelope.getChartLegendLabel());

				// Change color by Series
				cm.getLegend().setItemType(LegendItemType.SERIES_LITERAL);

				regenerateChart();
			}
		}
	}
	
	public void addInteractivity(Chart cm, Class clazz)
	{
		
		ActionValue actionValue = CallBackValueImpl.create(clazz.getCanonicalName());
		Action action = ActionImpl.create(ActionType.CALL_BACK_LITERAL, actionValue);
		Trigger onclick = TriggerImpl.create(TriggerCondition.ONCLICK_LITERAL,action);
		pieSeries.getTriggers().add(onclick);
		
	}

}

