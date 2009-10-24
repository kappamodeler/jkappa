package com.plectix.simulator.staticanalysis.influencemap;

public class InfluenceMapEdge {
	private final int sourceRule;
	private final int targetRule;

	public InfluenceMapEdge(int fromRule, int toRule) {
		this.sourceRule = fromRule;
		this.targetRule = toRule;
	}

	public int getTargetRule() {
		return targetRule;
	}

	@Override
	public final String toString() {
		return new String("from rule: " + (sourceRule + 1) + " toRule: " + (targetRule + 1));
	}
}
