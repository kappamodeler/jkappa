package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.component.Observables;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.interfaces.ObservableRuleInterface;
import com.plectix.simulator.streaming.LiveDataSourceInterface;
import com.plectix.simulator.streaming.LiveData.PlotType;

public final class ObservablesLiveDataSource implements LiveDataSourceInterface {

	private enum SimulatorPlotTypeEnum implements PlotType {
		OBSERVABLE, RULE;

		@Override
		public String getName() {
			return toString();
		}
	}
	
	private final int numberOfUniqueObservables;
	private final Observables observables;
	private final List<ObservableInterface> uniqueObservables;
	private final String[] plotNames;
	private final PlotType[] plotTypes;

	public ObservablesLiveDataSource(final Observables observables) {
		this.observables = observables;
		this.uniqueObservables = observables.getUniqueComponentList();
		this.numberOfUniqueObservables = uniqueObservables.size();
		this.plotNames = new String[numberOfUniqueObservables];
		this.plotTypes = new PlotType[numberOfUniqueObservables];
		
		// let's fill the plotNames, which should not change
		for (int i= 0; i < numberOfUniqueObservables; i++) {
		    ObservableInterface observableComponent = uniqueObservables.get(i);
			String observableName = observableComponent.getName();
			if (observableName == null) {
				observableName = observableComponent.getLine();
			}
			plotNames[i] = observableName;
			plotTypes[i] = (observableComponent instanceof ObservableRuleInterface) ? SimulatorPlotTypeEnum.RULE : SimulatorPlotTypeEnum.OBSERVABLE;
		}
	}

	public final double[] getPlotValues() {
		double[] values = new double[numberOfUniqueObservables];
		for (int i= 0; i < numberOfUniqueObservables; i++) {
		    values[i] = uniqueObservables.get(i).getLastValue();
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
