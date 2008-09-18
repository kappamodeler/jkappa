package com.plectix.simulator.components;

import java.util.List;


public class CRule {

	private List<CConnectedComponent> left;
	private List<CConnectedComponent> right;
	private Double activity;
	private String name;

	public String getName() {
		return name;
	}

	public Double getActivity() {
		return activity;
	}

	public void setActivity(Double activity) {
		this.activity = activity;
	}

	public CRule(List<CConnectedComponent> left, List<CConnectedComponent> right,String name, Double activity) {
		this.left = left;
		this.right = right;
		this.activity = activity;
		this.name=name;
	}

	public List<CConnectedComponent> getLeft() {
		return left;
	}

	public List<CConnectedComponent> getRight() {
		return right;
	}
}
