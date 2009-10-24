package com.plectix.simulator.staticanalysis.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.staticanalysis.stories.storage.StateOfLink;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/*package*/ final class LinkStateIterator implements Iterator<StateOfLink> {
	private final Iterator<StateOfLink> stateIterator;
	private StateOfLink freeLinkState = new StateOfLink();

	public LinkStateIterator(WeakCompression weak) throws StoryStorageException {
		stateIterator = weak.getStorage().getInformationAboutWires().wireLinkStateIterator(
				weak.getEvent().getWireKey(weak.getWireIdx()));
	}

	@Override
	public final boolean hasNext() {
		return freeLinkState != null;
	}

	@Override
	public final StateOfLink next() {
		if (stateIterator.hasNext())
			return stateIterator.next();
		if (freeLinkState != null) {
			StateOfLink freeState = freeLinkState;
			freeLinkState = null;
			return freeState;
		}
		return null;
	}

	@Override
	public final void remove() {
	}
}
