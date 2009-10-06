package com.plectix.simulator.stories.weakcompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.stories.weakcompression.util.Correctness;
import com.plectix.simulator.stories.weakcompression.util.GreyCode;
import com.plectix.simulator.stories.weakcompression.util.Maps;
import com.plectix.simulator.stories.weakcompression.util.StoragePrinter;

public class BruteForseWithGreyCode {
	private WireStorageInterface storage;
	private KappaSystem kappaSystem;
	private StoragePrinter printer;
	private Correctness correctness;
	private Maps maps;

	public BruteForseWithGreyCode(WireStorageInterface storage,
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
		assertFalse(veryBrute(maps.initMap()));
	}

	private boolean veryBrute(Map<Long, MarkOfEvent> map)
			throws StoryStorageException {
		TreeMap<Long, MarkOfEvent> unresolvedMap = maps.getUnresolvedMap(
				storage.getEvents(), map);

		if (!unresolvedMap.isEmpty()) {

			for (MarkOfEvent[] marks : GreyCode.generateGreyLists(unresolvedMap
					.size())) {
				Long key = unresolvedMap.firstKey();
				TreeMap<Long, MarkOfEvent> newMap = maps.putNewEvents(
						unresolvedMap, marks, key);
				newMap.putAll(map);

				if (correctness.checkMap(newMap)) {
					System.out.println("------------------");
					System.out.println("OH MY GOD!!!");
					printer.printResult(newMap, true);
					correctness.checkAnotherWay(newMap);
					return true;
				}
				newMap = null;
			}
		} else {
			assertTrue("a map with unresolved events is empty", false);
		}
		return false;
	}

}
