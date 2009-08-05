package com.plectix.simulator.stories.weakCompression.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;

public class Maps {

	private IWireStorage storage;

	public Maps(IWireStorage storage) {
		this.storage = storage;
	}

	public Map<Long, EMarkOfEvent> initMap() throws StoryStorageException {
		Map<Long, EMarkOfEvent> map = new LinkedHashMap<Long, EMarkOfEvent>();
		map.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
		return map;
	}

	public TreeMap<Long, EMarkOfEvent> getUnresolvedMap(
			TreeMap<Long, AtomicEvent<?>> wireMap,
			Map<Long, EMarkOfEvent> resolvedMap) {
		TreeMap<Long, EMarkOfEvent> unresolved = new TreeMap<Long, EMarkOfEvent>();
		for (Entry<Long, AtomicEvent<?>> entry : wireMap.entrySet()) {
			if (!resolvedMap.containsKey(entry.getKey())) {
				unresolved.put(entry.getKey(), EMarkOfEvent.UNRESOLVED);
			}
		}
		return unresolved;
	}

	public TreeMap<Long, EMarkOfEvent> getUnresolvedMap(Set<CEvent> events,
			Map<Long, EMarkOfEvent> map) {
		
		TreeMap<Long, EMarkOfEvent> unresolved = new TreeMap<Long, EMarkOfEvent>();
		
		for (CEvent event : events) {
			if (!map.containsKey(event.getStepId())) {
				unresolved.put(event.getStepId(), EMarkOfEvent.UNRESOLVED);
			}
		}
		return unresolved;
	}
	
	public TreeMap<Long, EMarkOfEvent> putNewEvents(
			TreeMap<Long, EMarkOfEvent> unresolved, List<EMarkOfEvent> marks,
			Long key) {
		TreeMap<Long, EMarkOfEvent> newMap = new TreeMap<Long, EMarkOfEvent>();
		for (EMarkOfEvent markOfEvent : marks) {
			newMap.put(key, markOfEvent);
			key = unresolved.higherKey(key);
		}
		return newMap;
	}
	public TreeMap<Long, EMarkOfEvent> putNewEvents(
			TreeMap<Long, EMarkOfEvent> unresolved, EMarkOfEvent[] marks,
			Long key) {
		
		TreeMap<Long, EMarkOfEvent> newMap = new TreeMap<Long, EMarkOfEvent>();
		newMap.putAll(unresolved);
		for (int i = 0; i < marks.length; i++) {
			newMap.put(key, marks[i]);
			key = newMap.higherKey(key);
		}
		return newMap;
	}


}
