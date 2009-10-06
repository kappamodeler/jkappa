package com.plectix.simulator.stories.weakcompression.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public class StoragePrinter {

	private WireStorageInterface storage;
	private KappaSystem kappaSystem;
	private TreeMap<Long, MarkOfEvent> first;

	public StoragePrinter(WireStorageInterface storage, KappaSystem kappaSystem) {
		this.storage = storage;
		this.kappaSystem = kappaSystem;
	}

	public void printWire(WireHashKey wireHashKey,
			TreeMap<Long, AtomicEvent<?>> value) {
		for (Entry<Long, AtomicEvent<?>> entry : value.entrySet()) {
			System.err.println(entry.getKey() + "\t ("
					+ entry.getValue().getState() + ")"
					+ entry.getValue().getType() + ":\t"
					+ entry.getValue().getContainer().getMark());
		}
	}

	public void printResult(Map<Long, MarkOfEvent> exMap, boolean b) {
		if (b) {
			if (exMap == null)
				System.out.println("BEGIN");
			System.out.println("STORAGE WAS:");

			for (Event event : storage.getEvents()) {
				System.out.println(event.getStepId()
						+ "\t"
						+ event.getRuleId()
						+ "\t"
						+ event.getMark()
						+ "\t"
						+ ((event.getRuleId() != -1) ? SimulationData.getData(
								kappaSystem.getRuleById(event.getRuleId()),
								true) : "initial event")
						+ "\t"
						+ ((event.getRuleId() != -1) ? kappaSystem.getRuleById(
								event.getRuleId()).getName() : "init"));
			}

			System.out.println("map WAS:");

			for (Entry<Long, MarkOfEvent> entry : first.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		}
		if (exMap != null) {
			System.out.println("map BECOME:");
			for (Entry<Long, MarkOfEvent> entry : exMap.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		} else
			System.out.println("ex map == NULL");

	}

	public void fillFirstMap() throws StoryStorageException {
		first = new TreeMap<Long, MarkOfEvent>();
		for (Event event : storage.getEvents()) {
			if (event.getMark() != null) {
				first.put(event.getStepId(), event.getMark());
			} else {
				first.put(event.getStepId(), MarkOfEvent.UNRESOLVED);
			}
		}

		first.put(storage.observableEvent().getStepId(), MarkOfEvent.KEPT);
	}

	public TreeMap<Long, MarkOfEvent> getFirst() {
		return first;
	}

}
