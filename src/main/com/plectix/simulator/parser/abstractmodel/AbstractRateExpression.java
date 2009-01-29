package com.plectix.simulator.parser.abstractmodel;

public class AbstractRateExpression {
	private final String myRuleName;
	private final double myValue;
	
	public AbstractRateExpression(String name, double value) {
		myRuleName = name;
		myValue = value;
	}
}
