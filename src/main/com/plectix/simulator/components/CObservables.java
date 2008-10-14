package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CObservables {
	private List<ObservablesConnectedComponent> connectedComponentList = new ArrayList<ObservablesConnectedComponent>();
	public static List<Double> countTimeList = new ArrayList<Double>();

	public static List<Double> getCountTimeList() {
		return countTimeList;
	}

	public final void PrintObsCount() {
		for (ObservablesConnectedComponent cc : connectedComponentList) {
			System.out.println(cc.getInjectionsList().size());
		}
	}

	public final void calculateObs(Double time) {
		countTimeList.add(time);
		for (ObservablesConnectedComponent cc : connectedComponentList) {
			cc.calculateInjection();
		}
	}

	public CObservables() {
	}

	public final List<ObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<CConnectedComponent> list,
			String name, String line) {
		for (CConnectedComponent component : list) {
			ObservablesConnectedComponent oCC = new ObservablesConnectedComponent(
					component.getAgents(), name, line);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
		}
	}

	public final void checkAutomorphisms() {
		for (ObservablesConnectedComponent oCC : connectedComponentList) {
			if (!oCC.isChecked) {
				for (ObservablesConnectedComponent oCCIn : connectedComponentList) {
					if (!(oCC == oCCIn) && !(oCCIn.isChecked)) {
						if (oCC.isAutomorphism(oCCIn.getAgents().get(0))) {
							int index = connectedComponentList.indexOf(oCC);
							oCC.addAutomorphicObservables(index);
							oCCIn.isChecked = true;
							oCCIn.setMainAutomorphismNumber(index);
						}
					}
				}
			}
			oCC.isChecked = true;
		}
	}

	public class ObservablesConnectedComponent extends CConnectedComponent {
		private String name;
		private String line;
		private List<Integer> automorphicObservables;
		private boolean isChecked = false;
		public static final int NO_INDEX = -1;
		private int mainAutomorphismNumber = NO_INDEX;

		public int getMainAutomorphismNumber() {
			return mainAutomorphismNumber;
		}

		public void setMainAutomorphismNumber(int mainAutomorphismNumber) {
			this.mainAutomorphismNumber = mainAutomorphismNumber;
		}

		public List<Integer> getAutomorphicObservables() {
			return automorphicObservables;
		}

		public void addAutomorphicObservables(int automorphicObservable) {
			this.automorphicObservables.add(automorphicObservable);
		}

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}

		public String getLine() {
			return line;
		}

		private List<Integer> countList = new ArrayList<Integer>();

		public List<Integer> getCountList() {
			return countList;
		}

		public final void calculateInjection() {
			countList.add(getInjectionsList().size());
		}

		public ObservablesConnectedComponent(List<CAgent> connectedAgents,
				String name, String line) {
			super(connectedAgents);
			this.name = name;
			this.line = line;
			this.automorphicObservables = new ArrayList<Integer>();
		}

		public String getName() {
			return name;
		}
	}

}
