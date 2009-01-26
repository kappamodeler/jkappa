package com.plectix.simulator.parser.abstractmodel;

import java.util.List;

import com.plectix.simulator.interfaces.IConnectedComponent;

public class AbstractPerturbationRule extends AbstractRule {

	public AbstractPerturbationRule(List<IConnectedComponent> left,
			List<IConnectedComponent> right, String name, double ruleRate,
			int ruleID, boolean isStorify) {
		super(left, right, name, ruleRate, ruleID, isStorify);
	}
	
	public void setCount(double countToFile) {
		// TODO Auto-generated method stub
		
	}

}
