package com.plectix.simulator.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.plectix.simulator.gui.lib.ColorMap;
import com.plectix.simulator.gui.lib.GridBagPanel;

/**
 * <p>TODO document GraphPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
public class GraphPanel extends GridBagPanel implements ControlPanelListener {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(GraphPanel.class);

	private static final boolean SHOW_LEGEND = true;
	
	private static final Shape RECTANGLE = new Rectangle(-3, -3, 6, 6);
	@SuppressWarnings("unused")
	private static final Shape UP_TRIANGLE = new Polygon(new int[]{-5, 0, 5}, new int[]{12, 0, 12}, 3);
	@SuppressWarnings("unused")
	private static final Shape DOWN_TRIANGLE = new Polygon(new int[]{-5, 0, 5}, new int[]{-12, 0, -12}, 3);
	
	private org.jfree.chart.ChartPanel jfreeChartPanel = null;
	private ChartZoomInfo chartZoomInfo = new ChartZoomInfo();
	
	private TimeSeries memoryUsageTimeSeries = null;
	
	public GraphPanel() {
		super();
	}

	public void initialize() {
		GridBagConstraintsEx gc = createNewConstraints().insets(5, 5, 5, 5);
		
		// Chart Panel
		jfreeChartPanel = new org.jfree.chart.ChartPanel(null, false);
		jfreeChartPanel.setMinimumSize(new Dimension(800, 600));
		jfreeChartPanel.setPreferredSize(new Dimension(800, 600));
		jfreeChartPanel.setMaximumDrawWidth(2000);
		jfreeChartPanel.setMaximumDrawHeight(1500);
		jfreeChartPanel.setMinimumDrawWidth(400);
		jfreeChartPanel.setMinimumDrawHeight(300);
		add(jfreeChartPanel, gc.fillBoth());
	}

	public final void addMemoryUsageData(final long currentTimeMillis, final long memoryUsage) {
		// LOGGER.info(currentTimeMillis + ": " + (memoryUsage/1024.0/1024.0));
		memoryUsageTimeSeries.add(new FixedMillisecond(currentTimeMillis), memoryUsage/1024.0/1024.0);	
		updateChart();
	}

	public final void resetCharts() {
		memoryUsageTimeSeries = new TimeSeries("Total Memory Usage (MB)");
		memoryUsageTimeSeries.setDescription("Total Memory Usage (MB)");
		memoryUsageTimeSeries.setDomainDescription("Domain");
		memoryUsageTimeSeries.setRangeDescription("Range");
	}
	
	/**
	 * 
	 */
	private void updateChart() {		
		// Create X axis:
		DateAxis xAxis = new DateAxis();
		xAxis.setLowerMargin(0.0);
		xAxis.setUpperMargin(0.0);
		xAxis.setLabel("Date");
		   
		// Combine the plots
        CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(xAxis);
        combinedPlot.setOrientation(PlotOrientation.VERTICAL);
        combinedPlot.setGap(8);
        
        boolean seriesExists = true;
        combinedPlot.add(createTimeSeriesPlot(xAxis), 1);
        
        // Create Chart:
        if (seriesExists) {
        	jfreeChartPanel.setChart(new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, SHOW_LEGEND));
        	jfreeChartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
        } else {
            jfreeChartPanel.setChart(null);
        }
	}
	
	private XYPlot createTimeSeriesPlot(DateAxis xAxis) {
		// Create Y axis:
        NumberAxis yAxis = new NumberAxis();
		yAxis.setLowerMargin(0.02);
		yAxis.setUpperMargin(0.02);
        yAxis.setAutoRangeIncludesZero(true);
        
        // Plot:
        XYPlot plot = new XYPlot();
		plot.setBackgroundPaint(new Color(255,255,240));
		plot.setDomainAxis(xAxis);
		plot.setRangeAxis(yAxis);
		
		XYItemRenderer renderer = new ItemRenderer(); // XYLineAndShapeRenderer(true, false);

		renderer.setSeriesPaint(0, ColorMap.getColor(0));
//		renderer.setSeriesStroke(0, new BasicStroke(5));
		
		plot.setDataset(0, new TimeSeriesCollection(memoryUsageTimeSeries));
		plot.setRenderer(0, renderer);
	
		return plot;
	}
	
	//***************************************************************************************
	/**
	 * 
	 * @author ecemis
	 */
	private static final class ItemRenderer extends XYLineAndShapeRenderer {
		private static final long serialVersionUID = 1L;
		
		public ItemRenderer() {
			super(true, true);

			Shape shape = RECTANGLE;
			Color color = Color.BLUE;
			
			setBaseShape(shape);
			setBaseShape(shape);
			setBaseItemLabelPaint(color);
			setBasePaint(color);
		}
	}

}
