package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IObservablesComponent;

public class CObservables {
	private List<ObservablesConnectedComponent> connectedComponentList = new ArrayList<ObservablesConnectedComponent>();
	private List<IObservablesComponent> componentList = new ArrayList<IObservablesComponent>();
	public static List<Double> countTimeList = new ArrayList<Double>();

	private double timeNext;
	private double timeSampleMin;

	public final double getTimeSampleMin() {
		return timeSampleMin;
	}

	public static void setCountTimeList(List<Double> countTimeList) {
		CObservables.countTimeList = countTimeList;
	}

	public static List<Double> getCountTimeList() {
		return countTimeList;
	}

	public final boolean addRulesName(String name, int obsRuleNameID,
			List<CRule> rules) {
		for (CRule rule : rules) {
			if ((rule.getName() != null) && (rule.getName().equals(name))) {
				ObservablesRuleComponent obsRC = new ObservablesRuleComponent(
						rule, obsRuleNameID);
				componentList.add(obsRC);
				return true;
			}
		}

		return false;
	}

	private final double getTimeSampleMin(double fullTime, int points) {
		double timeSampleMin;
		if (points != -1)
			timeSampleMin = fullTime / points;
		else
			timeSampleMin = fullTime / 1000;
		return timeSampleMin;
	}

	public final void calculateObs(double time, boolean isTime) {
		int size = countTimeList.size();
		if ((size > 0)
				&& (Math.abs(countTimeList.get(size - 1) - time) < 1e-16)) {
			if (isTime) {
				calculateAll(IObservablesComponent.CALCULATE_WITH_REPLASE_LAST);
			} else {
				updateLastValueAll(time);
				calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
				countTimeList.add(time);
				changeLastTime=false;
			}
			return;
		}

		if (time >= timeNext) {
			timeNext += timeSampleMin;
			if (!changeLastTime) {
				updateLastValueAll(time);
			}
			countTimeList.add(lastTime);
			calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
			lastTime = time;
			changeLastTime = false;
			return;
		}
		updateLastValueAll(time);
	}

	private final void calculateAll(boolean replaceLast) {
		for (IObservablesComponent cc : componentList) {
			cc.calculate(replaceLast);
		}
	}

	private double lastTime;
	private boolean changeLastTime = false;

	private final void updateLastValueAll(double time) {
		for (IObservablesComponent cc : componentList) {
			cc.updateLastValue();
		}
		lastTime = time;
		changeLastTime = true;
	}

	public final void calculateObsLast(double time) {
		int size = countTimeList.size();
		if (Math.abs(countTimeList.get(size - 1) - time) < 1e-16)
			return;
		updateLastValueAll(time);
		countTimeList.add(time);
		calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
	}

	public CObservables() {
	}

	public final void init(double fullTime, double initialTime, int points) {
		timeSampleMin = 0.;
		timeNext = 0.;
		if (initialTime > 0.0) {
			timeNext = initialTime;
			fullTime = fullTime - timeNext;
		} else
			timeNext = timeSampleMin;

		timeSampleMin = getTimeSampleMin(fullTime, points);
		timeNext += timeSampleMin;
	}

	public final List<IObservablesComponent> getComponentList() {
		return componentList;
	}

	public final List<ObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<CConnectedComponent> list,
			String name, String line, int id) {
		for (CConnectedComponent component : list) {
			ObservablesConnectedComponent oCC = new ObservablesConnectedComponent(
					component.getAgents(), name, line, id);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
			componentList.add(oCC);
		}
	}

	public final void checkAutomorphisms() {
		for (ObservablesConnectedComponent oCC : connectedComponentList) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
				for (ObservablesConnectedComponent oCCIn : connectedComponentList) {
					if (!(oCC == oCCIn)
							&& oCCIn.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
						if (oCC.isAutomorphism(oCCIn.getAgents().get(0))) {
							int index = connectedComponentList.indexOf(oCC);
							oCC.addAutomorphicObservables(index);
							oCCIn.setMainAutomorphismNumber(index);
						}
					}
				}
			}
		}
	}
}
