package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;

public class AbstractRule implements IAbstractComponent {
	private final String myName;
	private final List<AbstractAgent> myLHS;
	private final List<AbstractAgent> myRHS;
	private final double myRate;
	private final int myId;
	private static final IConnectedComponent EMPTY_LHS_CC = new CConnectedComponent(
			CConnectedComponent.EMPTY);
	private final boolean myIsStorify;
	
	public AbstractRule(List<AbstractAgent> left,
			List<AbstractAgent> right, String name,
			double ruleRate, int ruleID, boolean isStorify) {
		myRate = ruleRate;
		myLHS = left;
		
		myRHS = right;
		myName = name;
		myId = ruleID;
		myIsStorify = isStorify;
	}
	
	public String getName() {
		return myName;
	}
	
	public double getRate() {
		return myRate;
	}
	
	// uwaga! we won't wrap collections here, 'cos these are "temporary" classes  
	public List<AbstractAgent> getRHS() {
		return myRHS;
	}
	
	public List<AbstractAgent> getLHS() {
		return myLHS;
	}

	public int getID() {
		return myId;
	}

	public boolean isStorify() {
		return myIsStorify;
	}
}
