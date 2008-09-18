package com.plectix.simulator.components;

import java.util.List;

public class CRule {

	private List<CConnectedComponent> left;
	private List<CConnectedComponent> right;

	public CRule(List<CConnectedComponent> left, List<CConnectedComponent> right) {
		this.left = left;
		this.right = right;
	}

	public List<CConnectedComponent> getLeft() {
		return left;
	}

	public List<CConnectedComponent> getRight() {
		return right;
	}
}
