package com.plectix.simulator.subviews.util;

import java.util.ArrayList;
import java.util.List;

public class Tag {

	private String data;
	private List<String> dataList = new ArrayList<String>();

	public Tag(String data) {
		this.data = data;
		parseTag();
	}

	public String getData() {
		return data;
	}

	public List<String> getDataList() {
		return dataList;
	}

	@Override
	public boolean equals(Object aTag) {

		if (this == aTag)
			return true;

		if (aTag == null)
			return false;

		if (getClass() != aTag.getClass())
			return false;

		Tag tag = (Tag) aTag;

		return equalsWithThis(tag);

	}

	private boolean equalsWithThis(Tag tag) {

		if (getDataList().size() != tag.getDataList().size())
			return false;

		for (String dataComponent : getDataList()) {
			if (!tag.getDataList().contains(dataComponent))
				return false;
		}

		return true;
	}

	private void parseTag() {

		String step1This[] = this.data.split(" ");

		if (step1This.length == 5) {

			dataList.add(step1This[1]);

			String step2This[] = step1This[4].split(",");

			if (step2This.length > 0) {

				for (String element : step2This) {

					dataList.add(element);

				}

			}

		}

	}

}
