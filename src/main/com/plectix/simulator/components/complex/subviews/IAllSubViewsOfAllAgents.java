package com.plectix.simulator.components.complex.subviews;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.interfaces.ISolution;

public interface IAllSubViewsOfAllAgents {
	
	public void build(ISolution solution, List<CRule> rules);
		
	public Iterator<Integer> getAllTypesIdOfAgents();
	
	public List<String> getAllTypesOfAgents();
	
	public List<CSubViews> getAllSubViewsByTypeId(int type);
	
	public List<CSubViews> getAllSubViewsByType(String type);
	
	public CSubViews getSubViewForRule(String typeOfAgent, CRule rule);
	
}