package com.plectix.simulator.components.stories.newVersion;

public class AtomicEvent<E> {
	private ECheck type;
	private final CEvent container;
	private AState<E> state;

	public AtomicEvent(CEvent container, ECheck type) {
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

	public void setType(ECheck type) {
		this.type = type;
	}

	public ECheck getType() {
		return type;
	}

	public CEvent getContainer() {
		return container;
	}

	public boolean isCausing() {
		return false;
	}

	public void correctingType(ECheck modification) {
		if (type == ECheck.TEST_AND_MODIFICATION)
			return;
		if (type == ECheck.TEST && modification == ECheck.MODIFICATION)
			type = ECheck.TEST_AND_MODIFICATION;
	}
}
