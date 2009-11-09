package com.plectix.simulator.simulationclasses.perturbations.util;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.staticanalysis.Observables;

public class VectorObservable implements Vector {
	private final ObservableInterface observable;
	private final Observables observables;
	
	public VectorObservable(ObservableInterface observable, Observables observables) {
		this.observable = observable;
		this.observables = observables;
	}
	
	@Override
	public double getValue() {
		return this.observable.getCurrentState(observables);
	}

	@Override
	public String getName() {
		return "['" + observable.getName() + "']";
	}
}
