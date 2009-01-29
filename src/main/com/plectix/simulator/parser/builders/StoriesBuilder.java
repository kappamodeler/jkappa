package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.AbstractStories;

public class StoriesBuilder {
	public List<String> build(AbstractStories stories) {
		return stories.getStorifiedNames();
	}
}
