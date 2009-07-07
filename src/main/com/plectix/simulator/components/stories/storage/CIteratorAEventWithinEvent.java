package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class CIteratorAEventWithinEvent implements IAtomicEventIterator {
	
	private List<AtomicEvent> listOfEvent = null;
	private int value;
	

	public CIteratorAEventWithinEvent(HashMap<WireHashKey, AtomicEvent<?>> map){
		listOfEvent = new ArrayList<AtomicEvent>();
		value = 0;
		//should be some heuristic by size of wires
		for (AtomicEvent<?> a: map.values()){
			listOfEvent.add(a);
		}
		
		
		
	}
	
	@Override
	public AtomicEvent<?> value() {
		return listOfEvent.get(value);
	}

	@Override
	public boolean hasNext() {
		return (value<listOfEvent.size()-1);
	}

	/**
	 * not implemented
	 */
	@Override
	public WireHashKey next() {
		return null;
	}
	/**
	 * not implemented
	 */
	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
