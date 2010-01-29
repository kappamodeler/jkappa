package com.plectix.simulator.util;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.parser.EasyReader;
import com.plectix.simulator.parser.GeneralReader;

public class QuantityDataParser extends GeneralReader<Map<String, Integer>> {
	private EasyReader reader = getReader();

	public QuantityDataParser(String path) throws FileNotFoundException {
		super(path);
	}

	@Override
	protected Map<String, Integer> unsafeRead() throws NumberFormatException {
		Map<String, Integer> map = new TreeMap<String, Integer>();

		String line = reader.getLine();
		String currentName = "";
		Integer value = 0;

		while (line != null) {
			if (!"".equals(line)) {
				String[] data = line.split(" - ");
				currentName = data[0];
				value = Integer.parseInt(data[1]);

				map.put(currentName.intern(), value);
			}
			line = reader.getLine();
		}
		return map;
	}
}
