/**
 * 
 */
package com.plectix.simulator.simulationclasses.perturbations;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.staticanalysis.Observables;

/**
 * This class implements rate parameters for left handSide in "perturbation expression".
 * @author avokhmin
 * @see Perturbation
 */
// TODO rename or get rid
/*package*/ final class SumParameters implements PerturbationExpressionInterface {
	private final ObservableInterface observableComponent;
	private double parameter;
	
	/**
	 * Constructor of SumParameters with given <b>observableComponent</b> and
	 * <b>parameter</b> - correction factor.
	 * @param observableComponent given observableComponent
	 * @param parameter given correction factor
	 */
	public SumParameters(ObservableInterface observableComponent, double parameter) {
		this.observableComponent = observableComponent;
		this.parameter = parameter;
	}

	
	public final String getName() {
		if (observableComponent != null)
			return observableComponent.getName();
		return null;
	}

	public final String getValueToString() {
		return parameter + "";
	}

	public final double getMultiplication(Observables obs) {
		double multiply = 0.;
		multiply = this.observableComponent.getCurrentState(obs);
		return multiply * this.parameter;
	}

	public final double getValue() {
		return parameter;
	}

	public final void setValue(double value) {
		this.parameter = value;
	}
	
	/**
	 * Override standard "equals". Uses for simplification similar this.
	 */
	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof SumParameters))
			return false;
		SumParameters sp = (SumParameters) obj;
		if (observableComponent != sp.observableComponent)
			return false;
		return true;
	}

}