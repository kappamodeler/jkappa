package com.plectix.simulator.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.plectix.simulator.gui.lib.ColorMap;
import com.plectix.simulator.gui.lib.GridBagPanel;
import com.plectix.simulator.streaming.LiveData;

/**
 * <p>TODO document GraphPanel
 * </p>
 * @version $Id$
 * @author ecemis
 */
public class GraphPanel extends GridBagPanel implements ControlPanelListener {
	private static final long serialVersionUID = 1L;
	
	private static final double MEGA = 1024.0 * 1024.0;

	private static final Logger LOGGER = Logger.getLogger(GraphPanel.class);

	private static final boolean SHOW_LEGEND = true;
	
	private static final Shape RECTANGLE = new Rectangle(-3, -3, 6, 6);
	@SuppressWarnings("unused")
	private static final Shape UP_TRIANGLE = new Polygon(new int[]{-5, 0, 5}, new int[]{12, 0, 12}, 3);
	@SuppressWarnings("unused")
	private static final Shape DOWN_TRIANGLE = new Polygon(new int[]{-5, 0, 5}, new int[]{-12, 0, -12}, 3);

	private org.jfree.chart.ChartPanel observablesChartPanel = null;
	private org.jfree.chart.ChartPanel memoryChartPanel = null;
	
	private ChartZoomInfo chartZoomInfo = new ChartZoomInfo();
	
	private TimeSeries memoryUsageTimeSeriesHeap = null;
	private TimeSeries memoryUsageTimeSeriesNonHeap = null;
	private TimeSeries memoryUsageTimeSeriesTotal = null;
	
	public GraphPanel() {
		super();
	}

	public void initialize() {
		GridBagConstraintsEx gc = createNewConstraints().insets(5, 5, 5, 5);

		// Chart Panels
		observablesChartPanel = createNewChartPanel();
		memoryChartPanel = createNewChartPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab("Observables", observablesChartPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("Memory", memoryChartPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		tabbedPane.setSelectedIndex(1);
		
		add(tabbedPane, gc.fillBoth());
	}

	private final org.jfree.chart.ChartPanel createNewChartPanel() {
		org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(null, false);
		chartPanel.setMinimumSize(new Dimension(800, 600));
		chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.setMaximumDrawWidth(2000);
		chartPanel.setMaximumDrawHeight(1500);
		chartPanel.setMinimumDrawWidth(400);
		chartPanel.setMinimumDrawHeight(300);
		return chartPanel;
	}

	public void updateLiveDataChart(LiveData liveData) {
		// TODO update XY Series for observablesChartPanel
		// Then call updateLiveDataChartPanel()
		
	}
	
	public final void updateMemoryUsageChart() {
		final MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		final long currentHeap = mbean.getHeapMemoryUsage().getUsed();
		final long currentNonHeap = mbean.getNonHeapMemoryUsage().getUsed();
		final long currentTimeMillis = System.currentTimeMillis();
		
		// LOGGER.info(currentTimeMillis + ": " + (memoryUsage/1024.0/1024.0));
		memoryUsageTimeSeriesHeap.add(new FixedMillisecond(currentTimeMillis), currentHeap/MEGA);	
		memoryUsageTimeSeriesNonHeap.add(new FixedMillisecond(currentTimeMillis), currentNonHeap/MEGA);	
		memoryUsageTimeSeriesTotal.add(new FixedMillisecond(currentTimeMillis), (currentHeap + currentNonHeap)/MEGA);	
		
		updateMemoryChartPanel();
	}

	public final void resetCharts() {
		memoryUsageTimeSeriesHeap = new TimeSeries("Heap Memory Usage (MB)");
		memoryUsageTimeSeriesNonHeap = new TimeSeries("Non-Heap Memory Usage (MB)");
		memoryUsageTimeSeriesTotal = new TimeSeries("Total Memory Usage (MB)");
	}
	
	/**
	 * 
	 */
	private void updateMemoryChartPanel() {		
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
        	memoryChartPanel.setChart(new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, SHOW_LEGEND));
        	memoryChartPanel.getChart().setBackgroundPaint(new Color(0, 0, 0, 0));
        } else {
            memoryChartPanel.setChart(null);
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
		
		/*
		XYItemRenderer renderer = new ItemRenderer(); 
		renderer.setSeriesPaint(0, ColorMap.getColor(0));
		renderer.setSeriesPaint(1, ColorMap.getColor(1));
		renderer.setSeriesPaint(2, ColorMap.getColor(2));
//		renderer.setSeriesStroke(0, new BasicStroke(5));
		*/
		
		plot.setDataset(0, new TimeSeriesCollection(memoryUsageTimeSeriesHeap));
		plot.setDataset(1, new TimeSeriesCollection(memoryUsageTimeSeriesNonHeap));
		plot.setDataset(2, new TimeSeriesCollection(memoryUsageTimeSeriesTotal));
		
		plot.setRenderer(0, new ItemRenderer(ColorMap.getColor(0)));  // XYLineAndShapeRenderer(true, false);
		plot.setRenderer(1, new ItemRenderer(ColorMap.getColor(1)));
		plot.setRenderer(2, new ItemRenderer(ColorMap.getColor(2)));
	
		return plot;
	}
	
	//***************************************************************************************
	/**
	 * 
	 * @author ecemis
	 */
	private static final class ItemRenderer extends XYLineAndShapeRenderer {
		private static final long serialVersionUID = 1L;
		
		public ItemRenderer(final Color color) {
			super(true, true);

			Shape shape = RECTANGLE;
			
			setBaseShape(shape);
			setBaseShape(shape);
			setBaseItemLabelPaint(color);
			setBasePaint(color);
		}
	}


}
