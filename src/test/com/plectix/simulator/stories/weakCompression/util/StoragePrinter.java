package com.plectix.simulator.stories.weakCompression.util;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;

public class StoragePrinter {
	
	
	private IWireStorage storage;
	private KappaSystem kappaSystem;
	private TreeMap<Long, EMarkOfEvent> first;

	public StoragePrinter(IWireStorage storage, KappaSystem kappaSystem) {
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
	
	public void printResult(Map<Long, EMarkOfEvent> exMap, boolean b) {
		if (b) {
			if (exMap == null)
				System.out.println("BEGIN");
			System.out.println("STORAGE WAS:");

			for (CEvent event : storage.getEvents()) {
				System.out.println(event.getStepId()
						+ "\t"
						+ event.getRuleId()
						+ "\t"
						+ event.getMark()
						+ "\t"
						+ ((event.getRuleId() != -1) ? SimulationData.getData(
								kappaSystem.getRuleByID(event.getRuleId()),
								true) : "initial event")
						+ "\t"
						+ ((event.getRuleId() != -1) ? kappaSystem.getRuleByID(
								event.getRuleId()).getName() : "init"));
			}

			System.out.println("map WAS:");

			for (Entry<Long, EMarkOfEvent> entry : first.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		}
		if (exMap != null) {
			System.out.println("map BECOME:");
			for (Entry<Long, EMarkOfEvent> entry : exMap.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
		} else
			System.out.println("ex map == NULL");

	}
	
	public void fillFirstMap() throws StoryStorageException {
		first = new TreeMap<Long, EMarkOfEvent>();
		for (CEvent event : storage.getEvents()) {
			if (event.getMark() != null) {
				first.put(event.getStepId(), event.getMark());
			} else {
				first.put(event.getStepId(), EMarkOfEvent.UNRESOLVED);
			}
		}

		first.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);
	}
	
	
	
	public TreeMap<Long, EMarkOfEvent> getFirst() {
		return first;
	}

}
