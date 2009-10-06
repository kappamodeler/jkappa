package com.plectix.simulator.stories.weakcompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.stories.weakcompression.util.Correctness;
import com.plectix.simulator.stories.weakcompression.util.Maps;
import com.plectix.simulator.stories.weakcompression.util.MarksGenerator;
import com.plectix.simulator.stories.weakcompression.util.StoragePrinter;

public class BruteForseByWires {
	private WireStorageInterface storage;
	private KappaSystem kappaSystem;
	private WireHashKey[] wires;

	private Correctness correctness;
	private StoragePrinter printer;
	private Maps maps;

	public BruteForseByWires(WireStorageInterface storage,
			KappaSystem kappaSystem) {
		this.storage = storage;
		this.kappaSystem = kappaSystem;
	}

	@Test
	public void bruteForse() throws StoryStorageException {
		printer = new StoragePrinter(storage, kappaSystem);
		printer.fillFirstMap();

		correctness = new Correctness(storage);

		maps = new Maps(storage);

		correctness.checkStorage(storage);
		// printer.printResult(null, true);
		buildWiresArray(storage.getStorageWires().entrySet());

		if (storage.getEvents().size() > 1)
			assertFalse(brute(maps.initMap(), 0));
		else
			assertTrue(storage.observableEvent().getMark().equals(
					MarkOfEvent.KEPT));
	}

	private void buildWiresArray(
			Set<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> entrySet) {
		int i = 0;
		wires = new WireHashKey[entrySet.size()];
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wireHashKey : entrySet) {
			wires[i++] = wireHashKey.getKey();
		}
	}

	private boolean brute(Map<Long, MarkOfEvent> map, int wNumber)
			throws StoryStorageException {
		if (wires.length == wNumber) {
			printer.printResult(map, true);
			correctness.checkAnotherWay(map);
			return true;
		}

		TreeMap<Long, AtomicEvent<?>> wireMap = storage.getStorageWires().get(
				wires[wNumber]);
		TreeMap<Long, MarkOfEvent> unresolved = maps.getUnresolvedMap(wireMap,
				map);
		if (!unresolved.isEmpty()) {
			List<List<MarkOfEvent>> listOfMarks = MarksGenerator
					.generateLists(unresolved.size());

			for (List<MarkOfEvent> marks : listOfMarks) {

				Long key = unresolved.firstKey();
				TreeMap<Long, MarkOfEvent> newMap = maps.putNewEvents(
						unresolved, marks, key);
				newMap.putAll(map);

				if (correctness.checkTheCorrectnessOnWire(newMap,
						wires[wNumber], wireMap)) {
					if (brute(newMap, wNumber + 1)) {
						return true;
					}
				}
			}
		} else {
			if (correctness.checkTheCorrectnessOnWire(map, wires[wNumber],
					wireMap)) {
				if (brute(map, wNumber + 1)) {
					return true;
				}
			}
		}

		return false;
	}

}
