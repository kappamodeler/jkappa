package com.plectix.simulator.staticanalysis.stories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.staticanalysis.stories.compressions.Compressor;
import com.plectix.simulator.staticanalysis.stories.storage.AbstractStorage;
import com.plectix.simulator.staticanalysis.stories.storage.Event;
import com.plectix.simulator.staticanalysis.stories.storage.StoriesAgentTypesStorage;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

public final class Stories {

	private final List<Integer> observablesRulesIds;
	private final Map<Integer, WireStorageInterface> eventsMapForCurrentStory;
	private StoriesAgentTypesStorage storiesAgentTypesStorage;
	
	// may be non-compression, weak compression, strong compression
	private StoryCompressionMode storifyMode;

	public Stories(SimulationData simData) {
		this.observablesRulesIds = new ArrayList<Integer>();
		this.eventsMapForCurrentStory = new LinkedHashMap<Integer, WireStorageInterface>();
		storiesAgentTypesStorage = new StoriesAgentTypesStorage();
		storifyMode = simData.getSimulationArguments()
		.getStorifyMode();
		for (int i = 0; i < simData.getSimulationArguments().getIterations(); i++) {
			this.eventsMapForCurrentStory.put(new Integer(i),
					new AbstractStorage(i, storiesAgentTypesStorage));
		}
	}

	public final int getRuleIdAtStories(int index) {
		if (index >= observablesRulesIds.size() || index < 0)
			return -1;
		return observablesRulesIds.get(index);
	}

	/**
	 * param index ; root of cleaning
	 */
	public final void cleaningStory(int index) throws StoryStorageException {
		if (eventsMapForCurrentStory.get(index).isImportantStory()) {
			compress(eventsMapForCurrentStory.get(index));
		} else {
			// delete this story
			eventsMapForCurrentStory.get(index).clearList();
			storiesAgentTypesStorage
					.resetTypesOfAgents(eventsMapForCurrentStory.get(index)
							.getIteration());
		}
	}

	private void compress(WireStorageInterface wireStorageInterface) throws StoryStorageException {
		if (wireStorageInterface.observableEvent() != null && wireStorageInterface.initialEvent() != null) {
			Compressor compressor = new Compressor(wireStorageInterface);
			compressor.execute(storifyMode);

		}
	}

	public final void addToStories(List<Integer> ruleIDs) {
			observablesRulesIds.addAll(ruleIDs);
	}

	public final void addEventToStory(int index, Event eventContainer)
			throws StoryStorageException {
		eventsMapForCurrentStory.get(index).addEventContainer(
				eventContainer);
	}

	public final void addLastEventToStoryStorifyRule(int index,
			Event eventContainer, double currentTime)
			throws StoryStorageException {
		this.eventsMapForCurrentStory.get(Integer.valueOf(index))
				.addLastEventContainer(eventContainer, currentTime);
	}

	public final boolean checkRule(int ruleToBeChecked, int index) {
		return observablesRulesIds.contains(ruleToBeChecked);
	}

	public final Map<Integer, WireStorageInterface> getEventsMapForCurrentStory() {
		return eventsMapForCurrentStory;
	}

	public final StoriesAgentTypesStorage getStoriesAgentTypesStorage() {
		return storiesAgentTypesStorage;
	}
}
