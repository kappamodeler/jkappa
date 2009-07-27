package com.plectix.simulator.components.stories.compressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.WireHashKey;

class StrongCompression 
{
	private CompressionPassport passport;
	private WeakCompression weak;
	
	//private boolean swapTop = false;
	
	private ArrayList<Long> agents1 = new ArrayList<Long>();
	private ArrayList<Long> agents2 = new ArrayList<Long>();
	
	public StrongCompression (CompressionPassport passport) 
	{
		this.passport = passport; 
		this.weak = new WeakCompression(passport.getStorage());
	}
	
	//public void setSwapMode (boolean top)
	//{
	//	this.swapTop = top;
	//}
	
	public void process () throws StoryStorageException 
	{
		boolean improved = true;
		
		weak.process();
		passport.removeEventWithMarkDelete();
		
		while (improved)
		{
			improved = false;
			
			IEventIterator eventIterator1 = passport.eventIterator(false);
			IEventIterator eventIterator2 = passport.eventIterator(true);
			
			if (eventIterator1.hasNext())
				eventIterator1.next();
			
			if (eventIterator2.hasNext())
				eventIterator2.next();
			
			while (eventIterator1.hasNext() && eventIterator2.hasNext())
			{
				if (eventIterator1.next() > eventIterator2.next())
					break;
				
				ICEvent event1 = eventIterator1.value();
				ICEvent event2 = eventIterator2.value();
				
				if (lookThroughPerturbations(event1, true) || (event1.getStepId() != event2.getStepId() && lookThroughPerturbations(event2, false)))
				{
					improved = true;
					break;
				}
			}
		}
	}
	
	private boolean lookThroughPerturbations (ICEvent event, boolean swapTop) throws StoryStorageException
	{
		Iterator<Integer> typeIterator = passport.agentTypeIterator();

		boolean improved = false;
		
		while (typeIterator.hasNext() && !improved)
		{
			Integer type = typeIterator.next();

			Iterator<Long> agentIterator1 = passport.agentIterator(type);

			while (agentIterator1.hasNext() && !improved)
			{
				Long agentId1 = agentIterator1.next();

				Iterator<Long> agentIterator2 = passport.agentIterator(type);

				while (agentIterator2.hasNext())
					if (agentIterator2.next() == agentId1)
						break;

				while (agentIterator2.hasNext() && !improved)
				{
					Long agentId2 = agentIterator2.next();

					if (!extendSwap(event, agentId1, agentId2))
						continue;

					ICEvent neiEvent = passport.swapAgents(agents1, agents2, event.getStepId(), swapTop);

					// TODO: remember swapped pairs
					if (weak.processInconsistent(event, neiEvent))
					{
						improved = true;
						passport.removeEventWithMarkDelete();
					} else
						passport.undoSwap();
				}
			}
		}
		
		return improved;
	}
	
	private boolean doesAgentMatter (ICEvent event, Long agentId)
	{
		ArrayList<WireHashKey> wires = passport.getAgentWires(agentId);
		
		for (WireHashKey wire: wires)
			if (event.containsWire(wire))
				return true;
		return false;
	}
	
	private boolean extendSwap (ICEvent event, Long agentId1, Long agentId2) throws StoryStorageException
	{
		int curPair = 0;
		
		agents1.clear();
		agents2.clear();
		
		agents1.add(agentId1);
		agents2.add(agentId2);
		
		if (!doesAgentMatter(event, agentId1) && !doesAgentMatter(event, agentId2))
			return false;
		
		while (curPair != agents1.size())
		{
			if (!extendPair(event, curPair))
				return false;
			curPair++;
		}

		return true;
	}

	private boolean extendPair (ICEvent event, int pairIdx) throws StoryStorageException
	{
		Long agentId1 = agents1.get(pairIdx);
		Long agentId2 = agents2.get(pairIdx);
		
		if (!passport.isAbleToSwap(agentId1, agentId2))
			return false;
		
		ArrayList<WireHashKey> wires1 = passport.getAgentWires(agentId1);
		ArrayList<WireHashKey> wires2 = passport.getAgentWires(agentId2);
		
		IWireStorage storage = passport.getStorage();
		
		for (WireHashKey w1: wires1)
		{
			WireHashKey w2 = null;
			
			if (w1.getTypeOfWire() != ETypeOfWire.LINK_STATE)
				continue;
			
			for (WireHashKey _w2: wires2)
			{
				if (_w2.getTypeOfWire() == ETypeOfWire.LINK_STATE && w1.getSiteId() == _w2.getSiteId())
				{
					w2 = _w2;
					break;
				}
			}
			
			if (w2 == null)
				throw new StoryStorageException ("extendPair: different agents");
			
			Entry<Long, AtomicEvent<?>> event1 = storage.getStorageWires().get(w1).ceilingEntry(event.getStepId());
			Entry<Long, AtomicEvent<?>> event2 = storage.getStorageWires().get(w2).ceilingEntry(event.getStepId());
			
			CStateOfLink state1 = getStateFromSwapArea(event1);
			CStateOfLink state2 = getStateFromSwapArea(event2);
			
			if (state1 == null && state2 == null)
				continue;
			
			if (state1 == null)
			{
				event1 = storage.getStorageWires().get(w1).floorEntry(event.getStepId());
				state1 = getStateOutsideSwapArea(event1);
			} else if (state2 == null)
			{
				event2 = storage.getStorageWires().get(w2).floorEntry(event.getStepId());
				state2 = getStateOutsideSwapArea(event2);
			}
			
			if (state1 == null || state2 == null)
				return false;
			
			if (passport.getAgentType(state1.getAgentId()) != passport.getAgentType(state2.getAgentId()))
				return false;
				
			if (state1.getSiteId() != state2.getSiteId())
				return false;
			
			if (agents2.contains(state1.getAgentId()) || agents1.contains(state2.getAgentId()))
				return false;
			
			boolean cont1 = agents1.contains(state1.getAgentId());
			boolean cont2 = agents2.contains(state2.getAgentId());
			
			if (cont1 != cont2)
				return false;
			
			if (!cont1)
			{
				agents1.add(state1.getAgentId());
				agents2.add(state2.getAgentId());
			}
		}
		
		return true;
	}
	
	private CStateOfLink getStateFromSwapArea (Entry<Long, AtomicEvent<?>> event)
	{
		if (event != null && event.getValue().getType() != EActionOfAEvent.MODIFICATION)
		{
			CStateOfLink state = (CStateOfLink) event.getValue().getState().getBeforeState();
			if (!state.isFree())
				return state;
		}
		return null;
	}

	private CStateOfLink getStateOutsideSwapArea (Entry<Long, AtomicEvent<?>> event)
	{
		if (event != null)
		{
			CStateOfLink state;
			
			if (event.getValue().getType() == EActionOfAEvent.TEST)
				state = (CStateOfLink) event.getValue().getState().getBeforeState();
			else
				state = (CStateOfLink) event.getValue().getState().getAfterState();
			if (state != null && !state.isFree())
				return state;
		}
		return null;
	}
}
