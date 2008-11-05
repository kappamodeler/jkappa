/**
 * 
 */
package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.IObservablesComponent;

class SumParameters {
	/**
	 * 
	 */
	private IObservablesComponent observableID;
	double parameter;

	public SumParameters(IObservablesComponent observableID, double parameter) {
		this.observableID = observableID;
		this.parameter = parameter;
	}

	public double getMultiply(CObservables observables) {
		double multiply = 0.;
		multiply = this.observableID.getSize();
		return multiply * this.parameter;
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