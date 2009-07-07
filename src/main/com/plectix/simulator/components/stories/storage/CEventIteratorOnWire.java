package com.plectix.simulator.components.stories.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class CEventIteratorOnWire implements IEventIterator {

	private WireHashKey parentWire;
	private TreeMap<Long, AtomicEvent<?>> wire;
	private Long currentKey;
	private boolean narrowDown;

	
	public CEventIteratorOnWire(TreeMap<Long, AtomicEvent<?>> map,WireHashKey wkey, Long first,
			boolean reverse){
		wire = map;
		currentKey = first;
		narrowDown = reverse;		
	}
	@Override
	public CEvent value() {
		return wire.get(currentKey).getContainer();
	}

	@Override
	public boolean hasNext() {
		if (narrowDown){
			return(wire.lowerKey(currentKey)!=null);
		}
		else{
			return(wire.higherKey(currentKey)!=null);
		}
	}

	@Override
	public Long next() {
		if (narrowDown){
			return wire.lowerKey(currentKey);
		}
		else{
			return wire.higherKey(currentKey);
		}
	}

	/**
	 * not implemented
	 */
	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
