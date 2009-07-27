package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NameDictionary {
	private final List<String> idToNamesList = new ArrayList<String>();
	private final Map<String, Integer> namesToIdMap = new LinkedHashMap<String, Integer>();
	
	public final int addName(String name) {
		Integer id = namesToIdMap.get(name);
		if (id == null) {
			id = idToNamesList.size();
			idToNamesList.add(name);
			namesToIdMap.put(name, id);
		}
		return id;
	}
	
	/**
	 * Name dictionary.
	 * @param id - id of name
	 * @return Strting by id
	 */
	public final String getName(int id) {
		return idToNamesList.get(id);
	}

	public int getId(String argument) {
		return namesToIdMap.get(argument);
	}
}
