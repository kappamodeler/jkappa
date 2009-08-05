package com.plectix.simulator.stories.weakCompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;

import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.stories.StoryCorrectness;
import com.plectix.simulator.stories.weakCompression.util.Correctness;
import com.plectix.simulator.stories.weakCompression.util.Maps;
import com.plectix.simulator.stories.weakCompression.util.MarksGenerator;
import com.plectix.simulator.stories.weakCompression.util.StoragePrinter;

public class BruteForseWithMarksGenerator {
	private IWireStorage storage;
	private TreeMap<Long, EMarkOfEvent> first;
	private KappaSystem kappaSystem;
	private StoragePrinter printer;
	private Correctness correctness;
	private Maps maps;

	public BruteForseWithMarksGenerator(IWireStorage storage,
			KappaSystem kappaSystem) {
		this.storage = storage;
		this.kappaSystem = kappaSystem;
	}

	@Test
	public void bruteForse() throws StoryStorageException {
		
		printer = new StoragePrinter(storage, kappaSystem);
		printer.fillFirstMap();
		first = printer.getFirst();
		
		maps = new Maps(storage);
		
		correctness = new Correctness(storage);
		correctness.checkStorage(storage);
		
		printer.printResult(null, true);
		assertFalse(veryBrute(maps.initMap()));
	}

	private boolean veryBrute(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		TreeMap<Long, EMarkOfEvent> unresolvedMap = maps.getUnresolvedMap(storage.getEvents(), map);
		
		
		if (!unresolvedMap.isEmpty()) {
			
			for (List<EMarkOfEvent> marks : MarksGenerator.generateLists(unresolvedMap.size())) {
				
				Long key = unresolvedMap.firstKey();
				TreeMap<Long, EMarkOfEvent> newMap = maps.putNewEvents(unresolvedMap,
						marks, key);
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
