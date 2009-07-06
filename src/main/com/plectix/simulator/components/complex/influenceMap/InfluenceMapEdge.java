package com.plectix.simulator.components.complex.influenceMap;

class InfluenceMapEdge {
	private final int fromRule;
	private final int toRule;

	public InfluenceMapEdge(int fromRule, int toRule) {
		this.fromRule = fromRule;
		this.toRule = toRule;
	}

	public int getFromRule() {
		return fromRule;
	}

	public int getToRule() {
		return toRule;
	}

	public String toString() {
		return new String("from rule: " + (fromRule+1) + " toRule: " + (toRule+1));
//		return new String("from rule: " + fromRule + " toRule: " + toRule);
	}
}
