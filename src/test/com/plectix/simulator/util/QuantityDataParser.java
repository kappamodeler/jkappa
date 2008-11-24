package com.plectix.simulator.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuantityDataParser extends Parser<Map<String, Integer>> {
	private EasyFileReader myReader = getFileReader();

	public QuantityDataParser(String path) {
		super(path);
	}

	protected Map<String, Integer> unsafeParse() {
		Map<String, Integer> map = new HashMap<String, Integer>();

		String line = myReader.getStringFromFile();
		String currentName = "";
		Integer value = 0;

		while (line != null) {
			if (!"".equals(line)) {
				String[] data = line.split(" - ");
				currentName = data[0];
				try {
					value = Integer.parseInt(data[1]);
				} catch (NumberFormatException e) {
					System.err.println(data[1] + " is not an integer!");
				} catch (Exception ne) {
					System.out.println(ne.getMessage() + " at " + line);
				}

				map.put(currentName.intern(), value);
			}
			line = myReader.getStringFromFile();
		}
		return Collections.unmodifiableMap(map);
	}
}
