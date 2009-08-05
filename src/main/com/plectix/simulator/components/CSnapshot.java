package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.solution.StraightStorage;
import com.plectix.simulator.components.solution.SuperStorage;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
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
	private final SimulationData simulationData;

	/**
	 * Constructor. Initializes snapshot element using current simulation data.
	 * @param simulationData simulation data
	 * @param snapshotTime time of snapshot 
	 */
	public CSnapshot(SimulationData simulationData,double snapshotTime) {
		this.snapshotTime = snapshotTime;
		this.simulationData = simulationData;
		totalAgents = 0;
		largestConnectedComponent = 0;
		smollestConnectedComponent = Integer.MAX_VALUE;
		uniqueConnectedComponent = 0;
		snapshotElements = new ArrayList<SnapshotElement>();

		ISolution solution = simulationData.getKappaSystem().getSolution();
		for (SuperSubstance ss : solution.getSuperStorage().getComponents()) {
			addComponent(ss.getComponent(), ss.getQuantity());
		}
		for (IConnectedComponent cc : solution.getStraightStorage().split()) {
			addComponent(cc, 1);
		}
		
		for (SnapshotElement se : snapshotElements)
			se.eraseConnectedComponent();
	}

	private void addComponent(IConnectedComponent cc, long number) {
		int ccSize = cc.getAgents().size();
		totalAgents += cc.getAgents().size() * number;
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
		if (!isAdd) {
			snapshotElements.add(new SnapshotElement(cc, number, simulationData.isOcamlStyleObsName()));	
		}
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
		return snapshotElements;
	}
}
