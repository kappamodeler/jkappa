package com.plectix.simulator.components.stories.storage;

import com.plectix.simulator.components.stories.enums.EActionOfAEvent;

public class AtomicEvent<E> {
	private EActionOfAEvent type;
	private final CEvent container;
	private AState<E> state;

	public AtomicEvent(CEvent container, EActionOfAEvent type) {
		this.container = container;
		state = new AState<E>();
		this.type = type;
	}

	public void setState(AState<E> state) {
		this.state = state;
	}

	public AState<E> getState() {
		return state;
	}

	public void setType(EActionOfAEvent type) {
		this.type = type;
	}

	public EActionOfAEvent getType() {
		return type;
	}

	public CEvent getContainer() {
		return container;
	}

	public void correctingType(EActionOfAEvent modification) {
		if (type == EActionOfAEvent.TEST_AND_MODIFICATION)
			return;
		if (type == EActionOfAEvent.TEST && modification == EActionOfAEvent.MODIFICATION)
			type = EActionOfAEvent.TEST_AND_MODIFICATION;
	}

	public AtomicEvent<?> cloneWithBefore(CEvent container) {
		AtomicEvent<E> outAEvent = new AtomicEvent<E>(container,EActionOfAEvent.MODIFICATION);
		AState<E> outState = new AState<E>();
		outAEvent.setState(outState);
		outState.setAfterState(state.getBeforeState());
		outState.setBeforeState(null);
		return outAEvent;
	}
}
