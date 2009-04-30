package com.plectix.simulator.components.stories.events;

public abstract class AEvent {
	private final long stepId;

	public AEvent(long stepId) {
		this.stepId = stepId;
	}

	public long getStepId() {
		return stepId;
	}

	abstract public boolean isCausing();
}
