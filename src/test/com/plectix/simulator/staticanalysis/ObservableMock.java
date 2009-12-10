package com.plectix.simulator.staticanalysis;

import java.util.List;

import com.plectix.simulator.interfaces.ObservableInterface;

public class ObservableMock extends ObservableConnectedComponent implements
		ObservableInterface {

	private long value;

	public ObservableMock(List<Agent> connectedAgents, String name,
			String line, int id, boolean isUnique) {
		super(connectedAgents, name, line, id, isUnique);
		// TODO Auto-generated constructor stub
	}

	@Override
	public final long getInjectionsWeight() {
		return value;
	}

	@Override
	public void updateLastValue() {
		lastInjectionsQuantity = getInjectionsWeight();
	}

	public void setValue(long value) {
		this.value = value;
	}

}
