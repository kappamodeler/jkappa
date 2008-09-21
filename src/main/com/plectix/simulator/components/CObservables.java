package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IObservables;

public class CObservables implements IObservables {
	private List<CConnectedComponent> connectedComponentList = new ArrayList<CConnectedComponent>();

	public CObservables() {
	}

	public CObservables(List<CConnectedComponent> list) {
		addConnectedComponents(list);
	}

	public final List<CConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<CConnectedComponent> list) {
		connectedComponentList.addAll(list);
	}

}
