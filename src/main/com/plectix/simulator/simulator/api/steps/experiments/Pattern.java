package com.plectix.simulator.simulator.api.steps.experiments;

public interface Pattern<E> {
	public boolean matches(E object);

	public boolean matches(String string);
}
