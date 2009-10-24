package com.plectix.simulator.xmlmap;

import java.util.ArrayList;
import java.util.List;

public class Bond {

	private String FromAgent;
	private String FromSite;
	private String ToAgent;
	private String ToSite;

	private List<Integer> rules;

	public Bond(String FromAgent, String FromSite, String ToAgent, String ToSite) {
		this.FromAgent = FromAgent;
		this.FromSite = FromSite;
		this.ToAgent = ToAgent;
		this.ToSite = ToSite;

		rules = new ArrayList<Integer>();
	}

	public void addRuleId(Integer id) {
		rules.add(id);
	}

	public String getFromAgent() {
		return FromAgent;
	}

	public String getFromSite() {
		return FromSite;
	}

	public String getToAgent() {
		return ToAgent;
	}

	public String getToSite() {
		return ToSite;
	}

	@Override
	public boolean equals(Object aBond) {

		if (this == aBond)
			return true;

		if (aBond == null)
			return false;

		if (getClass() != aBond.getClass())
			return false;

		Bond bond = (Bond) aBond;

		if (((bond.FromAgent.equals(this.FromAgent))
				&& (bond.FromSite.equals(this.FromSite))
				&& (bond.ToAgent.equals(this.ToAgent)) && (bond.ToSite
				.equals(this.ToSite)))
				|| ((bond.FromAgent.equals(this.ToAgent))
						&& (bond.FromSite.equals(this.ToSite))
						&& (bond.ToAgent.equals(this.FromAgent)) && (bond.ToSite
						.equals(this.FromSite))))
			return true;

		return false;
	}
}
