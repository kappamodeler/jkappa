package com.plectix.simulator.xmlmap;

import java.util.ArrayList;
import java.util.List;

public class Site {
	private String name;
	private boolean canChangeState;
	private boolean canBeBound;

	private List<Integer> rules;

	public Site(String name, boolean canChangeState, boolean canBeBound) {
		this.name = name;
		this.canChangeState = canChangeState;
		this.canBeBound = canBeBound;
		rules = new ArrayList<Integer>();
	}

	public void add(Integer ruleId) {
		rules.add(ruleId);
	}

	public boolean equals(Site site) {
		if (site.name.equals(this.name)
				&& (site.canChangeState == this.canChangeState))
			// &&
			// (site.canBeBound == this.canBeBound) &&
			// (isEqual(site.rules, this.rules)))
			return true;
		return false;
	}

	private boolean isEqual(List<Integer> r1, List<Integer> r2) {
		if (r1.size() != r2.size())
			return false;
		else {
			for (Integer integer : r2) {
				if (!r1.contains(integer))
					return false;
			}
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public boolean getCanChangeState() {
		return canChangeState;
	}

	public boolean getCanBeBound() {
		return canBeBound;
	}

	public List<Integer> getRules() {
		return rules;
	}

}
