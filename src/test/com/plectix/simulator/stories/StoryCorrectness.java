package com.plectix.simulator.stories;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.MarkOfEvent;
import com.plectix.simulator.component.stories.State;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.component.stories.compressions.CompressionPassport;
import com.plectix.simulator.component.stories.storage.AbstractState;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.Event;
import com.plectix.simulator.component.stories.storage.EventIteratorInterface;
import com.plectix.simulator.component.stories.storage.StateOfLink;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;

public class StoryCorrectness {

	/**
	 * non empty storage
	 * 
	 * @param storage
	 * @throws StoryStorageException
	 */
	static void testStorage(WireStorageInterface storage)
			throws StoryStorageException {
		assertFalse(storage.getEvents().isEmpty());
		assertFalse(storage.getStorageWires().isEmpty());
	}

	/**
	 * empty wires
	 * 
	 * @throws StoryStorageException
	 * 
	 */
	static void testWires(WireStorageInterface storage)
			throws StoryStorageException {
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getValue().isEmpty()
					&& wire.getKey().getTypeOfWire() == TypeOfWire.AGENT) {
				CompressionPassport sp = storage.extractPassport();
				Long id = wire.getKey().getAgentId();

				for (WireHashKey wk : sp.getAgentWires(id)) {
					if (!storage.getStorageWires().get(wk).isEmpty()) {
						System.out
								.println("azdfgslkjdfhzlkdfjhbvlzkdfjglzkdvlzjvzj");
						System.out.println(storage.getStorageWires().get(wk)
								.size());
						System.out.println(storage.getStorageWires().get(wk)
								.entrySet());
						System.out.println(storage.observableEvent()
								.getStepId()
								+ "zsdfgjhgbdf");
					}
				}

			}

		}

	}

	static void testInternalStatesIterator(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent;
		LinkedHashSet<String> internalSet;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getKey().getTypeOfWire().equals(TypeOfWire.INTERNAL_STATE)) {

				internalSet = new LinkedHashSet<String>();
				for (Iterator<String> iterator = storage.getInformationAboutWires()
						.wireInternalStateIterator(wire.getKey()); iterator
						.hasNext();) {
					internalSet.add(iterator.next());
				}

				long eventId = wire.getValue().firstKey();
				for (EventIteratorInterface iterator = storage.eventIterator(
						wire.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					if (aevent.getState().getBeforeState() != null)
						assertTrue(internalSet.contains(aevent.getState()
								.getBeforeState()));
					if (aevent.getState().getAfterState() != null)
						assertTrue("\nwire " + wire.getKey() + "\ntwireHash "
								+ wire.getKey().hashCode()
								+ "\nthere are no internal state "
								+ aevent.getState().getAfterState(),
								internalSet.contains(aevent.getState()
										.getAfterState()));
				}
				internalSet = null;
			}
		}
	}

	static void testOfParallelCorrectness(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent;
		Event econtainer;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getValue().isEmpty()) {
				continue;
			}
			if (wire.getKey().getTypeOfWire().equals(TypeOfWire.LINK_STATE)) {
				long eventId = wire.getValue().firstKey();
				for (EventIteratorInterface iterator = storage.eventIterator(
						wire.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					econtainer = aevent.getContainer();
					checkParallelLinkStates(eventId, aevent, wire, econtainer);
				}
			}
			if (wire.getKey().getTypeOfWire().equals(TypeOfWire.AGENT)) {
				long eventId = wire.getValue().firstKey();
				for (EventIteratorInterface iterator = storage.eventIterator(
						wire.getKey(), eventId, false); iterator.hasNext();) {
					eventId = iterator.next();
					aevent = storage.getAtomicEvent(wire.getKey(), eventId);
					econtainer = aevent.getContainer();
					checkParallelAgentStates(eventId, aevent, wire, econtainer);
				}
			}
		}
	}

	public static void testOfStates(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			if (wire.getValue() == null) {
				System.out.println("value =null");
			}
			if (wire.getValue().isEmpty()) {
				continue;
			}
			// if(wire.getValue().firstKey()==null){
			// System.out.println("firts");
			// }
			long eventId = wire.getValue().firstKey();
			for (EventIteratorInterface iterator = storage.eventIterator(wire
					.getKey(), eventId, false); iterator.hasNext();) {
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

	static void testOfStatesSensitive(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		AtomicEvent<?> prevaevent = null;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			long eventId = wire.getValue().firstKey();
			for (EventIteratorInterface iterator = storage.eventIterator(wire
					.getKey(), eventId, false); iterator.hasNext();) {
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

	static void testWireWithMinUnresolved(WireStorageInterface storage)
			throws StoryStorageException {
		WireHashKey wireWithMin = null;
		LinkedHashSet<WireHashKey> set = null;
		int tmpMin = Integer.MAX_VALUE;
		for (Event event : storage.getEvents()) {
			set = new LinkedHashSet<WireHashKey>();
			wireWithMin = event.getWireWithMinimumUresolvedEvent(storage.getInformationAboutWires());
			if (wireWithMin != null) {
				for (WireHashKey wkey : event.getAtomicEvents().keySet()) {
					int counter = 0;
					for (AtomicEvent<?> at : storage.getStorageWires()
							.get(wkey).values()) {
						if (at.getType().equals(ActionOfAEvent.TEST))
							continue;
						if (at.getContainer().getMark().equals(
								MarkOfEvent.UNRESOLVED))
							counter++;
					}

					if (counter != 0) {
						if (counter < tmpMin) {
							set.clear();
							set.add(wkey);
							tmpMin = counter;
						} else if (counter == tmpMin) {
							set.add(wkey);
						}
					}
				}
				assertTrue(!set.isEmpty());
				if (!set.isEmpty()) {
					// System.err.println("wireWithMin = " + wireWithMin
					// + storage.getUnresolvedModifyCount(wireWithMin));
					// for (WireHashKey wkey : set) {
					// System.out.println("  " + wkey.toString() + "  "
					// + storage.getUnresolvedModifyCount(wkey));
					// }
					assertTrue(set.contains(wireWithMin));
				}

				tmpMin = Integer.MAX_VALUE;
			}

		}
	}

	static public void testCompareStorageMaps(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		Event econtainer = null;
		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> map1 = storage
				.getStorageWires();
		// Map<Event, Map<WireHashKey, AtomicEvent<?>>> map2 = storage
		// .getWiresByEvent();
		Set<Event> set = storage.getEvents();

		int aeventCounter1 = 0;
		int aeventCounter2 = 0;

		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : map1
				.entrySet()) {
			if (wire.getValue().isEmpty()) {
				continue;
			}
			aeventCounter1 += wire.getValue().size();
			long eventId = wire.getValue().firstKey();
			for (EventIteratorInterface iterator = storage.eventIterator(wire
					.getKey(), eventId, false); iterator.hasNext();) {
				eventId = iterator.next();
				aevent = storage.getAtomicEvent(wire.getKey(), eventId);
				econtainer = aevent.getContainer();
				// if (!map2.containsKey(econtainer))
				// System.out.println();

				if (!set.contains(econtainer)) {
					System.out.println();
				}
				// assertTrue(map2.containsKey(econtainer));
				// assertTrue(map2.get(econtainer).containsKey(wire.getKey()));
				// assertTrue(map2.get(econtainer).containsValue(aevent));

				assertTrue(set.contains(econtainer));
				assertTrue(econtainer.getAtomicEvents().containsKey(
						wire.getKey()));
				assertTrue(containsAEvent(econtainer, aevent));

			}
		}

		for (Event event : set) {
			aeventCounter2 += event.getAtomicEvents().size();
		}
		assertTrue(aeventCounter1 == aeventCounter2);

	}

	private static boolean containsAEvent(Event econtainer,
			AtomicEvent<?> aevent) {

		if (aevent.getContainer().getStepId() != econtainer.getStepId()) {
			return false;
		}

		for (AtomicEvent<?> ae : econtainer.getAtomicEvents().values()) {
			if (!ae.getType().equals(aevent.getType()))
				continue;
			AbstractState<?> state = ae.getState();
			AbstractState<?> state2 = aevent.getState();

			if (state.getAfterState() == null && state2.getAfterState() != null)
				continue;
			if (state.getAfterState() != null && state2.getAfterState() == null)
				continue;
			if (state.getBeforeState() == null
					&& state2.getBeforeState() != null)
				continue;

			if (state.getBeforeState() != null
					&& state2.getBeforeState() == null)
				continue;

			if ((state.getAfterState() != null && state2.getAfterState() != null)
					&& !state.getAfterState().equals(state2.getAfterState()))
				continue;

			if ((state.getBeforeState() != null && state2.getBeforeState() != null)
					&& !state.getBeforeState().equals(state2.getBeforeState()))
				continue;

			return true;

		}
		// System.out.println(econtainer.getStepId());
		// System.out.println(aevent);
		// for (AtomicEvent<?> ae : econtainer.getAtomicEvents().values()) {
		// System.out.println(ae);
		// }
		return false;
	}

	static void testCountUnresolvedOnWire(WireStorageInterface storage)
			throws StoryStorageException {
		// try {
		// storage.markAllUnresolved();
		// } catch (StoryStorageException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Map<WireHashKey, TreeMap<Long, AtomicEvent<?>>> storageWires = storage
				.getStorageWires();
		WireHashKey wKey;
		int count;
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> entry : storageWires
				.entrySet()) {
			wKey = entry.getKey();
			count = 0;
			for (AtomicEvent<?> aEvent : entry.getValue().values()) {
				if (aEvent.getType() != ActionOfAEvent.TEST
						&& aEvent.getContainer().getMark() == MarkOfEvent.UNRESOLVED) {
					count++;
				}
			}

			// assertTrue(storage.getUnresolvedModifyCount(wKey) == count);
			if (storage.getInformationAboutWires().getUnresolvedModifyCount(wKey) != count) {
				System.out.println((storage.getInformationAboutWires().getUnresolvedModifyCount(wKey))
						+ "  " + (count) + (wKey.getTypeOfWire()));
			}
		}
	}

	private static void checkParallelAgentStates(long eventId,
			AtomicEvent<?> aevent,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire,
			Event econtainer) {
		for (Entry<WireHashKey, AtomicEvent<?>> ae : econtainer
				.getAtomicEvents().entrySet()) {
			if (ae.getKey().getAgentId() == (wire.getKey().getAgentId())) {
				checkingParallelAgentStates(ae.getValue().getState(), aevent
						.getState(), ae.getKey(), aevent.getType(), ae
						.getValue(), econtainer);

			}
		}
	}

	private static void checkingParallelAgentStates(AbstractState<?> state,
			AbstractState<?> agentState, WireHashKey wireHashKey,
			ActionOfAEvent type, AtomicEvent<?> atomicEvent, Event econtainer) {
		if (agentState.getBeforeState() == null) {
			assertTrue("before", state.getBeforeState() == null);
		}

		if (agentState.getAfterState() == null
				&& type.equals(ActionOfAEvent.TEST_AND_MODIFICATION)) {
			assertTrue("after: " + wireHashKey.getTypeOfWire() + "\n"
					+ econtainer.getRuleId() + "\n" + econtainer.getStepId()
					+ "\n" + atomicEvent.getType() + "\n"
					+ wireHashKey.hashCode() + "\n" + state.getAfterState(),
					state.getAfterState() == null);
		}

	}

	private static void checkParallelLinkStates(long eventId,
			AtomicEvent<?> aevent,
			Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire,
			Event econtainer) {
		for (Entry<WireHashKey, AtomicEvent<?>> ae : econtainer
				.getAtomicEvents().entrySet()) {
			if (ae.getKey().getTypeOfWire().equals(TypeOfWire.BOUND_FREE)) {
				if (ae.getKey().getSiteName().equals(
						(wire.getKey().getSiteName()))
						&& ae.getKey().getAgentId() == (wire.getKey()
								.getAgentId())) {
					checkingParallelBondStates(ae.getValue().getState(), aevent
							.getState());

				}

			}
		}

	}

	private static void checkingParallelBondStates(AbstractState<?> boundstate,
			AbstractState<?> linkstate) {
		if (boundstate.getBeforeState() == null) {
			assertTrue(linkstate.getBeforeState() == null);
		} else if (boundstate.getBeforeState().equals(State.FREE_LINK_STATE))
			assertTrue(((StateOfLink) linkstate.getBeforeState()).isFree());
		else {
			assertTrue((linkstate.getBeforeState() == null)
					|| !((StateOfLink) linkstate.getBeforeState()).isFree());
		}
		if (boundstate.getAfterState() == null) {
			assertTrue(linkstate.getAfterState() == null);
		} else if (boundstate.getAfterState().equals(State.FREE_LINK_STATE))
			assertTrue(((StateOfLink) linkstate.getAfterState()).isFree());
		else
			assertFalse(((StateOfLink) linkstate.getAfterState()).isFree());
	}

	static void printStorage(WireStorageInterface storage)
			throws StoryStorageException {
		AtomicEvent<?> aevent = null;
		System.out
				.println("\n______________________________________________\n");
		for (Entry<WireHashKey, TreeMap<Long, AtomicEvent<?>>> wire : storage
				.getStorageWires().entrySet()) {
			long eventId = wire.getValue().firstKey();
			System.out.print(wire.getKey().hashCode() + ": \t" + wire.getKey()
					+ "\t");
			for (EventIteratorInterface iterator = storage.eventIterator(wire
					.getKey(), eventId, false); iterator.hasNext();) {
				eventId = iterator.next();
				// System.out.print(eventId);
				aevent = storage.getAtomicEvent(wire.getKey(), eventId);
				// if (aevent)
				// System.out.print("\t" + eventId);

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

	private static void checkStates(AtomicEvent<?> aevent,
			WireHashKey wireHashKey) {
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
				if (!wireHashKey.getTypeOfWire().equals(TypeOfWire.BOUND_FREE))
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

	private static void checkNextState(AtomicEvent<?> first,
			AtomicEvent<?> second, WireHashKey wireHashKey) {
		if (first != null && second != null) {

			if (first.getType().equals(ActionOfAEvent.TEST))
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
					if (!second.getType().equals(ActionOfAEvent.MODIFICATION))
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

	public static void testLinks(WireStorageInterface storage) {
		for (WireHashKey wk : storage.getStorageWires().keySet()) {
			if (wk.getTypeOfWire() != TypeOfWire.LINK_STATE)
				continue;
			StateOfLink must = new StateOfLink(wk.getAgentId(), wk
					.getSiteName());
			for (AtomicEvent<?> ae : storage.getStorageWires().get(wk).values()) {
				if (ae.getContainer().getStepId() == -1) {
					testLinkAfter(ae, storage, must);
					continue;
				}
				if (ae.getType() == ActionOfAEvent.TEST) {
					testTestLink(ae, storage, must);
				} else {
					if (ae.getState().getBeforeState() == null) {
						testLinkAfter(ae, storage, must);
					} else {
						testLinkBefore(ae, storage, must);
						testLinkAfter(ae, storage, must);
					}

				}

			}
		}
	}

	private static void testLinkBefore(AtomicEvent<?> ae,
			WireStorageInterface storage, StateOfLink must) {
		StateOfLink stateOfLink = (StateOfLink) (ae.getState().getBeforeState());
		if (stateOfLink.getAgentId() == -1) {
			return;
		}
		if (!linkByLink(stateOfLink,
				ae.getContainer().getStepId() - 1, storage,must)) {
			System.out.println("before");
		}
		assertTrue(linkByLink(stateOfLink, ae.getContainer()
				.getStepId() - 1, storage,must));

	}

	private static void testLinkAfter(AtomicEvent<?> ae,
			WireStorageInterface storage, StateOfLink must) {
		StateOfLink stateOfLink = (StateOfLink) (ae.getState().getAfterState());
		if (stateOfLink == null || stateOfLink.getAgentId() == -1) {
			return;
		}

		
		if (!linkByLink(stateOfLink, ae.getContainer().getStepId(),
				storage,must)) {
			System.out.println("after");
			System.out.println(ae.getContainer());
		}
		assertTrue(linkByLink(stateOfLink, ae.getContainer()
				.getStepId(), storage,must));

	}

	private static void testTestLink(AtomicEvent<?> ae,
			WireStorageInterface storage, StateOfLink must) {
		StateOfLink stateOfLink = (StateOfLink) (ae.getState().getBeforeState());
		if (stateOfLink.getAgentId() == -1) {
			return;
		}
		if (!linkByLink(stateOfLink, ae.getContainer().getStepId(),
				storage,must)) {
			System.out.println("test");
		}
		assertTrue(linkByLink(stateOfLink, ae.getContainer()
				.getStepId(), storage,must));
	}

	private static boolean linkByLink(StateOfLink stateOfLink, long i,
			WireStorageInterface storage, StateOfLink must) {
		TreeMap<Long, AtomicEvent<?>> treeMap = storage.getStorageWires().get(
				new WireHashKey(stateOfLink.getAgentId(), stateOfLink
						.getSiteName(), TypeOfWire.LINK_STATE));
		if (treeMap == null) {
			throw new RuntimeException();
		}
		Entry<Long, AtomicEvent<?>> floorEntry = treeMap.floorEntry(i);
		if (floorEntry != null) {
			AtomicEvent<?> next = floorEntry.getValue();
			if (floorEntry.getKey().equals(i)
					&& next.getType() != ActionOfAEvent.TEST) {
				StateOfLink afterState = (StateOfLink) next.getState().getAfterState();
				return equalsOfLink(must, afterState);
			}
			if (next.getState().getAfterState() == null) {
				StateOfLink beforeState = (StateOfLink) next.getState().getBeforeState();
				return equalsOfLink(must, beforeState);
			} else {
				StateOfLink afterState = (StateOfLink) next.getState().getAfterState();
				return equalsOfLink(must, afterState);
			}
		} else {
			return false;

		}

	}

	private static boolean equalsOfLink(StateOfLink must, StateOfLink afterState) {
		if(!must.equals(afterState)){
			System.out.println(must);
			System.out.println(afterState);
		}
		return must.equals(afterState);
	}

	public static void testAll(WireStorageInterface storage)
			throws StoryStorageException {
		testStorage(storage);
		testWires(storage);
		testOfStates(storage);
		testOfParallelCorrectness(storage);
		testInternalStatesIterator(storage);
		testWireWithMinUnresolved(storage);
		testCompareStorageMaps(storage);
		testCountUnresolvedOnWire(storage);
		testLinks(storage);
	}
}
