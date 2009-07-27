package com.plectix.simulator.components.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.components.stories.enums.EState;

class BoundSateIterator implements Iterator<EState>
{
	private EState curState = null;

	@Override
	public boolean hasNext() {
		return curState != EState.FREE_LINK_STATE;
	}

	@Override
	public EState next() {
		if (curState == null)
			curState = EState.BOUND_LINK_STATE;
		else
			curState = EState.FREE_LINK_STATE;
		return curState;
	}

	@Override
	public void remove() {
	}
}
