package com.plectix.simulator.stories.weakCompression;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

public class WeakCompressionVeryBruteForse {
	private IWireStorage storage;
	private TreeMap<Long, EMarkOfEvent> first;
	private KappaSystem kappaSystem;

	public WeakCompressionVeryBruteForse(IWireStorage storage,
			KappaSystem kappaSystem) {
		this.storage = storage;
		this.kappaSystem = kappaSystem;
	}

	@Test
	public void bruteForse() throws StoryStorageException {
		Map<Long, EMarkOfEvent> eventMarks = new TreeMap<Long, EMarkOfEvent>();
		Map<Long, EMarkOfEvent> checkingMap = new TreeMap<Long, EMarkOfEvent>();
		first = new TreeMap<Long, EMarkOfEvent>();

		for (CEvent event : storage.getEvents()) {
			if (event.getMark() != null) {
				switch (event.getMark()) {
				case DELETED: {
					first.put(event.getStepId(), EMarkOfEvent.DELETED);
					eventMarks.put(event.getStepId(), EMarkOfEvent.DELETED);
					checkingMap.put(event.getStepId(), EMarkOfEvent.DELETED);
					break;
				}
				case UNRESOLVED: {
					first.put(event.getStepId(), EMarkOfEvent.UNRESOLVED);
					checkingMap.put(event.getStepId(), EMarkOfEvent.DELETED);
					eventMarks.put(event.getStepId(), EMarkOfEvent.DELETED);
					break;
				}
				case KEPT: {
					first.put(event.getStepId(), EMarkOfEvent.KEPT);
					checkingMap.put(event.getStepId(), EMarkOfEvent.KEPT);
					break;
				}

				default:
					break;
				}
			} else {
				first.put(event.getStepId(), EMarkOfEvent.UNRESOLVED);
				checkingMap.put(event.getStepId(), EMarkOfEvent.UNRESOLVED);
			}
		}

		assertTrue(checkMap(checkingMap));
		eventMarks
				.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);

		first.put(storage.observableEvent().getStepId(), EMarkOfEvent.KEPT);

//		printResult(null, true);
		assertFalse(veryBrute(eventMarks));
	}

	private boolean veryBrute(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		CompressionPassport passport = storage.extractPassport();
		TreeMap<Long, EMarkOfEvent> unresolvedMap = new TreeMap<Long, EMarkOfEvent>();
		ICEvent tmpEvent = null;
		for (IEventIterator iterator = passport.eventIterator(true); iterator
				.hasNext();) {
			Long eventId = (Long) iterator.next();
			tmpEvent = passport.extractGraph().getEventByStepId(eventId);
			if (!map.containsKey(eventId)) {
				unresolvedMap.put(eventId, EMarkOfEvent.UNRESOLVED);
			}
		}
		if (!unresolvedMap.isEmpty()) {
			TreeMap<Long, EMarkOfEvent> tmpMap;

			for (EMarkOfEvent[] marks : GreyCode.generateGreyLists(unresolvedMap.size())) {
			
//			for (List<EMarkOfEvent> marks : generateLists(unresolvedMap.size())) {
				tmpMap = new TreeMap<Long, EMarkOfEvent>();
				tmpMap.putAll(unresolvedMap);
				Long tmp = tmpMap.firstKey();
//				for (Iterator iterator = marks.iterator(); iterator
//						.hasNext();) {
//					EMarkOfEvent markOfEvent = (EMarkOfEvent) iterator
//							.next();
//					tmpMap.put(tmp, markOfEvent);
//					tmp = tmpMap.higherKey(tmp);
//					
//				}
				for (int i = 0; i < marks.length; i++) {
					tmpMap.put(tmp, marks[i]);
					tmp = tmpMap.higherKey(tmp);
					
				}
				tmpMap.putAll(map);
				if (checkMap(tmpMap)) {
					System.out.println("------------------");
					System.out.println("OH MY GOD!!!");
					printResult(tmpMap, true);
					checkAnotherWay(tmpMap);
					return true;
				}
				tmpMap = null;
			}
		} else {
			assertTrue("a map with unresolved events is empty", false);
		}
		return false;
	}

	private void checkAnotherWay(TreeMap<Long, EMarkOfEvent> marks)
			throws StoryStorageException {
		for (Entry<Long, EMarkOfEvent> entry : marks.entrySet()) {
			for(CEvent cevent: storage.getEvents()){
				if(cevent.getStepId() == entry.getKey() && !cevent.getMark().equals(entry.getValue()))
					cevent.setMark(entry.getValue(), storage);
			}
		}
		CompressionPassport passport = storage.extractPassport();
		passport.removeEventWithMarkDelete();
		StoryCorrectness.testOfStates(passport.getStorage());
	}

	private boolean checkMap(Map<Long, EMarkOfEvent> map)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (!wire.getValue().isEmpty())
				if (!checkTheCorrectnessOnWire(map, wire)) {
					// System.err.println("result: error on wire: "
					// + wire.getKey());
					// printWire(wire.getKey(),wire.getValue());
					return false;
				}
		}
		return true;
	}

	private void printWire(WireHashKey wireHashKey,
			TreeMap<Long, AtomicEvent<?>> value) {
		for (Entry<Long, AtomicEvent<?>> entry : value.entrySet()) {
			System.err.println(entry.getKey() + "\t ("
					+ entry.getValue().getState() + ")"
					+ entry.getValue().getType() + ":\t"
					+ entry.getValue().getContainer().getMark());
		}

	}

	private boolean compareMarksWithFirst(Map<Long, EMarkOfEvent> map) {
		if (map.size() != first.size())
			return false;
		for (Entry<Long, EMarkOfEvent> entry : map.entrySet()) {
			if (!first.get(entry.getKey()).equals(entry.getValue()))
				return false;
		}
		System.out.println("equals");
		return true;
	}

	private void printResult(Map<Long, EMarkOfEvent> exMap, boolean b) {
		if (b) {
			if (exMap == null)
				System.out.println("BEGIN");
			System.out.println("STORAGE WAS:");

			for (CEvent event : storage.getEvents()) {
				System.out.println(event.getStepId()
						+ "\t"
						+ event.getRuleId()
						 + "\t" + event.getMark()+ "\t"
							+ ((event.getRuleId()!= -1)? SimulationData.getData(kappaSystem.getRuleByID(event
									.getRuleId()), true) : "initial event") + "\t" + ((event.getRuleId()!= -1)? kappaSystem.getRuleByID(event.getRuleId()).getName(): "init"));
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

	private boolean checkTheCorrectnessOnWire(Map<Long, EMarkOfEvent> exMap,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		// AtomicEvent<?> tmp = null;
		AtomicEvent<?> prevaevent = null;

		long eventId = wire.getValue().firstKey();

		for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
				eventId, false); iterator.hasNext();) {
			eventId = iterator.next();
			while (exMap.get(eventId).equals(EMarkOfEvent.DELETED)) {
				if (iterator.hasNext()) {
					eventId = iterator.next();
				} else
					return true;
			}
			prevaevent = aevent;
			aevent = storage.getAtomicEvent(wire.getKey(), eventId);

			if (prevaevent == null) {
				if (aevent.getState().getBeforeState() != null) {
					// System.err.println("first event's before state != null");
					// System.out
					// .println("" + aevent.getContainer().getStepId()
					// + "(" + aevent.getContainer().getRuleId()
					// + ") before ="
					// + aevent.getState().getBeforeState());
					return false;
				}
			}

			if (!checkBetweenState(prevaevent, aevent, wire.getKey()
					.getTypeOfWire())) {
				// System.err.println("between: false");
				return false;
			}
		}
		prevaevent = null;
		aevent = null;
		return true;
	}

	private boolean checkBetweenState(AtomicEvent<?> first,
			AtomicEvent<?> second, ETypeOfWire typeOfWire) {
		if (first != null && second != null) {
			if (first.getContainer().getMark() != EMarkOfEvent.KEPT)
				assertTrue("TEST IS WRONG1: " + first.getContainer().getMark(),
						false);
			if (second.getContainer().getMark() != EMarkOfEvent.KEPT)
				assertTrue(
						"TEST IS WRONG2: " + second.getContainer().getMark(),
						false);

			if (first.getType().equals(EActionOfAEvent.TEST)) {
				// if (!first.getState().getBeforeState().equals(
				// second.getState().getBeforeState())) {
				// System.err.println("first type = " + first.getType()
				// + "1st = " + first.getState().getBeforeState());
				// System.err.println("second type = " + second.getType()
				// + "2nd = " + second.getState().getBeforeState());
				// System.err.println();
				// }
				return first.getState().getBeforeState().equals(
						second.getState().getBeforeState());
			} else {
				if (!second.getType().equals(EActionOfAEvent.MODIFICATION)) {
					// if (first.getState().getAfterState().equals(
					// second.getState().getBeforeState())) {
					// System.err.println("first type = " + first.getType()
					// + "1st = " + first.getState().getAfterState());
					// System.err
					// .println("second type = " + second.getType()
					// + "2nd = "
					// + second.getState().getBeforeState());
					//
					// }
					return first.getState().getAfterState().equals(
							second.getState().getBeforeState());
				}
			}
		}

		return true;
	}

	public List<List<EMarkOfEvent>> generateLists(int n) {
		List<List<EMarkOfEvent>> list = new ArrayList<List<EMarkOfEvent>>();
		for (int i = 0; i < Math.pow(2, n); i++) {
			List<EMarkOfEvent> tmp = new ArrayList<EMarkOfEvent>();
			list.add(tmp);
		}
		list = generate(n, list);
		list.remove(list.size() - 1);
		list.remove(0);
		return list;
	}

	private List<List<EMarkOfEvent>> generate(int n,
			List<List<EMarkOfEvent>> list) {
		int i = 0;
		if (n == 1) {
			for (List<EMarkOfEvent> list2 : list) {
				list2.add((i % 2 == 0) ? EMarkOfEvent.KEPT
						: EMarkOfEvent.DELETED);
				i++;
			}
			return list;
		} else {
			// System.out.println("n = " + n + "  2^n = " + Math.pow(2, n - 1));
			for (List<EMarkOfEvent> list2 : list) {
				list2
						.add(((i % Math.pow(2, n)) < Math.pow(2, n - 1)) ? EMarkOfEvent.KEPT
								: EMarkOfEvent.DELETED);
				i++;
			}
			return generate(--n, list);
		}
	}
	
	public Set<EMarkOfEvent []> generateGreyLists(int n) {
		Set<EMarkOfEvent []> set = new LinkedHashSet<EMarkOfEvent []>();
		EMarkOfEvent marks [] = new EMarkOfEvent[n];
		for (int i = 0; i < marks.length; i++) {
			marks[i] = EMarkOfEvent.DELETED;
		}
		int tmp;
		for (int i = 0; i < Math.pow(2, n) - 2; i++) {
			tmp = getIndex(i);
			marks[tmp] = inverse(marks[tmp]);
			set.add(marks);
		}
		
		
		
		return set;
		
	}

	private EMarkOfEvent inverse(EMarkOfEvent markOfEvent) {
		if (markOfEvent.equals(EMarkOfEvent.DELETED))
			return EMarkOfEvent.KEPT;
		else
			return EMarkOfEvent.DELETED;
	}

	private int getIndex(int i) {
		int index = 0;
		while(i%2 == 0){
			i = i/2;
			index++;
		}
		return index;
	}

}
