package com.plectix.simulator.components.complex.subviews;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.graphs.Vertex;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CSubViewClass extends Vertex{
	private int agentTypeId;
	private ArrayList<Integer> sitesId;
	private ArrayList<Integer> rulesId;

	public CSubViewClass(int agentTypeId) {
		this.agentTypeId = agentTypeId;
		sitesId = new ArrayList<Integer>();
		rulesId = new ArrayList<Integer>();
	}

	public int getAgentTypeId() {
		return agentTypeId;
	}

	public ArrayList<Integer> getSitesId() {
		return sitesId;
	}
	
	public ArrayList<Integer> getRulesId(){
		return rulesId;
	}
	
	
	public void addSite(int siteId){
		sitesId.add(siteId);
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

	public void addRuleId(ArrayList<Integer> rulesId2) {
		rulesId.addAll(rulesId2);
		
	}
}
