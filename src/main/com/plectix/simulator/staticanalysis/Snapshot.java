package com.plectix.simulator.staticanalysis;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.simulator.SimulationData;

/**
 * Class implements snapshots elements, which are used for saving information on solution state
 * in fixed time moments. 
 * @author avokhmin
 *
 */
public final class Snapshot {
	//TODO REMOVE UNNECESSARY FIELDS
	private int totalAgents;
	private int smollestConnectedComponent;
	private int largestConnectedComponent;
	private int uniqueConnectedComponent;
	private List<SnapshotElement> snapshotElements;
	private final double snapshotTime;
	private final SimulationData simulationData;

	/**
	 * Constructor. Initializes snapshot element using current simulation data.
	 * @param simulationData simulation data
	 * @param snapshotTime time of snapshot 
	 */
	public Snapshot(SimulationData simulationData, double snapshotTime) {
		this.snapshotTime = snapshotTime;
		this.simulationData = simulationData;
		totalAgents = 0;
		largestConnectedComponent = 0;
		smollestConnectedComponent = Integer.MAX_VALUE;
		uniqueConnectedComponent = 0;
		snapshotElements = new ArrayList<SnapshotElement>();

		SolutionInterface solution = simulationData.getKappaSystem().getSolution();
		for (SuperSubstance ss : solution.getSuperStorage().getComponents()) {
			addComponent(ss.getComponent(), ss.getQuantity());
		}
		for (ConnectedComponentInterface cc : solution.getStraightStorage().split()) {
			addComponent(cc, 1);
		}
		
		for (SnapshotElement se : snapshotElements)
			se.eraseConnectedComponent();
	}

	private final void addComponent(ConnectedComponentInterface component, long number) {
		int ccSize = component.getAgents().size();
		totalAgents += component.getAgents().size() * number;
		if (largestConnectedComponent < ccSize)
			largestConnectedComponent = ccSize;
		if (smollestConnectedComponent > ccSize)
			smollestConnectedComponent = ccSize;
		if (ccSize == 1)
			uniqueConnectedComponent++;
		boolean isAdd = false;
		for (SnapshotElement se : snapshotElements) {
			if (se.exists(component)) {
				isAdd = true;
				break;
			}
		}
		if (!isAdd) {
			snapshotElements.add(new SnapshotElement(component, number, simulationData.getSimulationArguments().isOcamlStyleNameingInUse()));	
		}
	}
	/**
	 * This method returns time, when this snapshot was done
	 * @return time when current snapshot was done
	 */
	public final double getSnapshotTime() {
		return snapshotTime;
	}
	
	/**
	 * This method returns all SnapshotElements.
	 */
	public final List<SnapshotElement> getSnapshotElements() {
		return snapshotElements;
	}
}
