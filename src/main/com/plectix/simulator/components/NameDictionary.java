package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameDictionary {
	private List<String> idToNamesList = new ArrayList<String>();
	private Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
	
	public final int addName(String name) {
		Integer id = namesToIdMap.get(name);
		if (id == null) {
			id = idToNamesList.size();
			idToNamesList.add(name);
			namesToIdMap.put(name, id);
		}
		return id;
	}
	
	public final String getName(int id) {
		return idToNamesList.get(id);
	}
}
