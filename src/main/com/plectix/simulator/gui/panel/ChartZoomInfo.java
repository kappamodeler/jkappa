package com.plectix.simulator.gui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DateRange;

/**
 * 
 * @author ecemis
 */
class ChartZoomInfo {
	private DateRange dateRange = null;
	private List<NumberAxis> yAxes = new ArrayList<NumberAxis>();
	
	public ChartZoomInfo() {
		super();
	}

	/**
	 * @param chart
	 */
	public void save(JFreeChart chart) {
		if (chart == null || chart.getPlot() == null) {
			yAxes.clear();
			return;
		}
		save((CombinedDomainXYPlot) chart.getPlot());
	}

	/**
	 * @param chart
	 */
	public boolean restore(JFreeChart chart) {
		if (chart == null || chart.getPlot() == null) {
			return false;
		}
		return restore((CombinedDomainXYPlot) chart.getPlot());
	}
	
	void save(CombinedDomainXYPlot combinedPlot) {
		DateAxis xAxis = (DateAxis) combinedPlot.getDomainAxis();
		dateRange = new DateRange(xAxis.getMinimumDate(), xAxis.getMaximumDate());
		
		yAxes.clear();
		List subPlots = combinedPlot.getSubplots();
		for (int i = 0; i< subPlots.size(); i++) {
			yAxes.add((NumberAxis) ((XYPlot)subPlots.get(i)).getRangeAxis());
		}
	}
	
	boolean restore(CombinedDomainXYPlot combinedPlot) {
		DateAxis xAxis = (DateAxis) combinedPlot.getDomainAxis();
		xAxis.setRange(dateRange.getLowerDate(), dateRange.getUpperDate());
		List subPlots = combinedPlot.getSubplots();
		if (subPlots.size() == yAxes.size()) {
			for (int i = 0; i< subPlots.size(); i++) {
				NumberAxis axis = (NumberAxis) ((XYPlot)subPlots.get(i)).getRangeAxis();
				axis.setLowerBound(yAxes.get(i).getLowerBound());
				axis.setUpperBound(yAxes.get(i).getUpperBound());
			}
			return true;
		}
		return false;
	}

}
