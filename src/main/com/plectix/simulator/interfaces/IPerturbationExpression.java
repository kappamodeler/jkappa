package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CObservables;

public interface IPerturbationExpression {
	public String getName();

	public String getValueToString();

	public double getMultiplication(CObservables obs);

	public double getValue();

	public void setValue(double value);
}
