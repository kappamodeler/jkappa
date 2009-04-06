package com.plectix.simulator.XMLmaps;

import java.util.ArrayList;
import java.util.List;


public class Agent {
	
	private String name;
	private Sites sites;
	private List<Integer> agentRules;

	
	public String getName(){
		return name;
	}
	
	public Agent(String name){
		this.name = name;
		sites = new Sites();
		
		agentRules = new ArrayList<Integer>();
	}

	Boolean equals(Agent agent){
		if ((agent.name.equals(this.name))&&
				(agent.sites.equals(this.sites))
//				&&(isEqual(agent.agentRules, this.agentRules))
				)
			return true;
		return false;
	}
	
	private boolean isEqual(List<Integer> list1, List<Integer> list2) {
		if (list1.size()!=list2.size())
			return false;
		for (Integer integer : list2) {
			if (!list1.contains(integer))
				return false;
		}
		return true;
	}

	public void addSite(Site site) {
		sites.add(site);
	}

	public void addRuleId(Integer id) {
		agentRules.add(id);
	}
	
	
	public List<Integer> getAgentRules(){
		return agentRules;
	}

	public Site getLastSite() {
		return sites.getLastSite();
	}
	
	public Sites getSites() {
		return sites;
	}
	
	
	class Sites{
		private List<Site> list;
		
		public Sites() {
			list = new ArrayList<Site>();
		}
		
		public List<Site> getList() {
			return list;
		}

		public Site getLastSite() {
			return list.get(list.size() - 1);
		}

		public void add(Site site) {
			list.add(site);
		}
		
		public boolean equals(Sites sites) {
			if (sites.list.size() != this.list.size())
				return false;
			else{
				for (Site site : this.list) {
					if (!sites.contains(site))
						return false;
				}
			}
			return true;
		}
		
		
		private boolean contains(Site site) {
			for (Site s : this.list) {
				if (s.equals(site))
					return true;
			}
			return false;
		}

	}


	
	

}
