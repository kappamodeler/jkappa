package com.plectix.simulator.util;

import java.util.*;

public class QuantityDataParser extends Parser<Map<String, Integer>> {
	private EasyFileReader myReader = getFileReader();

	public QuantityDataParser(String path) {
		super(path);
	}

	@Override
	protected Map<String, Integer> unsafeParse() throws NumberFormatException {
		Map<String, Integer> map = new TreeMap<String, Integer>();

		String line = myReader.getStringFromFile();
		String currentName = "";
		Integer value = 0;

		while (line != null) {
			if (!"".equals(line)) {
				String[] data = line.split(" - ");
				currentName = data[0];
				value = Integer.parseInt(data[1]);

				map.put(currentName.intern(), value);
			}
			line = myReader.getStringFromFile();
		}
		return Collections.unmodifiableMap(map);
	}
}
