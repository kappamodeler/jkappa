package com.plectix.simulator.components;

public class RuleIDs {
	int ruleID;
	int indexInTrace;
	public int getRuleID() {
		return ruleID;
	}

	public int getIndexInTrace() {
		return indexInTrace;
	}

	public int getLevel() {
		return level;
	}

	int level;
	
	public RuleIDs(int ruleID, int indexInTrace, int level) {
		this.ruleID = ruleID;
		this.level = level;
		this.indexInTrace = indexInTrace;
	}

	public final boolean equals(Object obj) {
		if (!(obj instanceof RuleIDs))
			return false;
		RuleIDs ruleID = (RuleIDs) obj;
		if (indexInTrace != ruleID.indexInTrace)
			return false;
		return true;
	}
}
