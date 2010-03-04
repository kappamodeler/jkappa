package com.plectix.simulator.staticanalysis.observables;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.simulator.api.steps.experiments.Pattern;

public class ObservableComponentsManager {
	private final Observables observables;
	
	ObservableComponentsManager(Observables observables) {
		this.observables = observables;
	}
	
	public final ObservableInterface findObservable(String observableName) {
		for (ObservableInterface observable : observables.getComponentList()) {
			if (observableName.equals(observable.getName())) {
				return observable;
			}
		}
		return null;
	}
	
	public final ObservableInterface findObservable(Pattern<?> pattern) {
		for (ObservableInterface observable : observables.getComponentList()) {
			if (observable.matches(pattern)) {
				return observable;
			}
		}
		return null;
	}

	public final double getFinalComponentState(String observableName) {
		ObservableInterface observable = this.findObservable(observableName);
		return (observable == null) ? -1 : observable.getLastValue();
	}

	public final double getFinalComponentState(Pattern<?> pattern) {
		ObservableInterface observable = this.findObservable(pattern);
		return (observable == null) ? -1 : observable.getLastValue();
	}
	
	public final double getMaxComponentState(String observableName) {
		ObservablesStatesVisitor stateHandler = new ObservablesStatesVisitor(new MaxStateFinder());
		return stateHandler.visit(observables, observableName);
	}

	public final double getMaxComponentState(Pattern<?> pattern) {
		ObservablesStatesVisitor stateHandler = new ObservablesStatesVisitor(new MaxStateFinder());
		return stateHandler.visit(observables, pattern);
	}
}
