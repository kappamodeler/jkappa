package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModelStories {
	private final List<String> storiesObjectsNames = new ArrayList<String>();
	
	public final void addName(String storyObjectName) {
		storiesObjectsNames.add(storyObjectName);
	}

	public final List<String> getStorifiedNames() {
		return storiesObjectsNames;
	}
	
	@Override
	public final String toString() {
		Collections.sort(storiesObjectsNames);
		StringBuffer sb = new StringBuffer();
		for (String name : storiesObjectsNames) {
			sb.append("%story: '" + name + "'\n");
		}
		return sb.toString();
	}
}
