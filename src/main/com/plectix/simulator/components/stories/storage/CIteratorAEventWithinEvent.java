package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class CIteratorAEventWithinEvent implements IAtomicEventIterator {
	
	private List<AtomicEvent<?>> listOfEvent = null;
	private List<WireHashKey> listOfWires = null;
	private int value;
	private int size=0;
	

	public CIteratorAEventWithinEvent(Map<WireHashKey, AtomicEvent<?>> map){
		listOfEvent = new ArrayList<AtomicEvent<?>>();
		listOfWires = new ArrayList<WireHashKey>();
		value = -1;
		
		//should be some heuristic by size of wires		
		for (Map.Entry<WireHashKey, AtomicEvent<?>> entry : map.entrySet()){
			listOfEvent.add(entry.getValue());
			listOfWires.add(entry.getKey());
			
		}
//		//should be some heuristic by size of wires
//		for (AtomicEvent<?> a: map.values()){
//			listOfEvent.add(a);
//		}
//		//should be some heuristic by size of wires
//		for(WireHashKey wKey : map.keySet()){
//			listOfWires.add(wKey);
//		}
		
		size = listOfEvent.size();
	}	
	//optimize by type of list
	public CIteratorAEventWithinEvent(List<AtomicEvent<?>> list){
		//should be some heuristic by size of wires
		listOfEvent = list;
		value = -1;
		size = list.size();	
	}
	
	public AtomicEvent<?> value() {
		return listOfEvent.get(value);
	}

	public boolean hasNext() {
		return (value<size-1);
	}

	/**
	 * 
	 */
	public WireHashKey next() {
		value++;
		return listOfWires.get(value);
	}
	/**
	 * not implemented. Ask Nikita why.
	 */
	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
