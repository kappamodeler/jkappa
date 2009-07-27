package com.plectix.simulator.components.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.StoryStorageException;

class LinkStateIterator implements Iterator<CStateOfLink>
{
	private Iterator<CStateOfLink> stateIterator;
	private CStateOfLink freeLinkState = new CStateOfLink();
	
	public LinkStateIterator (WeakCompression weak) throws StoryStorageException
	{
		stateIterator = weak.getStorage().wireLinkStateIterator(weak.getEvent().getWireKey(weak.getWireIdx()));
	}
	
	@Override
	public boolean hasNext() {
		return freeLinkState != null;
	}

	@Override
	public CStateOfLink next() {
		if (stateIterator.hasNext())
			return stateIterator.next();
		if (freeLinkState != null)
		{
			CStateOfLink freeState = freeLinkState;
			freeLinkState = null;
			return freeState;
		}
		return null;
	}

	@Override
	public void remove() {
	}
}
