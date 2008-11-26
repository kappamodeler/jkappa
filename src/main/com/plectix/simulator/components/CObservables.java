package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.*;

public class CObservables {
	private static boolean ocamlStyleObsName = false;

	private List<IObservablesConnectedComponent> connectedComponentList;
	private List<IObservablesComponent> componentList;
	public static List<Double> countTimeList;

	private double timeNext;
	private double timeSampleMin;
	private double initialTime = 0.0;
	private long events = -1;
	private int points = -1;

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
			List<IRule> rules) {
		for (IRule rule : rules) {
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

	public final void calculateObs(double time, long count, boolean isTime) {
		int size = countTimeList.size();

		if ((size > 0)
				&& (Math.abs(countTimeList.get(size - 1) - time) < 1e-16)) {
			if (isTime) {
				calculateAll(IObservablesComponent.CALCULATE_WITH_REPLASE_LAST);
			} else {
				updateLastValueAll(time);
				calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
				countTimeList.add(time);
				changeLastTime = false;
			}
			return;
		}

		if (isCalculateNow(time, count, isTime)) {
			// if (time >= timeNext) {
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

	private boolean changeTimeNext = false;

	private boolean isCalculateNow(double time, long count, boolean isTime) {
		if (isTime) {
			if (initialTime > 0)
				if ((!changeTimeNext) && (initialTime < time)) {
					changeTimeNext = true;
					updateLastValueAll(time);
				}
			if (time >= timeNext)
				return true;
		} else {
			if (initialTime > 0)
				if ((!changeTimeNext) && (initialTime < time)) {
					long fullTime = events - count;
					timeSampleMin = getTimeSampleMin(fullTime, points);
					timeNext = count + timeSampleMin;
					updateLastValueAll(time);
					countTimeList.add(time);
					calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
					changeTimeNext = true;
				}
			if (time < initialTime)
				return false;
			if (count >= timeNext)
				return true;
		}

		return false;
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
		if (size == 0)
			return;
		if (Math.abs(countTimeList.get(size - 1) - time) < 1e-16)
			return;
		updateLastValueAll(time);
		countTimeList.add(time);
		calculateAll(IObservablesComponent.CALCULATE_WITH_NOT_REPLASE_LAST);
	}

	public CObservables() {
		connectedComponentList = new ArrayList<IObservablesConnectedComponent>();
		componentList = new ArrayList<IObservablesComponent>();
		countTimeList = new ArrayList<Double>();
	}

	public final void init(double fullTime, double initialTime, long events,
			int points, boolean isTime) {
		timeSampleMin = 0.;
		timeNext = 0.;
		this.initialTime = initialTime;
		this.events = events;
		this.points = points;

		if (isTime) {
			if (initialTime > 0.0) {
				timeNext = initialTime;
				fullTime = fullTime - timeNext;
			} else
				timeNext = timeSampleMin;

			timeSampleMin = getTimeSampleMin(fullTime, points);
			timeNext += timeSampleMin;
		} else {
			timeSampleMin = getTimeSampleMin(events, points);
			if (initialTime <= 0.0)
				timeNext = timeSampleMin;
		}

	}

	public final List<IObservablesComponent> getComponentList() {
		return componentList;
	}

	public final List<IObservablesComponent> getComponentListForXMLOutput() {
		List<Integer> map = new ArrayList<Integer>();
		List<IObservablesComponent> list = new ArrayList<IObservablesComponent>();
		for (IObservablesComponent cc : componentList) {
			if (!map.contains(cc.getNameID())) {
				map.add(cc.getNameID());
				list.add(cc);
			}
		}
		return list;
	}

	public final List<IObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<IConnectedComponent> list,
			String name, String line, int nameID) {
		boolean unique;
		if (list.size() > 1)
			unique = false;
		else
			unique = true;
		if (ocamlStyleObsName) {
			line = SimulationMain.getSimulationManager().printPartRule(list);
		}

		for (IConnectedComponent component : list) {
			IObservablesConnectedComponent oCC = new ObservablesConnectedComponent(
					component.getAgents(), name, line, nameID, unique);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
			componentList.add(oCC);
		}
	}

	public final List<IObservablesConnectedComponent> getConnectedComponentListForXMLOutput() {
		List<Integer> map = new ArrayList<Integer>();
		List<IObservablesConnectedComponent> list = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent cc : connectedComponentList) {
			if (!map.contains(cc.getNameID())) {
				map.add(cc.getNameID());
				list.add(cc);
			}
		}
		return list;
	}

	public final void checkAutomorphisms() {
		for (IObservablesConnectedComponent oCC : connectedComponentList) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
				for (IObservablesConnectedComponent oCCIn : connectedComponentList) {
					if (!(oCC == oCCIn)
							&& oCCIn.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
						if (oCC.getAgents().size() == oCCIn.getAgents().size())
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

	public final static void setOcamlStyleObsName(boolean ocamlStyleObsName) {
		CObservables.ocamlStyleObsName = ocamlStyleObsName;
	}

	public final static boolean isOcamlStyleObsName() {
		return ocamlStyleObsName;
	}
}
