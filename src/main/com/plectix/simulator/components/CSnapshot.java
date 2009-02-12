package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;

public final class CSnapshot {

	private int totalAgents;
	private final int totalConnectedComponents;
	private int smollestConnectedComponent;
	private int largestConnectedComponent;
	private int uniqueConnectedComponent;
	private List<IConnectedComponent> ccList;
	private List<SnapshotElement> snapshotElements;
	private double snapshotTime;

	public double getSnapshotTime() {
		return snapshotTime;
	}

	public void setSnapshotTime(double snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	public CSnapshot(SimulationData simulationData,double snapshotTime) {
		this.snapshotTime = snapshotTime;
		ccList = simulationData.getKappaSystem().getSolution().split();
		totalAgents = 0;
		totalConnectedComponents = ccList.size();
		largestConnectedComponent = 0;
		smollestConnectedComponent = Integer.MAX_VALUE;
		uniqueConnectedComponent = 0;
		snapshotElements = new ArrayList<SnapshotElement>();

		for (IConnectedComponent cc : ccList) {
			int ccSize = cc.getAgents().size();
			totalAgents += cc.getAgents().size();
			if (largestConnectedComponent < ccSize)
				largestConnectedComponent = ccSize;
			if (smollestConnectedComponent > ccSize)
				smollestConnectedComponent = ccSize;
			if (ccSize == 1)
				uniqueConnectedComponent++;
			boolean isAdd = false;
			for (SnapshotElement se : snapshotElements) {
				if (se.exists(cc)) {
					isAdd = true;
					break;
				}
			}
			if (!isAdd)
				snapshotElements.add(new SnapshotElement(cc, simulationData.isOcamlStyleObsName()));
		}

		ccList.clear();
		for (SnapshotElement se : snapshotElements)
			se.setConnectedComponent(null);
	}

	public final List<SnapshotElement> getSnapshotElements() {
		return Collections.unmodifiableList(snapshotElements);
	}

	public final int getTotalAgents() {
		return totalAgents;
	}

	public final int getTotalConnectedComponents() {
		return totalConnectedComponents;
	}

	public final int getSmollestConnectedComponent() {
		return smollestConnectedComponent;
	}

	public final int getLargestConnectedComponent() {
		return largestConnectedComponent;
	}

	public final int getUniqueConnectedComponent() {
		return uniqueConnectedComponent;
	}
}
