package com.plectix.simulator.components.stories.storage.graphs;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.compressions.CompressionPassport;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.simulator.ThreadLocalData;

public class StoriesGraphs {

	private TreeMap<Long, Integer> nodeDepths;
	private TreeMap<Long, Integer> depths;
	private TreeMap<Long, Long> nodeEventIdToNodeId;
	private TreeMap<Long, LinkedHashSet<Long>> connections;
	private TreeMap<Long, Long> introAgentIdtoCCId;
	private TreeMap<Long, String> introCCIdtoData;

	private TreeMap<Long, Long> nodeIdToEventId;

	private CompressionPassport passport;

	public StoriesGraphs(CompressionPassport passport) {
		this.passport = passport;
		this.nodeDepths = new TreeMap<Long, Integer>();
		this.nodeEventIdToNodeId = new TreeMap<Long, Long>();
		this.connections = new TreeMap<Long, LinkedHashSet<Long>>();
		this.introAgentIdtoCCId = new TreeMap<Long, Long>();
		this.introCCIdtoData = new TreeMap<Long, String>();
		this.nodeIdToEventId = new TreeMap<Long, Long>();
	}

	public Integer getEventDepth(long eventId) throws StoryStorageException {
		if (nodeDepths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		return nodeDepths.get(nodeEventIdToNodeId.get(eventId));
	}

	public Integer getIntroDepth(long introId) throws StoryStorageException {
		if (nodeDepths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		if (!nodeDepths.containsKey(introId)) {
			return -1;
		}
		return nodeDepths.get(introId);
	}

	public void buildGraph() throws StoryStorageException {
		fillNodeIdMap(fillIntroIdMap(getEventByStepId(-1)));
		Integer depth = 0;
		
		
		for (IEventIterator iterator = passport.eventIterator(true); iterator
				.hasNext();) {
			long eventId = (long) iterator.next();
			if (nodeDepths.isEmpty()) {
				nodeDepths.put(nodeEventIdToNodeId.get(eventId), depth++);
			} else {
				if (eventId != -1) {
					for (Iterator<WireHashKey> wireIt = getEventByStepId(
							eventId).wireEventIterator(); wireIt.hasNext();) {
						WireHashKey wKey = (WireHashKey) wireIt.next();
						TreeMap<Long, AtomicEvent<?>> wire = passport
								.getStorage().getStorageWires().get(wKey);
						if (depth < getNearbyDepth(wire, eventId))
							depth = getNearbyDepth(wire, eventId);
					}
					nodeDepths.put(nodeEventIdToNodeId.get(eventId), depth);
				} else {
					Integer introDepth = 0;
					Long tmpCCId = null;
					for (Iterator<WireHashKey> wireIt = getEventByStepId(
							eventId).wireEventIterator(); wireIt.hasNext();) {
						WireHashKey wKey = (WireHashKey) wireIt.next();
						tmpCCId = introAgentIdtoCCId.get(wKey.getAgentId());
						if (!nodeDepths.containsKey(tmpCCId)) {
							for (Iterator<WireHashKey> wireByCC = getEventByStepId(
									eventId).wireEventIterator(); wireByCC
									.hasNext();) {
								WireHashKey w = (WireHashKey) wireByCC.next();

								TreeMap<Long, AtomicEvent<?>> wire = passport
										.getStorage().getStorageWires().get(w);
								if (introAgentIdtoCCId.get(w.getAgentId()) == tmpCCId) {
									if (introDepth < getNearbyDepth(wire,
											eventId)) {
										introDepth = getNearbyDepth(wire,
												eventId);
									}
								}
							}
							nodeDepths.put(tmpCCId, introDepth);
						}
					}
				}
			}
			if (eventId != -1) {
				fillConnections(getEventByStepId(eventId));
			}
		}
		fillDepths();
		inverseDepths();
		filterNodes();
	}

	private void fillDepths() {
		depths = new TreeMap<Long, Integer>();

		Long tmpNode = connections.lastKey();
		depths.put(tmpNode, 0);
		do{
			for (Long leaf : connections.get(tmpNode)) {
				if (depths.containsKey(leaf)){
					if (depths.get(leaf)< depths.get(tmpNode) + 1){
						depths.put(leaf, depths.get(tmpNode) + 1);
					}
				} else {
					depths.put(leaf, depths.get(tmpNode) + 1);
				}
			}
			tmpNode = connections.lowerKey(tmpNode);
		} while(tmpNode != null);
		
		
		nodeDepths = depths;
		
	}

	private void filterNodes() {
		TreeMap<Long, Long> newMap = new TreeMap<Long, Long>();
		for (Iterator iterator = nodeIdToEventId.keySet().iterator(); iterator
				.hasNext();) {
			Long node = (Long) iterator.next();
			if (findNodeInConnection(node))
				newMap.put(node, nodeIdToEventId.get(node));

		}
		nodeIdToEventId = newMap;
	}

	private boolean findNodeInConnection(Long node) {
		for (Entry<Long, LinkedHashSet<Long>> entry : connections.entrySet()) {
			if (entry.getKey().equals(node))
				if (entry.getValue().size() == 1 && entry.getValue().contains(node))
					return false;
				else
					return true;
			
			for (Long n : entry.getValue()) {
				if (n.equals(node))
					return true;
			}
		}
		return false;
	}

	private void inverseDepths() {

		TreeMap<Long, Integer> inverseDepths = new TreeMap<Long, Integer>();
		int graphDepth = nodeDepths.size() - 1;
		for (Long key : nodeDepths.keySet()) {
			inverseDepths.put(key, graphDepth - nodeDepths.get(key));
		}
		nodeDepths = null;
		nodeDepths = inverseDepths;
	}

	private Long fillIntroIdMap(ICEvent introEvent)
			throws StoryStorageException {
		TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap = new TreeMap<Long, LinkedHashSet<BoundedCouple>>();
		if (introEvent != null) {
			long counter = 0;

			for (Iterator<WireHashKey> wireIt = introEvent.wireEventIterator(); wireIt
					.hasNext();) {
				WireHashKey wKey = (WireHashKey) wireIt.next();
				if (wKey.getTypeOfWire().equals(ETypeOfWire.AGENT)) {
					if (!agentsMap.containsKey(wKey.getAgentId())) {
						agentsMap.put(wKey.getAgentId(),
								new LinkedHashSet<BoundedCouple>());
					}
				}
				if (wKey.getTypeOfWire().equals(ETypeOfWire.LINK_STATE)) {
					CStateOfLink link = (CStateOfLink) introEvent
							.getAtomicEvent(wKey).getState().getAfterState();
					if (link != null) {
						// if (link.getAgentId() != -1 && link.getSiteId() !=
						// -1){
						if (!agentsMap.containsKey(wKey.getAgentId())) {
							agentsMap.put(wKey.getAgentId(),
									new LinkedHashSet<BoundedCouple>());
						}
						agentsMap.get(wKey.getAgentId()).add(
								new BoundedCouple(wKey.getAgentId(), wKey
										.getSiteId(), link.getAgentId(), link
										.getSiteId()));
						// }
					}
				}
			}
			counter = fillCCDataMap(agentsMap);
			return counter;
		}
		return Long.valueOf(0);
	}

	private Long fillCCDataMap(
			TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap) {

		long counter = 0;
		Map<Long, Set<BoundedCouple>> mapCC = new TreeMap<Long, Set<BoundedCouple>>();

		for (Entry<Long, LinkedHashSet<BoundedCouple>> entry : agentsMap
				.entrySet()) {
			Set<BoundedCouple> set = new LinkedHashSet<BoundedCouple>();

			if (!introAgentIdtoCCId.containsKey(entry.getKey())) {

				set = buildCC(entry.getKey(), agentsMap, set);

				mapCC.put(counter, set);
				for (BoundedCouple couple : set) {
					introAgentIdtoCCId.put(couple.getAgent1(), counter);
					nodeIdToEventId.put(counter, counter);
					if (couple.getAgent2() != null) {
						introAgentIdtoCCId.put(couple.getAgent2(), counter);
						nodeIdToEventId.put(counter, counter);
					} else {
						// Integer type = ThreadLocalData.getTypeById().getType(
						// passport.getStorage().getIteration(),
						// couple.getAgent1());
						// introCCIdtoData.put(counter, ThreadLocalData
						// .getNameDictionary().getName(type));
					}
				}
				counter++;

			}
		}

		for (Long id : mapCC.keySet()) {

			if (mapCC.get(id).size() > 0) {
				introCCIdtoData.put(id, getText(mapCC.get(id)));
			}
		}

		return counter;
	}

	private String getText(Set<BoundedCouple> set) {

		StringBuffer text = new StringBuffer();
		Map<Long, Map<Integer, Integer>> agentWithSites = new LinkedHashMap<Long, Map<Integer, Integer>>();

		int counter = 0;
		for (BoundedCouple boundedCouple : set) {
			if (boundedCouple.getAgent2() != null) {

				boundedCouple.setLink(counter++);
				if (agentWithSites.containsKey(boundedCouple.getAgent1())) {
					agentWithSites.get(boundedCouple.getAgent1()).put(
							boundedCouple.getSite1(), boundedCouple.getLink());
				} else {
					agentWithSites.put(boundedCouple.getAgent1(),
							new LinkedHashMap<Integer, Integer>());
					agentWithSites.get(boundedCouple.getAgent1()).put(
							boundedCouple.getSite1(), boundedCouple.getLink());
				}

				if (agentWithSites.containsKey(boundedCouple.getAgent2())) {
					agentWithSites.get(boundedCouple.getAgent2()).put(
							boundedCouple.getSite2(), boundedCouple.getLink());
				} else {
					agentWithSites.put(boundedCouple.getAgent2(),
							new LinkedHashMap<Integer, Integer>());
					agentWithSites.get(boundedCouple.getAgent2()).put(
							boundedCouple.getSite2(), boundedCouple.getLink());
				}
			} else {
				if (!agentWithSites.containsKey(boundedCouple.getAgent1()))
					agentWithSites.put(boundedCouple.getAgent1(),
							new LinkedHashMap<Integer, Integer>());
				agentWithSites.get(boundedCouple.getAgent1()).put(
						boundedCouple.getSite1(), null);
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
			Integer type = ThreadLocalData.getTypeById().getType(
					passport.getStorage().getIteration(), agentId);
			text.append("" + ThreadLocalData.getNameDictionary().getName(type)
					+ "(");
			boolean siteFirst = true;
			if (agentWithSites.get(agentId) != null) {
				for (Entry<Integer, Integer> siteId : agentWithSites.get(
						agentId).entrySet()) {
					if (siteFirst) {
						siteFirst = false;
					} else {
						text.append(", ");
					}
					text.append(ThreadLocalData.getNameDictionary().getName(
							siteId.getKey()));

					if (siteId.getValue() != null)
						text.append("!" + siteId.getValue());
				}
			}
			text.append(")");

		}

		return text.toString();
	}

	private Set<BoundedCouple> buildCC(Long key,
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
				if (couple.getAgent2() != null)
					buildCC(couple.getAgent2(), agentsMap, set);
			}
		}

		return set;
	}

	private void fillNodeIdMap(Long id) throws StoryStorageException {
		for (IEventIterator iterator = passport.eventIterator(false); iterator
				.hasNext();) {
			long eventId = (long) iterator.next();
			if (eventId != -1) {
				nodeEventIdToNodeId.put(eventId, id);
				nodeIdToEventId.put(id, eventId);
				id++;
			}
		}

	}

	private void fillConnections(ICEvent event) {
		LinkedHashSet<Long> connectionSet = new LinkedHashSet<Long>();
		Long upperNodeId;
		Map<Long, Integer> agentsMap = new LinkedHashMap<Long, Integer>();
		int ccCounter = 0;
		Long tmpAgent;

		for (Iterator<WireHashKey> wireIt = event.wireEventIterator(); wireIt
				.hasNext();) {
			WireHashKey wKey = (WireHashKey) wireIt.next();
			TreeMap<Long, AtomicEvent<?>> wire = passport.getStorage()
					.getStorageWires().get(wKey);
			tmpAgent = wKey.getAgentId();
			agentsMap.put(wKey.getAgentId(), ccCounter);
			for (Iterator<WireHashKey> iter = event.wireEventIterator(); iter
					.hasNext();) {
				WireHashKey w = (WireHashKey) iter.next();
				if (w.getAgentId() != tmpAgent)
					continue;
				
				
			}

			if (!wire.get(event.getStepId()).getType().equals(
					EActionOfAEvent.MODIFICATION)) {

				upperNodeId = getUpperNode(wire, wKey, event.getStepId());
				if (upperNodeId != null)
					connectionSet.add(upperNodeId);
			}
			ccCounter++;
		}

		connections.put(nodeEventIdToNodeId.get(event.getStepId()),
				connectionSet);
	}
	private Long getUpperNode(TreeMap<Long, AtomicEvent<?>> wire,
			WireHashKey key, long eventId) {
		long tmp = eventId;
		tmp = wire.lowerKey(tmp);

//		while (wire.lowerEntry(tmp) != null
//				&& wire.get(tmp).getType().equals(EActionOfAEvent.TEST) )
//				 {
//			tmp = wire.lowerKey(tmp);
//		}

		if (wire.get(tmp) != null) {
			if (wire.get(tmp).getType().equals(EActionOfAEvent.TEST))
				return null;
			if (tmp != -1)
				return nodeEventIdToNodeId.get(tmp);
			else
				return introAgentIdtoCCId.get(key.getAgentId());
		}
		return null;
	}
	private int getNearbyDepth(TreeMap<Long, AtomicEvent<?>> wire, long stepId) {

		if (wire.higherKey(stepId) != null)
			return nodeDepths.get(nodeEventIdToNodeId.get(wire.higherKey(stepId))) + 1;
		else
			return 0;
	}

	public ICEvent getEventByStepId(long stepId) {
		return passport.getAllEventsByNumber().get(stepId).getContainer();
	}

	public long getNodeIdByEventId(long eventId) {
		if (!nodeEventIdToNodeId.containsKey(eventId))
			return -1;
		return nodeEventIdToNodeId.get(eventId);
	}

	public TreeMap<Long, String> getIntroCCIdtoData() {
		return introCCIdtoData;
	}

	public TreeMap<Long, LinkedHashSet<Long>> getConnections() {
		return connections;
	}

	public CompressionPassport getPassport() {
		return passport;
	}

	public Long getEventIdByNodeId(Long id) {
		return nodeIdToEventId.get(id);
	}
}
