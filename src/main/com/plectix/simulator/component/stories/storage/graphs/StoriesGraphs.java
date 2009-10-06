package com.plectix.simulator.component.stories.storage.graphs;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.component.stories.ActionOfAEvent;
import com.plectix.simulator.component.stories.TypeOfWire;
import com.plectix.simulator.component.stories.compressions.CompressionPassport;
import com.plectix.simulator.component.stories.storage.AtomicEvent;
import com.plectix.simulator.component.stories.storage.EventInterface;
import com.plectix.simulator.component.stories.storage.EventIteratorInterface;
import com.plectix.simulator.component.stories.storage.StateOfLink;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireHashKey;

public final class StoriesGraphs {

	private final TreeMap<Long, Long> introAgentsToComponents = new TreeMap<Long, Long>();
	private final TreeMap<Long, String> introComponentIdsData = new TreeMap<Long, String>();
	private TreeMap<Long, Long> nodeIdToEventId = new TreeMap<Long, Long>();
	private TreeMap<Long, Long> nodeEventIdToNodeId = new TreeMap<Long, Long>();
	private TreeMap<Long, Integer> depths = new TreeMap<Long, Integer>();
	private final Connections connections;
	private final CompressionPassport passport;

	public StoriesGraphs(CompressionPassport passport) {
		this.passport = passport;
		this.connections = new Connections();
	}

	public final int getEventDepth(long eventId) throws StoryStorageException {
		if (depths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		return depths.get(nodeEventIdToNodeId.get(eventId));
	}

	public final Connections getConnections2() {
		return connections;
	}

	public final int getIntroDepth(long introId) throws StoryStorageException {
		if (depths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		if (!depths.containsKey(introId)) {
			return -1;
		}
		return depths.get(introId);
	}

	public final void buildGraph() throws StoryStorageException {
		EventInterface intro = getEventByStepId(-1);
		fillNodeIdMap(initializeIntroIdMap(intro));
		fillConnections();
		fillDepths();
		// filterNodes();
	}

	private final void fillDepths() {

		TreeMap<Long, Set<Long>> myconnections = connections.getAdjacentEdges();
		Long tmpNode = myconnections.lastKey();
		depths.put(tmpNode, 0);
		do {
			for (Long leaf : myconnections.get(tmpNode)) {
				if (depths.containsKey(leaf)) {
					if (depths.get(leaf) < depths.get(tmpNode) + 1) {
						depths.put(leaf, depths.get(tmpNode) + 1);
					}
				} else {
					depths.put(leaf, depths.get(tmpNode) + 1);
				}
			}
			tmpNode = myconnections.lowerKey(tmpNode);
		} while (tmpNode != null);

		inverseDepths();
	}

	private final void inverseDepths() {
		TreeMap<Long, Integer> inverseDepths = new TreeMap<Long, Integer>();
		int graphDepth = depths.size() - 1;
		for (Long key : depths.keySet()) {
			inverseDepths.put(key, graphDepth - depths.get(key));
		}
		depths = null;
		depths = inverseDepths;
	}

	private final long initializeIntroIdMap(EventInterface introEvent)
			throws StoryStorageException {
		TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap = new TreeMap<Long, LinkedHashSet<BoundedCouple>>();
		if (introEvent != null) {
			long counter = 0;

			for (Iterator<WireHashKey> wireIt = introEvent.wireEventIterator(); wireIt
					.hasNext();) {
				WireHashKey wKey = (WireHashKey) wireIt.next();
				if (wKey.getTypeOfWire().equals(TypeOfWire.AGENT)) {
					if (!agentsMap.containsKey(wKey.getAgentId())) {
						agentsMap.put(wKey.getAgentId(),
								new LinkedHashSet<BoundedCouple>());
					}
				}
				if (wKey.getTypeOfWire().equals(TypeOfWire.LINK_STATE)) {
					StateOfLink link = (StateOfLink) introEvent.getAtomicEvent(
							wKey).getState().getAfterState();
					if (link != null) {
						if (!agentsMap.containsKey(wKey.getAgentId())) {
							agentsMap.put(wKey.getAgentId(),
									new LinkedHashSet<BoundedCouple>());
						}
						agentsMap.get(wKey.getAgentId()).add(
								new BoundedCouple(wKey.getAgentId(), wKey
										.getSiteName(), link.getAgentId(), link
										.getSiteName()));
					}
				}

			}

			initializeInternalStates(introEvent, agentsMap);
			counter = initializeComponentsData(agentsMap);
			return counter;
		}
		return Long.valueOf(0);
	}

	private final void initializeInternalStates(EventInterface introEvent,
			TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap)
			throws StoryStorageException {
		for (Iterator<WireHashKey> wireIt = introEvent.wireEventIterator(); wireIt
				.hasNext();) {
			WireHashKey wKey = (WireHashKey) wireIt.next();

			if (wKey.getTypeOfWire().equals(TypeOfWire.INTERNAL_STATE)) {
				String internal = (String) introEvent.getAtomicEvent(wKey)
						.getState().getAfterState();

				if (internal != null) {
					for (BoundedCouple couple : agentsMap
							.get(wKey.getAgentId())) {
						if (couple.getFirstAgentId() == wKey.getAgentId()
								&& couple.getFirstSite() == wKey.getSiteName()) {
							couple.setFirstInternalState(internal);
						}
						if (couple.getSecondAgentId() != Long.MIN_VALUE)
							if (couple.getSecondAgentId() == wKey.getAgentId()
									&& couple.getSecondSite().equals(wKey.getSiteName())) {
								couple.setSecondInternalState(internal);
							}
					}
				}
			}
		}
	}

	private final long initializeComponentsData(
			TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap) {

		long counter = 0;
		Map<Long, Set<BoundedCouple>> mapCC = new TreeMap<Long, Set<BoundedCouple>>();

		for (Entry<Long, LinkedHashSet<BoundedCouple>> entry : agentsMap
				.entrySet()) {
			Set<BoundedCouple> set = new LinkedHashSet<BoundedCouple>();

			if (!introAgentsToComponents.containsKey(entry.getKey())) {

				set = buildComponents(entry.getKey(), agentsMap, set);

				mapCC.put(counter, set);
				for (BoundedCouple couple : set) {
					introAgentsToComponents.put(couple.getFirstAgentId(), counter);
					nodeIdToEventId.put(counter, counter);
					if (couple.getSecondAgentId() != Long.MIN_VALUE) {
						introAgentsToComponents.put(couple.getSecondAgentId(), counter);
						nodeIdToEventId.put(counter, counter);
					}
				}
				counter++;

			}
		}

		for (Long id : mapCC.keySet()) {

			if (mapCC.get(id).size() > 0) {
				introComponentIdsData.put(id, getText(mapCC.get(id)));
			}
		}

		return counter;
	}

	private final String getText(Set<BoundedCouple> set) {

		StringBuffer text = new StringBuffer();
		Map<Long, Set<Site>> agentWithSites = new LinkedHashMap<Long, Set<Site>>();

		int counter = 0;

		for (BoundedCouple boundedCouple : set) {

			if (boundedCouple.getSecondAgentId() != Long.MIN_VALUE) {
				boundedCouple.setLink(counter++);

				if (agentWithSites.containsKey(boundedCouple.getFirstAgentId())) {

					agentWithSites.get(boundedCouple.getFirstAgentId()).add(
							new Site(boundedCouple.getFirstSite(), boundedCouple
									.getLink(), boundedCouple
									.getFirstInternalState()));

				} else {
					agentWithSites.put(boundedCouple.getFirstAgentId(),
							new LinkedHashSet<Site>());
					agentWithSites.get(boundedCouple.getFirstAgentId()).add(
							new Site(boundedCouple.getFirstSite(), boundedCouple
									.getLink(), boundedCouple
									.getFirstInternalState()));

				}

				if (agentWithSites.containsKey(boundedCouple.getSecondAgentId())) {
					agentWithSites.get(boundedCouple.getSecondAgentId()).add(
							new Site(boundedCouple.getSecondSite(), boundedCouple
									.getLink(), boundedCouple
									.getSecondInternalState()));

				} else {
					agentWithSites.put(boundedCouple.getSecondAgentId(),
							new LinkedHashSet<Site>());
					agentWithSites.get(boundedCouple.getSecondAgentId()).add(
							new Site(boundedCouple.getSecondSite(), boundedCouple
									.getLink(), boundedCouple
									.getSecondInternalState()));

				}

			} else {
				if (!agentWithSites.containsKey(boundedCouple.getFirstAgentId())) {
					agentWithSites.put(boundedCouple.getFirstAgentId(),
							new LinkedHashSet<Site>());
				}
				agentWithSites.get(boundedCouple.getFirstAgentId()).add(
						new Site(boundedCouple.getFirstSite(), boundedCouple
								.getFirstInternalState()));
			}
		}

		boolean agentFirst = true;

		for (Long agentId : agentWithSites.keySet()) {
			if (agentId == null)
				continue;

			if (agentFirst) {
				agentFirst = false;
			} else {
				text.append(", ");
			}
			String type = passport.getStorage().getStoriesAgentTypesStorage()
					.getType(passport.getStorage().getIteration(), agentId);
			text.append("" + type + "(");
			boolean siteFirst = true;

			if (agentWithSites.get(agentId) != null) { // is it neccessary???

				for (Site site : agentWithSites.get(agentId)) {
					if (siteFirst) {
						siteFirst = false;
					} else {
						text.append(", ");
					}
					text.append(site.toString());
				}
			}
			text.append(")");
		}

		return text.toString();
	}

	private final Set<BoundedCouple> buildComponents(Long key,
			TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap,
			Set<BoundedCouple> set) {
		boolean flag = false;

		// if (agentsMap.get(key).isEmpty()) {
		// set.add(new BoundedCouple(key));
		// return set;
		// }

		for (BoundedCouple couple : agentsMap.get(key)) {

			for (BoundedCouple boundedCouple : set) {
				if (boundedCouple.isSame(couple)
						|| boundedCouple.equals(couple)) {
					flag = true;
				}
			}

			if (!flag) {
				set.add(couple);
				if (couple.getSecondAgentId() != Long.MIN_VALUE)
					buildComponents(couple.getSecondAgentId(), agentsMap, set);
			}
		}

		return set;
	}

	private void fillNodeIdMap(long id) throws StoryStorageException {
		for (EventIteratorInterface iterator = passport.eventIterator(false); iterator
				.hasNext();) {
			long eventId = (long) iterator.next();
			if (eventId != -1) {
				nodeEventIdToNodeId.put(eventId, id);
				nodeIdToEventId.put(id, eventId);
				id++;
			}
		}

	}

	private final void fillConnections() throws StoryStorageException {
		for (EventIteratorInterface iterator = passport.eventIterator(false); iterator
				.hasNext();) {
			long eventId = (long) iterator.next();
			if (eventId != -1) {
				fillConnectionsForOneEvent(getEventByStepId(eventId));
			}
		}
	}

	private final void fillConnectionsForOneEvent(EventInterface event) {
		Long upperNode;
		// Set<Long> connectionSet = new LinkedHashSet<Long>();
		TreeMap<Long, Long> map = new TreeMap<Long, Long>();
		for (Iterator<WireHashKey> iterator = event.wireEventIterator(); iterator
				.hasNext();) {
			WireHashKey wkey = (WireHashKey) iterator.next();
			TreeMap<Long, AtomicEvent<?>> wire = passport.getStorage()
					.getStorageWires().get(wkey);
			if (!wire.get(event.getStepId()).getType().equals(
					ActionOfAEvent.MODIFICATION)) {

				upperNode = getUpperNode(wire, wkey, event.getStepId());

				if (upperNode != null) {
					map.put(upperNode, upperNode);
				}
			}
		}
		if (!map.isEmpty()) {
			upperNode = map.lastKey();

			do {
				connections.addConnection(nodeEventIdToNodeId.get(event
						.getStepId()), upperNode);
				upperNode = map.lowerKey(upperNode);
			} while (upperNode != null);
		}

	}

	private final Long getUpperNode(TreeMap<Long, AtomicEvent<?>> wire,
			WireHashKey key, long eventId) {
		Long tmp = eventId;
		tmp = wire.lowerKey(tmp);

		if (tmp != null) {
			while (wire.lowerEntry(tmp) != null
					&& wire.get(tmp).getType().equals(ActionOfAEvent.TEST)) {
				tmp = wire.lowerKey(tmp);
			}
			// }
			if (wire.get(tmp) != null) {
				// if (wire.get(tmp).getType().equals(EActionOfAEvent.TEST))
				// return null;
				if (tmp != -1)
					return nodeEventIdToNodeId.get(tmp);
				else
					return introAgentsToComponents.get(key.getAgentId());
			}
		}
		return null;
	}

	public final EventInterface getEventByStepId(long stepId) {
		if (!passport.getAllEventsByNumber().containsKey(stepId))
			return null;
		return passport.getAllEventsByNumber().get(stepId).getContainer();
	}

	public final long getNodeIdByEventId(long eventId) {
		if (!nodeEventIdToNodeId.containsKey(eventId))
			return -1;
		return nodeEventIdToNodeId.get(eventId);
	}

	public final TreeMap<Long, String> getIntroComponentIdtoData() {
		return introComponentIdsData;
	}

	public final CompressionPassport getPassport() {
		return passport;
	}

	public final long getEventIdByNodeId(long id) {
		return nodeIdToEventId.get(id);
	}
}
