package com.plectix.simulator.components;

import java.util.List;


public class CRule {

	private List<CConnectedComponent> leftHandSide;
	private List<CConnectedComponent> rightHandSide;
	private Double activity;
	private String name;


	public CRule(List<CConnectedComponent> left, List<CConnectedComponent> right,String name, Double activity) {
		this.leftHandSide = left;
		this.rightHandSide = right;
		setConnectedComponentLinkRule(left);
		setConnectedComponentLinkRule(right);
		this.activity = activity;
		this.name=name;
	}
	
	private final void setConnectedComponentLinkRule(List<CConnectedComponent> cList){
		if((cList==null)||(cList.isEmpty()))
			return;
		for(CConnectedComponent cc:cList)
			cc.setRule(this);
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
