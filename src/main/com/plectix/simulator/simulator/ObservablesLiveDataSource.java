package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.streaming.LiveDataSourceInterface;
import com.plectix.simulator.streaming.LiveData.PlotType;

public class ObservablesLiveDataSource implements LiveDataSourceInterface {
	
	private final int numberOfUniqueObservables;
	private final CObservables observables;
	private final List<IObservablesComponent> uniqueObservables;
	private final String[] plotNames;
	private final PlotType[] plotTypes;

	public ObservablesLiveDataSource(final CObservables observables) {
		this.observables = observables;
		this.uniqueObservables = observables.getUniqueComponentList();
		this.numberOfUniqueObservables = uniqueObservables.size();
		this.plotNames = new String[numberOfUniqueObservables];
		this.plotTypes = new PlotType[numberOfUniqueObservables];
		
		// let's fill the plotNames, which should not change
		for (int i= 0; i < numberOfUniqueObservables; i++) {
		    IObservablesComponent observableComponent = uniqueObservables.get(i);
			String observableName = observableComponent.getName();
			if (observableName == null) {
				observableName = observableComponent.getLine();
			}
			plotNames[i] = observableName;
			// TODO: Set plot type here:
			// plotTypes[i] = PlotType.OBSERVABLE OR PlotType.RULE; 
		}
	}

	public final double[] getPlotValues() {
		double[] values = new double[numberOfUniqueObservables];
		for (int i= 0; i < numberOfUniqueObservables; i++) {
			// TODO: The following statement looks weird: Why do we have to pass observables to getCurrentState() method?
		    values[i] = uniqueObservables.get(i).getCurrentState(observables);
		}
		return values;
	}

	public final String[] getPlotNames() {
		return plotNames;
	}

	public final PlotType[] getPlotTypes() {
		return plotTypes;
	}
}
