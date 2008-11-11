package com.plectix.simulator.interfaces;

public interface IPerturbationExpression {
	public String getName();

	public String getValueToString();

	public double getMultiplication();

	public double getValue();

	public void setValue(double value);
}
