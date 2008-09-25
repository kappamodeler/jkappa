package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CRule {

	private List<CConnectedComponent> leftHandSide;
	private List<CConnectedComponent> rightHandSide;
	private double activity = 0.;
	private String name;
	private double ruleRate;
	private int automorphismNumber = 1;

	public int getAutomorphismNumber() {
		return automorphismNumber;
	}

	public void setAutomorphismNumber(int automorphismNumber) {
		this.automorphismNumber = automorphismNumber;
	}

	public CRule(List<CConnectedComponent> left,
			List<CConnectedComponent> right, String name, double ruleRate) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		this.ruleRate = ruleRate;
		this.name = name;
		calculateAutomorphismsNumber();
	}

	private void calculateAutomorphismsNumber() {
		if (this.leftHandSide.size() == 2) {
			if (this.leftHandSide.get(0).unify(
					this.leftHandSide.get(1).getAgents().get(0))
					&& this.leftHandSide.get(1).unify(
							this.leftHandSide.get(0).getAgents().get(0)))
				automorphismNumber = 2;
		}
	}

	private final void setConnectedComponentLinkRule(
			List<CConnectedComponent> cList) {
		if (cList == null)
			return;
		for (CConnectedComponent cc : cList)
			cc.setRule(this);
	}

	public List<CInjection> getSomeInjectionList() {
		List<CInjection> list = new ArrayList<CInjection>();
		Random rand = new Random(); 
		for (CConnectedComponent cc : this.leftHandSide) {
			list.add(cc.getInjectionsList().get(rand.nextInt(cc.getInjectionsList().size())));
		}
		return list;
	}

	public void calcultateActivity() {
		activity = 1.;
		for (CConnectedComponent cc : this.leftHandSide){
			activity *= cc.getInjectionsList().size();
		}
		activity *= ruleRate;
		activity /= automorphismNumber;
	}

	public final String getName() {
		return name;
	}

	public final Double getActivity() {

		return activity;
	}

	public final void setActivity(Double activity) {
		this.activity = activity;
	}

	public final List<CConnectedComponent> getLeftHandSide() {
		return leftHandSide;
	}

	public final List<CConnectedComponent> getRightHandSide() {
		return rightHandSide;
	}

}
