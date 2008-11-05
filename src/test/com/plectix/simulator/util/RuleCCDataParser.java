package com.plectix.simulator.util;

import java.util.*;

public class RuleCCDataParser extends Parser<Map<String, RuleStructure>> {
	private EasyFileReader myReader = getFileReader();

	public RuleCCDataParser(String path) {
		super(path);
	}

	private void fillEmpty(List<String> list) {
		if (list.isEmpty()) {
			list.add("");
		}
	}
	
	protected Map<String, RuleStructure> unsafeParse() {
		Map<String, RuleStructure> map = new HashMap<String, RuleStructure>();

		String line = myReader.getStringFromFile();
		String currentTest = "";
		List<String> leftCCs = new ArrayList<String>();
		List<String> rightCCs = new ArrayList<String>();
		List<String> currentList = leftCCs;
		
		while (line != null) {
			if (!"".equals(line)) {
				if (line.startsWith("#")) {
					if (!"".equals(currentTest)) {
						fillEmpty(leftCCs);
						fillEmpty(rightCCs);
						map.put(currentTest, new RuleStructure(leftCCs, rightCCs));
						leftCCs = new ArrayList<String>();
						rightCCs = new ArrayList<String>();
					}
					currentList = leftCCs;
					currentTest = line.substring(1);
				} else if (line.startsWith("----")) {
					currentList = rightCCs;
				} else {
					currentList.add(line);
				}
			}
			line = myReader.getStringFromFile();
		}
		fillEmpty(leftCCs);
		fillEmpty(rightCCs);
		map.put(currentTest, new RuleStructure(leftCCs, rightCCs));
		return Collections.unmodifiableMap(map);
	}
}