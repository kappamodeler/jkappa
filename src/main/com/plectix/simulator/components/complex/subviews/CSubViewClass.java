package com.plectix.simulator.components.complex.subviews;

import java.util.HashSet;
import java.util.Set;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CSubViewClass {
	private int agentTypeId;
	private Set<Integer> sitesId;
	private Set<Integer> rulesId;

	public CSubViewClass(int agentTypeId, int siteId) {
		this.agentTypeId = agentTypeId;
		sitesId = new HashSet<Integer>();
		rulesId = new HashSet<Integer>();
		sitesId.add(Integer.valueOf(siteId));
	}

	public int getAgentTypeId() {
		return agentTypeId;
	}

	public Set<Integer> getSitesId() {
		return sitesId;
	}
	
	public Set<Integer> getRulesId(){
		return rulesId;
	}
	
	
	public void addSite(int site){
		sitesId.add(site);
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof CSubViewClass))
			return false;
		CSubViewClass inClass = (CSubViewClass)obj;
		
		if(agentTypeId != inClass.agentTypeId)
			return false;
		if(!sitesId.equals(inClass.sitesId))
			return false;
		return true;
	}
	
	public int hashCode() {
		return sitesId.hashCode();
	}
	
	public boolean isHaveSite(int siteId){
		return sitesId.contains(siteId);
	}
	
	public void addRuleId(int rule){
		rulesId.add(rule);
	}
	
	public boolean isHaveRule(CRule rule){
		return rulesId.contains(rule.getRuleID());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(ThreadLocalData.getNameDictionary().getName(agentTypeId));
		sb.append(" ");
		for(Integer id : sitesId)
			sb.append(ThreadLocalData.getNameDictionary().getName(id)+ " ");
		sb.append(" Rules:'");
		for(Integer id : rulesId)
			sb.append(id + " ");
		sb.append("'");
		return sb.toString();
	}

	public void addRulesId(CSubViewClass removedClass) {
		for (Integer ruleId : removedClass.getRulesId())
			this.addRuleId(ruleId);
	}
}
