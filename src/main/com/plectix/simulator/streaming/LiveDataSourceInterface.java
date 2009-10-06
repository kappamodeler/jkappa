package com.plectix.simulator.streaming;

import com.plectix.simulator.streaming.LiveData.PlotType;

public interface LiveDataSourceInterface {

	public double[] getPlotValues();

	public String[] getPlotNames();

	public PlotType[] getPlotTypes();

}