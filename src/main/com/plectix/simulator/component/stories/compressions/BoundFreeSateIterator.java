package com.plectix.simulator.component.stories.compressions;

import java.util.Iterator;

import com.plectix.simulator.component.stories.State;

/*package*/ final class BoundSateIterator implements Iterator<State> {
	private State curState = null;

	@Override
	public final boolean hasNext() {
		return curState != State.FREE_LINK_STATE;
	}

	@Override
	public final State next() {
		if (curState == null)
			curState = State.BOUND_LINK_STATE;
		else
			curState = State.FREE_LINK_STATE;
		return curState;
	}

	@Override
	public final void remove() {
	}
}
