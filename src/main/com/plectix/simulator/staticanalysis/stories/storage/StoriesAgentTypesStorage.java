package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class StoriesAgentTypesStorage {
	private final Map<Integer, Map<Long, String>> typeOfAgentByIdMap 
		= new LinkedHashMap<Integer, Map<Long, String>>();;
	private int iteration;
	
	public StoriesAgentTypesStorage() {
	}
	
	public final void setTypeOfAgent(long id, String type){
		Map<Long, String> typeOfAgentById = typeOfAgentByIdMap.get(iteration);
		if(typeOfAgentById == null){
			typeOfAgentById = new TreeMap<Long, String>();
			typeOfAgentByIdMap.put(iteration, typeOfAgentById);
		}
		typeOfAgentById.put(id, type);
	}
	
	public final String getType(int iteration, long id){
		return typeOfAgentByIdMap.get(iteration).get(id);
	}
	
	public final void resetTypesOfAgents(int index){
		Map<Long, String> typeOfAgentById = typeOfAgentByIdMap.get(index);
		if(typeOfAgentById != null)
			typeOfAgentById.clear();
	}

	public final void setIteration(int iteration){
		this.iteration = iteration;
	}

	public final void update(int index, LinkedHashMap<Long, String> typeById) {
		typeOfAgentByIdMap.remove(index);
		typeOfAgentByIdMap.put(index, typeById);
	}
}
