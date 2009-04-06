/**
 * 
 */
package com.plectix.simulator.components.perturbations;

import java.io.Serializable;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.interfaces.*;

/*package*/ final class SumParameters implements IPerturbationExpression, Serializable {
	/**
	 * 
	 */
	private final IObservablesComponent observableID;
	private double parameter;
	
	public SumParameters(IObservablesComponent observableID, double parameter) {
		this.observableID = observableID;
		this.parameter = parameter;
	}

	
	public final IObservablesComponent getObservablesComponent() {
		return this.observableID;
	}

	public final String getName() {
		if (observableID != null)
			return observableID.getName();
		return null;
	}

	public final String getValueToString() {
		//return Double.valueOf(parameter).toString();
		return parameter + "";
	}

	// public double getMultiply(CObservables observables) {
	// double multiply = 0.;
	// multiply = this.observableID.getSize();
	// return multiply * this.parameter;
	// }

	public final double getMultiplication(CObservables obs) {
		double multiply = 0.;
		multiply = this.observableID.getCurrentState(obs);
		return multiply * this.parameter;
	}

	public final double getValue() {
		return parameter;
	}

	public final void setValue(double value) {
		this.parameter = value;
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof SumParameters))
			return false;
		SumParameters sp = (SumParameters) obj;
		if (observableID != sp.observableID)
			return false;
		return true;
	}

}