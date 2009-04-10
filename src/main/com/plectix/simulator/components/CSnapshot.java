package com.plectix.simulator.components;

import java.util.*;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements snapshots elements, which are used for saving information on solution state
 * in fixed time moments. 
 * @author avokhmin
 *
 */
public final class CSnapshot {

	//TODO REMOVE UNNECESSARY FIELDS
	private int totalAgents;
	private int smollestConnectedComponent;
	private int largestConnectedComponent;
	private int uniqueConnectedComponent;
	private List<IConnectedComponent> ccList;
	private List<SnapshotElement> snapshotElements;
	private final double snapshotTime;

	/**
	 * Constructor. Initializes snapshot element using current simulation data.
	 * @param simulationData simulation data
	 * @param snapshotTime time of snapshot 
	 */
	public CSnapshot(SimulationData simulationData,double snapshotTime) {
		this.snapshotTime = snapshotTime;
		ccList = simulationData.getKappaSystem().getSolution().split();
		totalAgents = 0;
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
			se.eraseConnectedComponent();
	}

	/**
	 * This method returns time, when this snapshot was done
	 * @return time when current snapshot was done
	 */
	public double getSnapshotTime() {
		return snapshotTime;
	}
	
	/**
	 * This method returns all SnapshotElements.
	 */
	public final List<SnapshotElement> getSnapshotElements() {
		return Collections.unmodifiableList(snapshotElements);
	}
}
