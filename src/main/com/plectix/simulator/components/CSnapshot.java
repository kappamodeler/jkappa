package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulatorManager;

public class CSnapshot {

	private CSolution solution;

	private int totalAgents;

	public int getTotalAgents() {
		return totalAgents;
	}

	public int getTotalConnectedComponents() {
		return totalConnectedComponents;
	}

	public int getSmollestConnectedComponent() {
		return smollestConnectedComponent;
	}

	public int getLargestConnectedComponent() {
		return largestConnectedComponent;
	}

	public int getUniqueConnectedComponent() {
		return uniqueConnectedComponent;
	}

	private int totalConnectedComponents;

	private int smollestConnectedComponent;

	private int largestConnectedComponent;

	private int uniqueConnectedComponent;

	private List<IConnectedComponent> ccList;

	private List<SnapshotElement> snapshotElements;

	public List<SnapshotElement> getSnapshotElements() {
		return snapshotElements;
	}

	public class SnapshotElement {
		private int count;
		private IConnectedComponent cc;
		private String ccName;

		public int getCount() {
			return count;
		}

		public String getCcName() {
			return ccName;
		}

		public SnapshotElement(IConnectedComponent cc2) {
			count = 1;
			this.cc = cc2;
			this.cc.initSpanningTreeMap();
			parseCC();
		}

		private void parseCC() {
			ccName = SimulationMain.getSimulationManager().printPartRule(cc,0);
		}

		public boolean exists(IConnectedComponent ccEx) {
			if (cc == ccEx)
				return true;
			ccEx.initSpanningTreeMap();
			//if (cc.isAutomorphism(ccEx.getAgents().get(0))) {
			if (cc.unify(ccEx.getAgents().get(0)) && ccEx.unify(cc.getAgents().get(0))) {
				count++;
				return true;
			}

			return false;
		}

	}

	public CSnapshot(CSolution solution) {
		ccList = solution.split();
		init();
		clean();
	}

	private void clean() {
		ccList.clear();

		for (SnapshotElement se : snapshotElements)
			se.cc = null;
	}

	private void init() {
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
				snapshotElements.add(new SnapshotElement(cc));
		}
	}

}
