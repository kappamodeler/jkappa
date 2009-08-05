package com.plectix.simulator.components.stories.storage.graphs;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

	private TreeMap<Long, Integer> depths;
	private TreeMap<Long, Long> nodeEventIdToNodeId;
	private TreeMap<Long, Long> introAgentIdtoCCId;
	private TreeMap<Long, String> introCCIdtoData;

	private Connections connections;

	public Connections getConnections2() {
		return connections;
	}

	private TreeMap<Long, Long> nodeIdToEventId;

	private CompressionPassport passport;

	public StoriesGraphs(CompressionPassport passport) {
		this.passport = passport;
		this.depths = new TreeMap<Long, Integer>();
		this.nodeEventIdToNodeId = new TreeMap<Long, Long>();
		this.introAgentIdtoCCId = new TreeMap<Long, Long>();
		this.introCCIdtoData = new TreeMap<Long, String>();
		this.nodeIdToEventId = new TreeMap<Long, Long>();
		this.connections = new Connections();
	}

	public Integer getEventDepth(long eventId) throws StoryStorageException {
		if (depths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		return depths.get(nodeEventIdToNodeId.get(eventId));
	}

	public Integer getIntroDepth(long introId) throws StoryStorageException {
		if (depths.isEmpty()) {
			nodeEventIdToNodeId = new TreeMap<Long, Long>();
		}
		if (!depths.containsKey(introId)) {
			return -1;
		}
		return depths.get(introId);
	}

	public void buildGraph() throws StoryStorageException {
		ICEvent intro = getEventByStepId(-1);
		fillNodeIdMap(fillIntroIdMap(intro));
		fillConnections();
		fillDepths();
		//filterNodes();
	}

	private void fillDepths() {
		
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

	private void filterNodes() {
		TreeMap<Long, Long> newMap = new TreeMap<Long, Long>();
		for (Iterator<Long> iterator = nodeIdToEventId.keySet().iterator(); iterator
				.hasNext();) {
			Long node = (Long) iterator.next();
			if (findNodeInConnection(node))
				newMap.put(node, nodeIdToEventId.get(node));
		}
		nodeIdToEventId = newMap;
	}

	private boolean findNodeInConnection(Long node) {
		for (Entry<Long, Set<Long>> entry : connections.getAdjacentEdges().entrySet()) {
			if (entry.getKey().equals(node))
				if (entry.getValue().size() == 1
						&& entry.getValue().contains(node))
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
		int graphDepth = depths.size() - 1;
		for (Long key : depths.keySet()) {
			inverseDepths.put(key, graphDepth - depths.get(key));
		}
		depths = null;
		depths = inverseDepths;
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
						if (!agentsMap.containsKey(wKey.getAgentId())) {
							agentsMap.put(wKey.getAgentId(),
									new LinkedHashSet<BoundedCouple>());
						}
						agentsMap.get(wKey.getAgentId()).add(
								new BoundedCouple(wKey.getAgentId(), wKey
										.getSiteId(), link.getAgentId(), link
										.getSiteId()));
					}
				}

			}
			
			
			fillInternalStates(introEvent, agentsMap);
			counter = fillCCDataMap(agentsMap);
			return counter;
		}
		return Long.valueOf(0);
	}

	private void fillInternalStates(ICEvent introEvent,
			TreeMap<Long, LinkedHashSet<BoundedCouple>> agentsMap)
			throws StoryStorageException {
		for (Iterator<WireHashKey> wireIt = introEvent.wireEventIterator(); wireIt
				.hasNext();) {
			WireHashKey wKey = (WireHashKey) wireIt.next();
			
			if (wKey.getTypeOfWire().equals(ETypeOfWire.INTERNAL_STATE)) {
				Integer internal = (Integer)introEvent
				.getAtomicEvent(wKey).getState().getAfterState();
				
				if (internal != null) {
					for (BoundedCouple couple : agentsMap.get(wKey.getAgentId())) {
						if (couple.getAgent1().equals(wKey.getAgentId()) && couple.getSite1().equals(wKey.getSiteId())){
							couple.setInternalState1(internal);
						}
						if (couple.getAgent2()!=null)
							if (couple.getAgent2().equals(wKey.getAgentId()) && couple.getSite2().equals(wKey.getSiteId())){
								couple.setInternalState2(internal);
							}
					}
				}
			}
		}
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
		Map<Long, Set<Site>> agentWithSites = new LinkedHashMap<Long, Set<Site>>();
		
		int counter = 0;
		
		for (BoundedCouple boundedCouple : set) {
			
			if (boundedCouple.getAgent2() != null) {
				boundedCouple.setLink(counter++);
				
				if (agentWithSites.containsKey(boundedCouple.getAgent1())) {
					
					agentWithSites.get(boundedCouple.getAgent1()).add(
							new Site(boundedCouple.getSite1(), boundedCouple.getLink(), boundedCouple.getInternalState1()));
					
				} else {
					agentWithSites.put(boundedCouple.getAgent1(),
							new LinkedHashSet<Site>());
					agentWithSites.get(boundedCouple.getAgent1()).add(
							new Site(boundedCouple.getSite1(), boundedCouple.getLink(), boundedCouple.getInternalState1()));
					
					
				}
				
				if (agentWithSites.containsKey(boundedCouple.getAgent2())) {
					agentWithSites.get(boundedCouple.getAgent2()).add(
							new Site(boundedCouple.getSite2(), boundedCouple.getLink(), boundedCouple.getInternalState2()));
					
				} else {
					agentWithSites.put(boundedCouple.getAgent2(),
							new LinkedHashSet<Site>());
					agentWithSites.get(boundedCouple.getAgent2()).add(
							new Site(boundedCouple.getSite2(), boundedCouple.getLink(), boundedCouple.getInternalState2()));
					
				}
				
			} else {
				if (!agentWithSites.containsKey(boundedCouple.getAgent1())){
					agentWithSites.put(boundedCouple.getAgent1(),
							new LinkedHashSet<Site>());
				}
				agentWithSites.get(boundedCouple.getAgent1()).add(
						new Site(boundedCouple.getSite1(), boundedCouple.getInternalState1()));
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
			
			if (agentWithSites.get(agentId) != null) { //is it neccessary???
				
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

	private void fillConnections() throws StoryStorageException {

		for (IEventIterator iterator = passport.eventIterator(false); iterator
				.hasNext();) {
			long eventId = (long) iterator.next();
			if (eventId != -1) {
				fillConnections2(getEventByStepId(eventId));
			}
		}
	}

	private void fillConnections2(ICEvent event) {
		Long upperNode;
		// Set<Long> connectionSet = new LinkedHashSet<Long>();
		TreeMap<Long,Long> map = new TreeMap<Long, Long>();
		for (Iterator<WireHashKey> iterator = event.wireEventIterator(); iterator
				.hasNext();) {
			WireHashKey wkey = (WireHashKey) iterator.next();
			TreeMap<Long, AtomicEvent<?>> wire = passport.getStorage()
					.getStorageWires().get(wkey);
			if (!wire.get(event.getStepId()).getType().equals(
					EActionOfAEvent.MODIFICATION)) {

				upperNode = getUpperNode(wire, wkey, event.getStepId());

				if (upperNode != null) {
					map.put(upperNode, upperNode);
				}
			}
		}
		if (!map.isEmpty()){
			upperNode = map.lastKey();
			
			do{
				connections.addConnection(nodeEventIdToNodeId
						.get(event.getStepId()), upperNode);
				upperNode = map.lowerKey(upperNode);
			} while(upperNode!=null);
		}

	}

	private Long getUpperNode(TreeMap<Long, AtomicEvent<?>> wire,
			WireHashKey key, long eventId) {
		Long tmp = eventId;
		tmp = wire.lowerKey(tmp);

		if(tmp!=null){
			while (wire.lowerEntry(tmp) != null
					&& wire.get(tmp).getType().equals(EActionOfAEvent.TEST)) {
				tmp = wire.lowerKey(tmp);
			}
			// }
			if (wire.get(tmp) != null) {
				// if (wire.get(tmp).getType().equals(EActionOfAEvent.TEST))
				// return null;
				if (tmp != -1)
					return nodeEventIdToNodeId.get(tmp);
				else
					return introAgentIdtoCCId.get(key.getAgentId());
			}
		}
		return null;
	}

	public ICEvent getEventByStepId(long stepId) {
		if (!passport.getAllEventsByNumber().containsKey(stepId))
			return null;
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

	public CompressionPassport getPassport() {
		return passport;
	}

	public Long getEventIdByNodeId(Long id) {
		return nodeIdToEventId.get(id);
	}
}
