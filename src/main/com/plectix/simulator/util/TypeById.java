package com.plectix.simulator.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class TypeById {
	private Map<Integer,Map<Long,Integer>> typeOfAgentByIdMap;
	private int iteration;
	
	public TypeById() {
		typeOfAgentByIdMap = new LinkedHashMap<Integer, Map<Long,Integer>>();
	}
	
	public void setTypeOfAgent(Long id, Integer type){
		Map<Long, Integer> typeOfAgentById = typeOfAgentByIdMap.get(iteration);
		if(typeOfAgentById == null){
			typeOfAgentById = new TreeMap<Long, Integer>();
			typeOfAgentByIdMap.put(iteration, typeOfAgentById);
		}
		typeOfAgentById.put(id, type);
	}
	
	public Integer getType(Integer iteration, Long id){
		return typeOfAgentByIdMap.get(iteration).get(id);
	}
	
	public void resetTypesOfAgents(int i){
		Map<Long,Integer> typeOfAgentById = typeOfAgentByIdMap.get(i);
		if(typeOfAgentById != null)
			typeOfAgentByIdMap.get(i).clear();
	}

	public void setIteration(int iteration){
		this.iteration = iteration;
	}

	public void update(int i, LinkedHashMap<Long, Integer> typeById) {
		typeOfAgentByIdMap.remove(i);
		typeOfAgentByIdMap.put(i, typeById);
	}

}
