package com.plectix.simulator.staticanalysis.observables;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.simulator.api.steps.experiments.Pattern;

public class ObservablesStatesVisitor {
	private final ObservablesStatesHandler statesHandler;
	
	public ObservablesStatesVisitor(ObservablesStatesHandler strategy) { 
		this.statesHandler = strategy;
	}
	
	public double visit(Observables observables, Pattern<?> pattern) {
		ObservableInterface observable = observables.getComponentManager().findObservable(pattern);
		if (observable == null) {
			throw new RuntimeException("There is no observable like " + pattern);
		}
		for (int i = 0; i < observables.getCountTimeList().size(); i++) {
			statesHandler.visit(observable.getItem(i, observables));
		}
		return statesHandler.getResult();
	}
	
	public double visit(Observables observables, String observableName) {
		ObservableInterface observable = observables.getComponentManager().findObservable(observableName);
		if (observable == null) {
			throw new RuntimeException("There is no observable with name " + observableName);
		}
		for (int i = 0; i < observables.getCountTimeList().size(); i++) {
			statesHandler.visit(observable.getItem(i, observables));
		}
		return statesHandler.getResult();
	}
}
