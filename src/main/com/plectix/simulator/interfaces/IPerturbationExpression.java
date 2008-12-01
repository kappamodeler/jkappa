package com.plectix.simulator.interfaces;

public interface IPerturbationExpression {
	public String getName();

	public String getValueToString();

	public double getMultiplication(IObservables obs);

	public double getValue();

	public void setValue(double value);
}
