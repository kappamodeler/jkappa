package com.plectix.simulator.parser.abstractmodel;

import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.util.StringUtil;

public final class ModelRule {
	private final String name;
	private final List<ModelAgent> leftHandSideAgents;
	private final List<ModelAgent> rightHandSideAgents;
	private final double rate;
	private final int id;
	private final boolean isStorify;
	// -1 is default value
	private final double binaryRate;

	public ModelRule(List<ModelAgent> left, List<ModelAgent> right,
			String name, double ruleRate, double binaryRate, int ruleID,
			boolean isStorify) {
		this.rate = ruleRate;
		this.leftHandSideAgents = left;
		this.rightHandSideAgents = right;
		this.name = name;
		this.id = ruleID;
		this.binaryRate = binaryRate;
		this.isStorify = isStorify;
	}

	public final String getName() {
		return name;
	}

	public final double getRate() {
		return rate;
	}

	public final List<ModelAgent> getRHS() {
		return rightHandSideAgents;
	}

	public final List<ModelAgent> getLHS() {
		return leftHandSideAgents;
	}

	public final int getID() {
		return id;
	}

	public final double getBinaryRate() {
		return binaryRate;
	}

	public final boolean isStorify() {
		return isStorify;
	}

	@Override
	public final String toString() {
		final StringBuffer sb = new StringBuffer();
		if (leftHandSideAgents != null)
			Collections.sort(leftHandSideAgents);
		if (rightHandSideAgents != null)
			Collections.sort(rightHandSideAgents);
		sb.append("'" + name + "' ");
		if (leftHandSideAgents != null)
			sb.append(StringUtil.listToString(leftHandSideAgents));
		sb.append(" -> ");
		if(rightHandSideAgents!=null)
		sb.append(StringUtil.listToString(rightHandSideAgents));
		return sb.toString();
	}
}
