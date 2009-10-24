package com.plectix.simulator.subviews.util;

public class Set {

	private String agent;

	public Set(String agent) {
		this.agent = agent;
	}

	public String getAgent() {
		return agent;
	}

	@Override
	public boolean equals(Object aSet) {

		if (this == aSet)
			return true;

		if (aSet == null)
			return false;

		if (getClass() != aSet.getClass())
			return false;

		Set set = (Set) aSet;

		if (set.agent.equals(this.agent))
			return true;

		return false;
	}

}
