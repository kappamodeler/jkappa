package com.plectix.simulator.streaming;

import java.util.Collection;

/**
 * This class wraps live data that includes:
 * 
 * 1-) A description of each plot
       -) Name
       -) Type (solution observable or rule)

   2-) An array of samples, each containing
       -) The time
       -) 1 value for each plot
 * @author ecemis
 */
public class LiveData {

	public enum PlotType {
		OBSERVABLE, RULE
	}
	
	private String[] plotNames;
	
	private PlotType[] plotTypes;
	
	private Collection<LiveDataPoint> compressedData;

	public LiveData(String[] plotNames, PlotType[] plotTypes, Collection<LiveDataPoint> compressedData) {
		super();
		this.plotNames = plotNames;
		this.plotTypes = plotTypes;
		this.compressedData = compressedData;
	}

	public final int getNumberOfPlots() { 
		if (plotNames == null) {
			return 0;
		}
		return plotNames.length;
	}
	
	public final Collection<LiveDataPoint> getData() {
		return compressedData;
	}

	public final String[] getPlotNames() {
		return plotNames;
	}
	
	public final PlotType[] getPlotTypes() {
		return plotTypes;
	}

}
