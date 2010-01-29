package com.plectix.simulator.util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.parser.EasyReader;
import com.plectix.simulator.parser.GeneralReader;

public class RuleCCDataParser extends GeneralReader<Map<String, RuleStructure>> {
	private EasyReader reader = getReader();

	public RuleCCDataParser(String path) throws FileNotFoundException {
		super(path);
	}

	private void fillEmpty(List<String> list) {
		if (list.isEmpty()) {
			list.add("");
		}
	}

	@Override
	protected Map<String, RuleStructure> unsafeRead() {
		Map<String, RuleStructure> map = new LinkedHashMap<String, RuleStructure>();

		String line = reader.getLine();
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
						map.put(currentTest, new RuleStructure(leftCCs,
								rightCCs));
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
			line = reader.getLine();
		}
		fillEmpty(leftCCs);
		fillEmpty(rightCCs);
		map.put(currentTest, new RuleStructure(leftCCs, rightCCs));
		return map;
	}
}
