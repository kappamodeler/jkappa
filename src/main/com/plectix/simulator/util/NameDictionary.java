package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NameDictionary {
	private final List<String> idToNamesList = new ArrayList<String>();
	private final Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
	
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
