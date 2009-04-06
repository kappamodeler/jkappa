package com.plectix.simulator.XMLmaps;

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

	public boolean equals(Bond c) {
		if (((c.FromAgent.equals(this.FromAgent))
				&& (c.FromSite.equals(this.FromSite))
				&& (c.ToAgent.equals(this.ToAgent))
				&& (c.ToSite.equals(this.ToSite)))
			||((c.FromAgent.equals(this.ToAgent))
						&& (c.FromSite.equals(this.ToSite))
						&& (c.ToAgent.equals(this.FromAgent))
						&& (c.ToSite.equals(this.FromSite))))
			return true;
		return false;
	}
}
