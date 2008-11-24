/**
 * 
 */
package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;

public class SumParameters implements IPerturbationExpression {
	/**
	 * 
	 */
	private IObservablesComponent observableID;
	double parameter;

	public final IObservablesComponent getObservablesComponent() {
		return this.observableID;
	}

	public SumParameters(IObservablesComponent observableID, double parameter) {
		this.observableID = observableID;
		this.parameter = parameter;
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

	public String getName() {
		if (observableID != null)
			return observableID.getName();
		return null;
	}

	public String getValueToString() {
		return Double.valueOf(parameter).toString();
	}

	// public double getMultiply(CObservables observables) {
	// double multiply = 0.;
	// multiply = this.observableID.getSize();
	// return multiply * this.parameter;
	// }

	public double getMultiplication() {
		double multiply = 0.;
		multiply = this.observableID.getSize();
		return multiply * this.parameter;
	}

	public double getValue() {
		return parameter;
	}

	public void setValue(double value) {
		this.parameter = value;
	}
}