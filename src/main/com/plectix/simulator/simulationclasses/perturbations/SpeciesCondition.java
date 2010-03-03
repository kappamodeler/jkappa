package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ConditionType;
import com.plectix.simulator.simulationclasses.perturbations.util.LinearExpression;
import com.plectix.simulator.simulationclasses.perturbations.util.VectorObservable;
import com.plectix.simulator.staticanalysis.observables.Observables;
import com.plectix.simulator.util.InequalitySign;

public class SpeciesCondition implements ConditionInterface {
	// the one in left hand side
	private final ObservableInterface pickedObservable;
	private final LinearExpression<VectorObservable> expression;
	private final Observables observables;
	private final InequalitySign inequalitySign;
	
	public SpeciesCondition(ObservableInterface watchedObservable,
			LinearExpression<VectorObservable> expression, InequalitySign inequalitySign, Observables observables) {
		super();
		this.pickedObservable = watchedObservable;
		this.expression = expression;
		this.observables = observables;
		this.inequalitySign = inequalitySign;
	}

	private final double countObservable(ObservableInterface observable) {
		return this.pickedObservable.getCurrentState(observables);
	}
	
	@Override
	public boolean check(double currentTime) {
		double quantity = countObservable(pickedObservable);
		return inequalitySign.satisfy(quantity, expression.calculate()); 
	}

	@Override
	public ConditionType getType() {
		return ConditionType.SPECIES;
	}

	@Override
	public InequalitySign inequalitySign() {
		return inequalitySign;
	}

	public ObservableInterface getPickedObservable() {
		return pickedObservable;
	}

	public LinearExpression<VectorObservable> getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("'");
		sb.append(pickedObservable.getName());
		sb.append("' " + inequalitySign + " ");
		sb.append(expression);
		return sb.toString();
	}
}
