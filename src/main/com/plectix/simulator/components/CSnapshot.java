package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements snapshots elements.<br>
 * Creates image of current state Solution.
 * @author avokhmin
 *
 */
public final class CSnapshot {

	private int totalAgents;
	private final int totalConnectedComponents;
	private int smollestConnectedComponent;
	private int largestConnectedComponent;
	private int uniqueConnectedComponent;
	private List<IConnectedComponent> ccList;
	private List<SnapshotElement> snapshotElements;
	private double snapshotTime;

	/**
	 * this method returns time, when did do image of current state Solution.
	 */
	public double getSnapshotTime() {
		return snapshotTime;
	}

	/**
	 * Default constructor.<br>
	 * Creates image of current state Solution.
	 * @param simulationData given simulation data
	 * @param snapshotTime given time, when creating image. 
	 */
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

	/**
	 * This method returns all SnapshotElements. Uses for create xml.
	 */
	public final List<SnapshotElement> getSnapshotElements() {
		return Collections.unmodifiableList(snapshotElements);
	}

	/**
	 * This method returns total count of agents. 
	 */
	public final int getTotalAgents() {
		return totalAgents;
	}

	/**
	 * This method returns total connection components.
	 */
	public final int getTotalConnectedComponents() {
		return totalConnectedComponents;
	}

	/**
	 * This method returns connected components, witch has smallest agents number.
	 */
	public final int getSmallestConnectedComponent() {
		return smollestConnectedComponent;
	}

	/**
	 * This method returns connected components, witch has largest agents number.
	 */
	public final int getLargestConnectedComponent() {
		return largestConnectedComponent;
	}

	/**
	 * This method returns unique connected components.
	 */
	public final int getUniqueConnectedComponent() {
		return uniqueConnectedComponent;
	}
}
