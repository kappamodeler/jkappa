package com.plectix.simulator.staticanalysis.stories.storage;

import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;

public final class AtomicEvent<E> {
	private ActionOfAEvent type;
	private final Event container;
	private AbstractState<E> state;

	public AtomicEvent(Event container, ActionOfAEvent type) {
		this.container = container;
		state = new AbstractState<E>();
		this.type = type;
	}

	final void setState(AbstractState<E> state) {
		this.state = state;
	}

	public final AbstractState<E> getState() {
		return state;
	}

	public final ActionOfAEvent getType() {
		return type;
	}

	public final Event getContainer() {
		return container;
	}

	public final void correctingType(ActionOfAEvent modification) {
		if (type == ActionOfAEvent.TEST_AND_MODIFICATION)
			return;
		if (type == ActionOfAEvent.TEST && modification == ActionOfAEvent.MODIFICATION)
			type = ActionOfAEvent.TEST_AND_MODIFICATION;
	}

	public final AtomicEvent<?> cloneWithBefore(Event container) {
		AtomicEvent<E> outAEvent = new AtomicEvent<E>(container,ActionOfAEvent.MODIFICATION);
		AbstractState<E> outState = new AbstractState<E>();
		outAEvent.setState(outState);
		outState.setAfterState(state.getBeforeState());
		outState.setBeforeState(null);
		return outAEvent;
	}
	
	public final AtomicEvent<?> clone(){
		AtomicEvent<E> outAEvent = new AtomicEvent<E>(container,type);
		AbstractState<E> outState = new AbstractState<E>();
		outAEvent.setState(outState);
		outState.setAfterState(state.getAfterState());
		outState.setBeforeState(state.getBeforeState());
		return outAEvent;

	}
	@Override
	public final String toString(){
		String answer = "";
		answer +="container = " +container.getStepId();
		answer +=" type =" + type;
		answer += " state =  "+ state.toString();
		return answer;
	}
}
