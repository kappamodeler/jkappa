package com.plectix.simulator.simulator.options;

public class SimulatorParameter<E> {
	private final E value;
	
	public SimulatorParameter(E value) {
		this.value = value;
	}
	
	public E getValue() {
		return value;
	}

	public Class<?> getType() {
		return value.getClass();
	}
}
