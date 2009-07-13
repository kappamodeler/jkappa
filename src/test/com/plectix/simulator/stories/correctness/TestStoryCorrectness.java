package com.plectix.simulator.stories.correctness;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.logging.impl.AvalonLogger;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EMarkOfEvent;
import com.plectix.simulator.components.stories.enums.EState;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AState;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.stories.InitStoriesTests;

public class TestStoryCorrectness extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
	 //+ separator + "simple"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}

	private String fileName;

	public TestStoryCorrectness(String fileName) {
		super(path, fileName, false, false);
		this.fileName = fileName;
	}

	@Test
	public void testCorrectness() {
		System.out.println("\n\n**************************************fileName = "
				+ fileName);
		CStories stories = getStories();
		Map<Integer, IWireStorage> storages = stories
				.getEventsMapForCurrentStory();
		try {
			for (IWireStorage storage : storages.values()) {
				if(!storage.isImportantStory())
					continue;
				
				printStorage(storage);
				testStorage(storage);
				
				testWires(storage);
				testOfStates(storage);
				testOfParallelCorrectness(storage);
				testInternalStatesIterator(storage);
				testWireWithMinUnresolved(storage);
				testCompareStorageMaps(storage);
				testCompareStorageMaps(storage);
				
			}
		} catch (StoryStorageException e) {
			e.printStackTrace();
		}

	}


	private void testCountUnresolvedOnWire(IWireStorage storage) {
		try {
			storage.markAllUnresolved();
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires = storage.getStorageWires();
		WireHashKey wKey;
		int count;
		for(Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> entry : storageWires.entrySet()){
			wKey = entry.getKey();
			count =0;
			for(AtomicEvent<?> aEvent : entry.getValue().values()){
				if(aEvent.getType()!=EActionOfAEvent.TEST &&aEvent.getContainer().getMark()==EMarkOfEvent.UNRESOLVED){
					count++;
				}
			}
			
			assertTrue(storage.getUnresolvedModifyCount(wKey)==count);
		}	
	}

	
	private void testStorage(IWireStorage storage) throws StoryStorageException {
		assertFalse(storage.getWiresByEvent().isEmpty());
		assertFalse(storage.getStorageWires().isEmpty());
		
		System.out.println(storage.observableEvent().getMark());
	}

	private void testWireWithMinUnresolved(IWireStorage storage) {
		Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> map = storage.getWiresByEvent();
		WireHashKey wireWithMin = null;
		HashSet<Integer> set = null;
		int tmpMin = Integer.MAX_VALUE;
		for (Entry<CEvent, Map<WireHashKey, AtomicEvent<?>>> event : map.entrySet()) {
			wireWithMin = event.getKey().getWireWithMinimumUresolvedEvent(storage);
//			assertTrue(wireWithMin!=null);
			if (wireWithMin != null) {
				for (WireHashKey wkey : event.getValue().keySet()) {
					int counter = 0;
					for (AtomicEvent<?> at : storage.getStorageWires().get(wkey).values()) {
						if (!at.getType().equals(EActionOfAEvent.TEST) && at.getContainer().getMark().equals(EMarkOfEvent.UNRESOLVED))
							counter++;
					}
					if (counter < tmpMin){
						set = new HashSet<Integer>();
						set.add(wkey.hashCode());
					} else if (counter == tmpMin){
						set.add(wkey.hashCode());
					}
				}
				assertTrue(set.contains(wireWithMin.hashCode()));
				set = null;
				tmpMin = Integer.MAX_VALUE;
			}
				
		}
	}

	private void testCompareStorageMaps(IWireStorage storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		CEvent econtainer = null;
		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> map1 = storage
				.getStorageWires();
		Map<CEvent, Map<WireHashKey, AtomicEvent<?>>> map2 = storage
				.getWiresByEvent();
		Map<WireHashKey, AtomicEvent<?>> map3;
		
		int aeventCounter1 = 0;
		int aeventCounter2 = 0;
		
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : map1
				.entrySet()) {
			aeventCounter1 += wire.getValue().size();
			long eventId = wire.getValue().firstKey();
			for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
					eventId, false); iterator.hasNext();) {
				eventId = iterator.next();
				aevent = storage.getAtomicEvent(wire.getKey(), eventId);
				econtainer = aevent.getContainer();
				if(!map2.containsKey(econtainer))
					System.out.println();
				assertTrue(map2.containsKey(econtainer));
				assertTrue(map2.get(econtainer).containsKey(wire.getKey()));
				assertTrue(map2.get(econtainer).containsValue(aevent));
			}
		}

		for (Entry<CEvent, Map<WireHashKey, AtomicEvent<?>>> event : map2
				.entrySet()) {
			aeventCounter2 += event.getValue().size();
		}
		assertTrue(aeventCounter1 == aeventCounter2);

	}

	private void testWires(IWireStorage storage) {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			assertTrue("wire " + wire.getKey() + "  is empty :'(", !wire
					.getValue().isEmpty());
		}

	}

	private void testInternalStatesIterator(IWireStorage storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent;
		HashSet<Integer> internalSet;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getKey().getTypeOfWire()
					.equals(ETypeOfWire.INTERNAL_STATE)) {

				internalSet = new HashSet<Integer>();
				for (Iterator<Integer> iterator = storage
						.wireInternalStateIterator(wire.getKey()); iterator
						.hasNext();) {
					internalSet.add(iterator.next());
				}

				long eventId = wire.getValue().firstKey();
				for (IEventIterator iterator = storage.eventIterator(wire
						.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					if (aevent.getState().getBeforeState() != null)
						assertTrue(internalSet.contains(aevent.getState()
								.getBeforeState()));
					if (aevent.getState().getAfterState() != null)
						assertTrue("\nwire " + wire.getKey()
								+ "\ntwireHash " + wire.getKey().hashCode()
								+ "\nthere are no internal state "
								+ aevent.getState().getAfterState(),
								internalSet.contains(aevent.getState()
										.getAfterState()));
				}
				internalSet = null;
			}
		}
	}

	private void testOfParallelCorrectness(IWireStorage storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent;
		CEvent econtainer;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getKey().getTypeOfWire().equals(ETypeOfWire.LINK_STATE)) {
				// System.out.print(wire.getKey().hashCode() + ": \t");
				long eventId = wire.getValue().firstKey();
				for (IEventIterator iterator = storage.eventIterator(wire
						.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					econtainer = aevent.getContainer();
					checkParallelLinkStates(eventId, aevent, wire, econtainer);
				}
			}
			if (wire.getKey().getTypeOfWire().equals(ETypeOfWire.AGENT)) {
				// System.out.print(wire.getKey().hashCode() + ": \t");
				long eventId = wire.getValue().firstKey();
				for (IEventIterator iterator = storage.eventIterator(wire
						.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					econtainer = aevent.getContainer();
					checkParallelAgentStates(eventId, aevent, wire, econtainer);
				}
			}
		}
	}

	private void checkParallelAgentStates(long eventId, AtomicEvent<?> aevent,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire,
			CEvent econtainer) {
		for (Entry<WireHashKey, AtomicEvent<?>> ae : econtainer
				.getAtomicEvents().entrySet()) {
			if (ae.getKey().getAgentId().equals(wire.getKey().getAgentId())) {
				checkingParallelAgentStates(ae.getValue().getState(), aevent
						.getState(), ae.getKey(), aevent.getType(), ae
						.getValue(), econtainer);

			}
		}
	}

	private void checkingParallelAgentStates(AState<?> state,
			AState<?> agentState, WireHashKey wireHashKey,
			EActionOfAEvent type, AtomicEvent<?> atomicEvent, CEvent econtainer) {
		if (agentState.getBeforeState() == null) {
			assertTrue("before", state.getBeforeState() == null);
		}
		// else {
		// assertFalse(state.getBeforeState() == null);
		//
		// }
		if (agentState.getAfterState() == null
				&& type.equals(EActionOfAEvent.TEST_AND_MODIFICATION)) {
			// && !type.equals(EActionOfAEvent.TEST)) {
			assertTrue("after: " + wireHashKey.getTypeOfWire() + "\n"
					+ econtainer.getRuleId() + "\n" + econtainer.getStepId()
					+ "\n" + atomicEvent.getType() + "\n"
					+ wireHashKey.hashCode() + "\n" + state.getAfterState(),
					state.getAfterState() == null);
		}
		// else {
		// assertFalse(state.getAfterState() == null);
		//
		// }

	}

	private void checkParallelLinkStates(long eventId, AtomicEvent<?> aevent,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire,
			CEvent econtainer) {
		for (Entry<WireHashKey, AtomicEvent<?>> ae : econtainer
				.getAtomicEvents().entrySet()) {
			if (ae.getKey().getTypeOfWire().equals(ETypeOfWire.BOUND_FREE)) {
				if (ae.getKey().getSiteId().equals(wire.getKey().getSiteId())
						&& ae.getKey().getAgentId().equals(
								wire.getKey().getAgentId())) {
					checkingParallelBondStates(ae.getValue().getState(), aevent
							.getState());

				}

			}
		}

	}

	private void checkingParallelBondStates(AState<?> boundstate,
			AState<?> linkstate) {
		if (boundstate.getBeforeState() == null) {
			assertTrue(linkstate.getBeforeState() == null);
		} else if (boundstate.getBeforeState().equals(EState.FREE_LINK_STATE))
			assertTrue(((CStateOfLink) linkstate.getBeforeState()).isFree());
		else
			assertFalse(((CStateOfLink) linkstate.getBeforeState()).isFree());

		if (boundstate.getAfterState() == null) {
			assertTrue(linkstate.getAfterState() == null);
		} else if (boundstate.getAfterState().equals(EState.FREE_LINK_STATE))
			assertTrue(((CStateOfLink) linkstate.getAfterState()).isFree());
		else
			assertFalse(((CStateOfLink) linkstate.getAfterState()).isFree());
	}

	private void printStorage(IWireStorage storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		System.out.println("\n______________________________________________\n");
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			long eventId = wire.getValue().firstKey();
			System.out.print(wire.getKey().hashCode() + ": \t" + wire.getKey() + "\t");
			for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
					eventId, false); iterator.hasNext();) {
				eventId = iterator.next();
				// System.out.print(eventId);
				aevent = storage.getAtomicEvent(wire.getKey(), eventId);
				// if (aevent)
//				System.out.print("\t" + eventId);

				switch (aevent.getType()) {
				case TEST:
					System.out.print(eventId + "TEST" + "("
							+ aevent.getState().getBeforeState() + "; "
							+ aevent.getState().getAfterState() + ")");
					break;
				case TEST_AND_MODIFICATION:
					System.out.print(eventId + "TEST_AND_MOD" + "["
							+ aevent.getState().getBeforeState() + "; "
							+ aevent.getState().getAfterState() + "]");
					break;
				case MODIFICATION:
					System.out.print(eventId + "MOD" + "{"
							+ aevent.getState().getBeforeState() + "; "
							+ aevent.getState().getAfterState() + "}");
					break;

				default:
					break;
				}
				System.out.print("\t");
			}
			System.out.println();
		}

	}

	private void testOfStates(IWireStorage storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			long eventId = wire.getValue().firstKey();
			for (IEventIterator iterator = storage.eventIterator(wire.getKey(),
					eventId, false); iterator.hasNext();) {
				eventId = iterator.next();
				prevaevent = aevent;
				aevent = storage.getAtomicEvent(wire.getKey(), eventId);

				checkStates(aevent, wire.getKey());

				checkNextState(prevaevent, aevent, wire.getKey());
			}
			prevaevent = null;
			aevent = null;
		}
	}

	private void checkStates(AtomicEvent<?> aevent, WireHashKey wireHashKey) {
		if (aevent != null) {
			switch (aevent.getType()) {
			case TEST:
				assertTrue("atomic event " + aevent.getContainer().getRuleId()
						+ " with \"Test\" type has not null state after:\n"
						+ aevent.getState().getBeforeState() + "\n"
						+ aevent.getState().getAfterState(), aevent.getState()
						.getAfterState() == null);
				break;
			case TEST_AND_MODIFICATION:
				if (!wireHashKey.getTypeOfWire().equals(ETypeOfWire.BOUND_FREE))
					assertFalse(
							"atomic event "
									+ aevent.getContainer().getRuleId()
									+ "  with \"Test&Mod\" type has same states before and after:\n"
									+ aevent.getState().getBeforeState() + "\n"
									+ aevent.getState().getAfterState(), aevent
									.getState().isBeforeEqualsAfter());
				break;
			// case MODIFICATION:
			// assertTrue(aevent.getState().getBeforeState() != null);
			default:
				break;
			}
		}
	}

	private void checkNextState(AtomicEvent<?> first, AtomicEvent<?> second,
			WireHashKey wireHashKey) {
		if (first != null && second != null) {

			if (first.getType().equals(EActionOfAEvent.TEST))
				try {
					assertTrue("between atomic events: \n"
							+ wireHashKey.hashCode() + "\n"
							+ wireHashKey.getTypeOfWire().toString() + "\n"
							+ first.getContainer().getStepId() + "\t"
							+ first.getState().getBeforeState() + "\n"
							+ second.getContainer().getStepId() + "\t"
							+ second.getState().getBeforeState(), first
							.getState().getBeforeState().equals(
									second.getState().getBeforeState()));
				} catch (NullPointerException e) {
					assertTrue("dsdasd\n" + first.getType() + "\n"
							+ wireHashKey.getTypeOfWire(), false);
				}
			else {
				try {
					if (!second.getType().equals(EActionOfAEvent.MODIFICATION))
						assertTrue("between atomic events: \n"
								+ wireHashKey.hashCode() + "\n"
								+ wireHashKey.getTypeOfWire().toString()
								+ "\n\n" + first.getContainer().getStepId()
								+ "\t" + first.getType() + "\n"
								+ first.getState().getBeforeState() + "\n"
								+ "*" + first.getState().getAfterState()
								+ "\n\n"

								+ second.getContainer().getStepId() + "\t"
								+ second.getType() + "\n" + "*"
								+ second.getState().getBeforeState() + "\n"
								+ second.getState().getAfterState(), first
								.getState().getAfterState().equals(
										second.getState().getBeforeState()));
				} catch (NullPointerException e) {
					assertTrue("dasd\n" + second.getType() + "\n"
							+ first.getContainer().getStepId() + "\n"
							+ second.getContainer().getStepId() + "\n"
							+ wireHashKey.getTypeOfWire(), false);
				}
			}

		}

	}

	private void testBlocksForDelete(IWireStorage storage)
			throws StoryStorageException {
		CEvent obs = (CEvent) storage.observableEvent();
		// storage.eventIterator(wkey, reverse)
	}

}
