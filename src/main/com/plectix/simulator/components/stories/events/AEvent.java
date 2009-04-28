package com.plectix.simulator.components.stories.events;

public abstract class AEvent {
	public final static byte EMPTY_SETP = -1;
	public final static byte EMPTY = -1;

	private final long stepId;

	public AEvent(long stepId) {
		this.stepId = stepId;
	}

	public long getStepId() {
		return stepId;
	}

	abstract public boolean isCausing();
}
