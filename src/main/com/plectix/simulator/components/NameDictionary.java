package com.plectix.simulator.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NameDictionary {
	private Map<Integer, String> idToNamesMap = new HashMap<Integer, String>();
	private Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
	private Random randomIdGenerator = new Random();
	
	private int generateUniqueId() {
		Integer id;
		while(idToNamesMap.get((id = randomIdGenerator.nextInt())) != null);
		return id;
	}

	public int addName(String name) {
		Integer id = namesToIdMap.get(name);
		if(id != null) {
			return id;
		}
		id = generateUniqueId();
		idToNamesMap.put(id, name);
		namesToIdMap.put(name, id);
		return id;
	}
	
	public String getName(Integer id) {
		return idToNamesMap.get(id);
	}
}
