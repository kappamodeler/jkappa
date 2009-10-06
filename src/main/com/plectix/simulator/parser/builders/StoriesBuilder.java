package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.parser.abstractmodel.ModelStories;

public final class StoriesBuilder {
	public final List<String> build(ModelStories stories) {
		return stories.getStorifiedNames();
	}
}
