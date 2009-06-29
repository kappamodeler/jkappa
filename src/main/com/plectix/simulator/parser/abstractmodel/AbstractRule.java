package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

import com.plectix.simulator.parser.util.StringUtil;

public class AbstractRule implements IAbstractComponent {
	private final String myName;
	private final List<AbstractAgent> myLHS;
	private final List<AbstractAgent> myRHS;
	private final double myRate;
	private final int myId;
	private final boolean myIsStorify;
	// -1 is default value
	private final double binaryRate;
	
	public AbstractRule(List<AbstractAgent> left,
			List<AbstractAgent> right, String name,
			double ruleRate, double binaryRate, int ruleID, boolean isStorify) {
		myRate = ruleRate;
		myLHS = left;
		
		myRHS = right;
		myName = name;
		myId = ruleID;
		this.binaryRate = binaryRate;
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

	public double getBinaryRate() {
		return binaryRate;
	}
	
	public boolean isStorify() {
		return myIsStorify;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Collections.sort(myLHS);
		Collections.sort(myRHS);
		sb.append("'" + myName + "' ");
		sb.append(StringUtil.listToString(myLHS));
		sb.append(" -> ");
		sb.append(StringUtil.listToString(myRHS));
		
		return sb.toString();
	}
}
