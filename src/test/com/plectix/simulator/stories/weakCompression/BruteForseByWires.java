package com.plectix.simulator.stories.weakCompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.stories.weakCompression.util.Correctness;
import com.plectix.simulator.stories.weakCompression.util.Maps;
import com.plectix.simulator.stories.weakCompression.util.MarksGenerator;
import com.plectix.simulator.stories.weakCompression.util.StoragePrinter;

public class BruteForseByWires {
	private IWireStorage storage;
	private KappaSystem kappaSystem;
	private WireHashKey[] wires;
	
	private Correctness correctness;
	private StoragePrinter printer;
	private Maps maps;

	
	public BruteForseByWires(IWireStorage storage, KappaSystem kappaSystem) {
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
//		printer.printResult(null, true);
		buildWiresArray(storage.getStorageWires().entrySet());

		if (storage.getEvents().size() > 1)
			assertFalse(brute(maps.initMap(), 0));
		else
			assertTrue(storage.observableEvent().getMark().equals(
					EMarkOfEvent.KEPT));
	}





	private void buildWiresArray(
			Set<Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>>> entrySet) {
		int i = 0;
		wires = new WireHashKey[entrySet.size()];
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wireHashKey : entrySet) {
			wires[i++] = wireHashKey.getKey();
		}
	}


	private boolean brute(Map<Long, EMarkOfEvent> map, int wNumber)
			throws StoryStorageException {
		if (wires.length == wNumber) {
			printer.printResult(map, true);
			correctness.checkAnotherWay(map);
			return true;
		}

		TreeMap<Long, AtomicEvent<?>> wireMap = storage.getStorageWires().get(
				wires[wNumber]);
		TreeMap<Long, EMarkOfEvent> unresolved = maps.getUnresolvedMap(wireMap, map);
		if (!unresolved.isEmpty()) {
			List<List<EMarkOfEvent>> listOfMarks = MarksGenerator
					.generateLists(unresolved.size());

			for (List<EMarkOfEvent> marks : listOfMarks) {

				Long key = unresolved.firstKey();
				TreeMap<Long, EMarkOfEvent> newMap = maps.putNewEvents(unresolved,
						marks, key);
				newMap.putAll(map);

				if (correctness.checkTheCorrectnessOnWire(newMap, wires[wNumber], wireMap)) {
					if (brute(newMap, wNumber + 1)) {
						return true;
					}
				}
			}
		} else {
			if (correctness.checkTheCorrectnessOnWire(map, wires[wNumber], wireMap)) {
				if (brute(map, wNumber + 1)) {
					return true;
				}
			}
		}

		return false;
	}


	
	

	

	
}
