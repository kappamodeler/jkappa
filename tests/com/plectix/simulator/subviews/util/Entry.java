package com.plectix.simulator.subviews.util;

import java.util.ArrayList;
import java.util.List;

public class Entry {

	private String data;
	private List<String> dataList = new ArrayList<String>();

	public Entry(String data) {
		this.data = data;
		parseEntry();
	}

	public String getData() {
		return data;
	}

	public List<String> getDataList() {
		return dataList;
	}

	@Override
	public boolean equals(Object aEntry) {

		if (this == aEntry)
			return true;

		if (aEntry == null)
			return false;

		if (getClass() != aEntry.getClass())
			return false;

		Entry entry = (Entry) aEntry;

		return equalsWithThis(entry);

	}

	private boolean equalsWithThis(Entry entry) {

		if (getDataList().size() != entry.getDataList().size())
			return false;

		for (String dataComponent : getDataList()) {
			if (!entry.getDataList().contains(dataComponent))
				return false;
		}

		return true;
	}

	private void parseEntry() {

		String step1This[] = this.data.split("[(]");

		if (step1This.length == 2) {

			dataList.add(step1This[0]);

			String step2This[] = step1This[1].split("[)]");

			if (step2This.length == 1) {

				String step3This[] = step2This[0].split(",");

				if (step3This.length > 0) {

					for (String element : step3This) {

						dataList.add(element);

					}

				}

			}

		}

	}

}
