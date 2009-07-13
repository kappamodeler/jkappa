package com.plectix.simulator.components.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.components.stories.storage.ICEvent;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;

class StrongCompression 
{
	private IWireStorage storage;
	private boolean downwards = true;
	private boolean swapTop = true;
	
	public StrongCompression (IWireStorage storage) 
	{
		this.storage = storage;
	}
	
	public void setDirection (boolean downwards)
	{
		this.downwards = downwards;
	}
	
	public void setSwapMode (boolean top)
	{
		this.swapTop = top;
	}
	
	public void process () throws StoryStorageException 
	{
		boolean improved = true;
		
		while (improved)
		{
			improved = false;
			
			IEventIterator eventIterator = storage.eventIterator(!downwards);
			
			while (eventIterator.hasNext() && !improved)
			{
				eventIterator.next();
				ICEvent event = eventIterator.value();
				
				Iterator<Integer> typeIterator = storage.agentTypeIterator();

				while (typeIterator.hasNext() && !improved)
				{
					Integer type = typeIterator.next();

					Iterator<Long> agentIterator1 = storage.agentIterator(type);

					while (agentIterator1.hasNext() && !improved)
					{
						Long agentId1 = agentIterator1.next();

						Iterator<Long> agentIterator2 = storage.agentIterator(type);

						while (agentIterator2.hasNext())
							if (agentIterator2.next() == agentId1)
								break;

						while (agentIterator2.hasNext() && !improved)
						{
							Long agentId2 = agentIterator2.next();

							// TODO: extension ...

							IWireStorage newStorage = storage.swapAgents(new Long[] {agentId1}, new Long[] {agentId2}, event.getStepId(), swapTop);

							WeakCompression weak = new WeakCompression(newStorage);

							weak.setBoundaryEvent(event, !swapTop);

							if (weak.process())
								improved = true;
						}
					}
				}
			}
		}
	}
}
