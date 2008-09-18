package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservables;

public class CObservables implements IObservables {
	private List<CConnectedComponent> conCompList = new ArrayList<CConnectedComponent>();

	public CObservables() {
	}

	public CObservables(List<CConnectedComponent> list) {
		addConnectedComponents(list);
	}

	public List<CConnectedComponent> getConComp() {
		return conCompList;
	}

	public void addConnectedComponents(List<CConnectedComponent> list) {
		conCompList.addAll(list);
	}

}
