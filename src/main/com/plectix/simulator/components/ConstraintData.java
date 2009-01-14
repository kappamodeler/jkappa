package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class ConstraintData {
	public final static byte TYPE_CONSTRAINT_NORMAL = 0;
	public final static byte TYPE_CONSTRAINT_NO_POLY = 1;
	public final static byte TYPE_CONSTRAINT_NO_HELIX = 2;

	private byte type;

	private double activity;
	private double activityConstraint;

	private List<ConstraintExpression> expressionList;

	public ConstraintData(double activity) {
		this.activity = activity;
		this.type = TYPE_CONSTRAINT_NORMAL;
		this.activityConstraint = -1;
	}

	public ConstraintData(byte type, double activity, double activityConstraint) {
		this.type = type;
		this.activity = activity;
		this.activityConstraint = activityConstraint;
	}

	public List<ConstraintExpression> getExpressionList() {
		return expressionList;
	}

	public void addExpression(ConstraintExpression ce) {
		if (expressionList == null)
			expressionList = new ArrayList<ConstraintExpression>();
		expressionList.add(ce);
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setActivity(double activity) {
		this.activity = activity;
	}

	public void setActivityConstraint(double activityConstraint) {
		this.activityConstraint = activityConstraint;
	}

	public byte getType() {
		return type;
	}

	public double getActivity() {
		return activity;
	}

	public double getActivityConstraint() {
		return activityConstraint;
	}

	@Override
	public String toString() {
		String st = Double.toString(activity);
		if (activityConstraint >= 0)
			st += "(" + Double.toString(activityConstraint) + ")";
		switch (type) {
		case TYPE_CONSTRAINT_NO_HELIX:
			st += "[" + "NO_HELIX" + "]";
			break;
		case TYPE_CONSTRAINT_NO_POLY:
			st += "[" + "NO_POLY" + "]";
			break;
		}
		return st;
	}
}
