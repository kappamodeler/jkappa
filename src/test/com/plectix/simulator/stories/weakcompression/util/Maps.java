package com.plectix.simulator.stories.weakcompression.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;

public class Maps {

	private WireStorageInterface storage;

	public Maps(WireStorageInterface storage) {
		this.storage = storage;
	}

	public Map<Long, MarkOfEvent> initMap() throws StoryStorageException {
		Map<Long, MarkOfEvent> map = new LinkedHashMap<Long, MarkOfEvent>();
		map.put(storage.observableEvent().getStepId(), MarkOfEvent.KEPT);
		return map;
	}

	public TreeMap<Long, MarkOfEvent> getUnresolvedMap(
			TreeMap<Long, AtomicEvent<?>> wireMap,
			Map<Long, MarkOfEvent> resolvedMap) {
		TreeMap<Long, MarkOfEvent> unresolved = new TreeMap<Long, MarkOfEvent>();
		for (Entry<Long, AtomicEvent<?>> entry : wireMap.entrySet()) {
			if (!resolvedMap.containsKey(entry.getKey())) {
				unresolved.put(entry.getKey(), MarkOfEvent.UNRESOLVED);
			}
		}
		return unresolved;
	}

	public TreeMap<Long, MarkOfEvent> getUnresolvedMap(Set<Event> events,
			Map<Long, MarkOfEvent> map) {

		TreeMap<Long, MarkOfEvent> unresolved = new TreeMap<Long, MarkOfEvent>();

		for (Event event : events) {
			if (!map.containsKey(event.getStepId())) {
				unresolved.put(event.getStepId(), MarkOfEvent.UNRESOLVED);
			}
		}
		return unresolved;
	}

	public TreeMap<Long, MarkOfEvent> putNewEvents(
			TreeMap<Long, MarkOfEvent> unresolved, List<MarkOfEvent> marks,
			Long key) {
		TreeMap<Long, MarkOfEvent> newMap = new TreeMap<Long, MarkOfEvent>();
		for (MarkOfEvent markOfEvent : marks) {
			newMap.put(key, markOfEvent);
			key = unresolved.higherKey(key);
		}
		return newMap;
	}

	public TreeMap<Long, MarkOfEvent> putNewEvents(
			TreeMap<Long, MarkOfEvent> unresolved, MarkOfEvent[] marks, Long key) {

		TreeMap<Long, MarkOfEvent> newMap = new TreeMap<Long, MarkOfEvent>();
		newMap.putAll(unresolved);
		for (int i = 0; i < marks.length; i++) {
			newMap.put(key, marks[i]);
			key = newMap.higherKey(key);
		}
		return newMap;
	}

}
