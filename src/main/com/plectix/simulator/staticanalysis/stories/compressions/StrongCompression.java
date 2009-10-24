package com.plectix.simulator.staticanalysis.stories.compressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.storage.AtomicEvent;
import com.plectix.simulator.staticanalysis.stories.storage.EventInterface;
import com.plectix.simulator.staticanalysis.stories.storage.EventIteratorInterface;
import com.plectix.simulator.staticanalysis.stories.storage.StateOfLink;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireHashKey;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

/*package*/ final class StrongCompression {
	private final CompressionPassport passport;
	private final WeakCompression weak;

	private final ArrayList<Long> agents1 = new ArrayList<Long>();
	private final ArrayList<Long> agents2 = new ArrayList<Long>();
	private final ArrayList<ExtensionData> extensionLinks = new ArrayList<ExtensionData>();

	public StrongCompression(CompressionPassport passport) {
		this.passport = passport;
		this.weak = new WeakCompression(passport.getStorage());
	}

	public final void process() throws StoryStorageException {
		boolean improved = true;

		// StoryCorrectness.testLinks(passport.getStorage());
		weak.process();
		passport.removeEventWithMarkDelete();

		while (improved) {
			improved = false;

			if (passport.eventCount() < 3)
				return;

			EventIteratorInterface eventIterator1 = passport
					.eventIterator(false);
			EventIteratorInterface eventIterator2 = passport
					.eventIterator(true);

			while (eventIterator1.hasNext() && eventIterator2.hasNext()) {
				if (eventIterator1.next() > eventIterator2.next())
					break;

				EventInterface event1 = eventIterator1.value();
				EventInterface event2 = eventIterator2.value();

				if ((event1.getStepId() != -1 && lookThroughPerturbations(
						event1, true))
						|| lookThroughPerturbations(event2, false)) {
					improved = true;
					break;
				}
			}
		}
	}

	private final boolean lookThroughPerturbations(EventInterface event,
			boolean swapTop) throws StoryStorageException {
		Iterator<String> typeIterator = passport.agentTypeIterator();

		boolean improved = false;

		// System.out.println(event.getStepId() + " : event");

		while (typeIterator.hasNext() && !improved) {
			String type = typeIterator.next();

			Iterator<Long> agentIterator1 = passport.agentIterator(type);
			while (agentIterator1.hasNext() && !improved) {
				Long agentId1 = agentIterator1.next();

				Iterator<Long> agentIterator2 = passport.agentIterator(type);

				while (agentIterator2.hasNext())
					if (agentIterator2.next() == agentId1)
						break;

				while (agentIterator2.hasNext() && !improved) {
					Long agentId2 = agentIterator2.next();

					if (!extendSwap(event, agentId1, agentId2, swapTop))
						continue;

					if (extensionLinks.size() > 1) {
						continue;
					}
					EventInterface neiEvent = passport
							.swapAgents(agents1, agents2, extensionLinks, event
									.getStepId(), swapTop);

					// TODO: remember swapped pairs
					if (weak.processInconsistent(event, neiEvent)) {
						improved = true;
						passport.removeEventWithMarkDelete();
					} else
						passport.undoSwap();
					//StoryCorrectness.testLinks(passport.getStorage());
				}
			}
		}

		return improved;
	}

	private final boolean doesAgentMatter(EventInterface event, Long agentId) {
		ArrayList<WireHashKey> wires = passport.getAgentWires(agentId);

		for (WireHashKey wire : wires)
			if (event.containsWire(wire))
				return true;
		return false;
	}

	// TODO: may be should start from neiEvent...
	private final boolean extendSwap(EventInterface event, Long agentId1,
			Long agentId2, boolean swapTop) throws StoryStorageException {
		int curPair = 0;

		agents1.clear();
		agents2.clear();

		extensionLinks.clear();

		agents1.add(agentId1);
		agents2.add(agentId2);

		if (!doesAgentMatter(event, agentId1)
				&& !doesAgentMatter(event, agentId2))
			return false;

		while (curPair != agents1.size()) {
			if (!extendPair(event, curPair, swapTop))
				return false;
			curPair++;
		}

		return true;
	}

	protected final boolean extendPair(EventInterface event, int pairIdx,
			boolean swapTop) throws StoryStorageException {
		Long agentId1 = agents1.get(pairIdx);
		Long agentId2 = agents2.get(pairIdx);

		if (!passport.isAbleToSwap(agentId1, agentId2))
			return false;

		ArrayList<WireHashKey> wires1 = passport.getAgentWires(agentId1);
		ArrayList<WireHashKey> wires2 = passport.getAgentWires(agentId2);

		WireStorageInterface storage = passport.getStorage();

		for (WireHashKey w1 : wires1) {
			WireHashKey w2 = null;

			if (w1.getTypeOfWire() != TypeOfWire.LINK_STATE)
				continue;

			for (WireHashKey _w2 : wires2) {
				if (_w2.getTypeOfWire() == TypeOfWire.LINK_STATE
						&& w1.getSiteName() == _w2.getSiteName()) {
					w2 = _w2;
					break;
				}
			}

			if (w2 == null)
				throw new StoryStorageException("extendPair: different agents");

			Long upperEventId1 = storage.getStorageWires().get(w1).floorKey(
					event.getStepId());
			Long upperEventId2 = storage.getStorageWires().get(w2).floorKey(
					event.getStepId());

			if (!swapTop && upperEventId1 != null
					&& upperEventId1.equals(event.getStepId()))
				upperEventId1 = storage.getStorageWires().get(w1).lowerKey(
						event.getStepId());
			if (!swapTop && upperEventId2 != null
					&& upperEventId2.equals(event.getStepId()))
				upperEventId2 = storage.getStorageWires().get(w2).lowerKey(
						event.getStepId());

			AtomicEvent<?> event1 = null;
			AtomicEvent<?> event2 = null;

			if (upperEventId1 != null)
				event1 = storage.getStorageWires().get(w1).get(upperEventId1);
			if (upperEventId2 != null)
				event2 = storage.getStorageWires().get(w2).get(upperEventId2);

			StateOfLink state1 = getStateInSpace(event1);
			StateOfLink state2 = getStateInSpace(event2);

			if (state1 == null && state2 == null)
				continue;

			if (state1 == null || state2 == null)
				return false;

			if (passport.getAgentType(state1.getAgentId()) != passport
					.getAgentType(state2.getAgentId()))
				return false;

			if (!state1.getSiteName().equals(state2.getSiteName()))
				return false;

			if (!isStateTesting(w1, upperEventId1)
					&& !isStateTesting(w2, upperEventId2))
				continue;

			if (agents2.contains(state1.getAgentId())
					|| agents1.contains(state2.getAgentId()))
				return false;

			boolean cont1 = agents1.contains(state1.getAgentId());
			boolean cont2 = agents2.contains(state2.getAgentId());

			if (cont1 != cont2)
				return false;

			if (!cont1) {
				addExtensionData(w1, w2, state1, state2);
			}
			else{
				if(!checkCoincidence(state1.getAgentId(), state2.getAgentId())){
					return false;
				}
			}
			
		}

		return true;
	}

	protected boolean checkCoincidence(Long first, Long second) {
		long number1=-1;
		long number2=-1;
		for(int i=0;i<agents1.size();i++){
			if(agents1.get(i).equals(first)){
				number1=agents1.get(i);
				break;
			}
		}
		for(int i=0;i<agents2.size();i++){
			if(agents2.get(i).equals(second)){
				number2=agents2.get(i);
				break;
			}
		}
		if(number1==-1||number2==-1){
			throw new RuntimeException();
		}
		if(number1!=number2)
			return false;
		
		return true;
	}

	protected void addExtensionData(WireHashKey w1, WireHashKey w2,
			StateOfLink state1, StateOfLink state2) {
		agents1.add(state1.getAgentId());
		agents2.add(state2.getAgentId());

		ExtensionData extensionData = new ExtensionData();

		extensionData.wk1 = w1;
		extensionData.wk2 = new WireHashKey(state1.getAgentId(), state1
				.getSiteName(), TypeOfWire.LINK_STATE);
		extensionData.wk3 = w2;
		extensionData.wk4 = new WireHashKey(state2.getAgentId(), state2
				.getSiteName(), TypeOfWire.LINK_STATE);

		extensionLinks.add(extensionData);
	}

	private final StateOfLink getStateInSpace(AtomicEvent<?> event) {
		if (event != null) {
			StateOfLink state = null;

			switch (event.getType()) {
			case TEST:
				state = (StateOfLink) event.getState().getBeforeState();
				break;
			case MODIFICATION:
			case TEST_AND_MODIFICATION:
				state = (StateOfLink) event.getState().getAfterState();
				break;
			}
			if (state != null && !state.isFree())
				return state;
		}
		return null;
	}

	private final boolean isStateTesting(WireHashKey wKey, Long uppedId) {
		Entry<Long, AtomicEvent<?>> event = passport.getStorage()
				.getStorageWires().get(wKey).higherEntry(uppedId);
		if (event != null) {
			if (event.getValue().getType() == ActionOfAEvent.MODIFICATION
					&& event.getValue().getState().getBeforeState() == null)
				return false;
			return true;
		}
		return false;
	}
}
