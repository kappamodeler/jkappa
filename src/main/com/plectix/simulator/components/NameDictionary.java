package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.Map;

public class NameDictionary {
	private Map<Integer, String> idToNamesMap = new HashMap<Integer, String>();
	private Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
	private int nextId = 0;
	
	public final int addName(String name) {
		Integer id = namesToIdMap.get(name);
		if(id != null) {
			return id;
		}
		idToNamesMap.put(nextId, name);
		namesToIdMap.put(name, nextId);
		
		return nextId++;
	}
	
	public final String getName(Integer id) {
		return idToNamesMap.get(id);
	}
}
