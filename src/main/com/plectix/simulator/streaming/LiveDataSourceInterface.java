package com.plectix.simulator.streaming;

import com.plectix.simulator.streaming.LiveData.PlotType;

public interface LiveDataSourceInterface {

	public abstract double[] getPlotValues();

	public abstract String[] getPlotNames();

	public abstract PlotType[] getPlotTypes();

}