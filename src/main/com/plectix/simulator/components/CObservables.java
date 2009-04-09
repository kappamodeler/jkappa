package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.*;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.*;

/**
 * This class describes observables storage.
 * This is the set of objects, which we want to keep an eye on during the simulation.
 * 
 * In fact such objects are rules and substances.
 * 
 * @see ObservablesConnectedComponent
 * @see ObservablesRuleComponent
 * @author evlasov
 *
 */
@SuppressWarnings("serial")
public class CObservables implements Serializable {
	private boolean ocamlStyleObsName = false;
	private final List<Double> countTimeList = new ArrayList<Double>();;
	private List<IObservablesConnectedComponent> connectedComponentList 
				= new ArrayList<IObservablesConnectedComponent>();;
	private List<IObservablesComponent> componentList = new ArrayList<IObservablesComponent>();
	private double timeNext;
	private double timeSampleMin;
	private double initialTime = 0.0;
	private long events = -1;
	private int points = -1;
	private double lastTime;
	private boolean changeLastTime = false;
	private boolean changeTimeNext = false;
	private List<IObservablesComponent> componentListForXMLOutput = null;

	/**
	 * This method initializes CObservables within external parameters 
	 * @param fullTime total time of simulation 
	 * @param initialTime time starting point
	 * @param events number of events
	 * @param points precision (number of points) of observables state graphic
	 * @param isTime <tt>true</tt>, if we have to save an information about current time and 
	 * observables list, otherwise <tt>false</tt>
	 */
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

			this.initializeMinSampleTime(fullTime, points);
			timeNext += timeSampleMin;
		} else {
			this.initializeMinSampleTime(events, points);
			if (initialTime <= 0.0)
				timeNext = timeSampleMin;
		}

	}
	
	/**
	 * This method resets all the information in CObservables. We use it when we've got
	 * more than on simulation in one time.
	 */
	public final void resetLists() {
		countTimeList.clear();
		componentListForXMLOutput = null;
		componentList.clear();
		connectedComponentList.clear();
	}
	
	/**
	 * This method returns minimal difference between two time points in graphic 
	 * for observables, i.e. precision.
	 * @return minimal difference between two time points in graphic for observables.
	 */
	public final double getTimeSampleMin() {
		return timeSampleMin;
	}

	/**
	 * This method returns list of time-points on "x" axis in graphic for observables.
	 * @return list of time-points on "x" axis in graphic for observables.
	 */
	public final List<Double> getCountTimeList() {
		return countTimeList;
	}

	/**
	 * This method sets default value for the minimal difference of two time points on
	 * the "x" axis in graphic for observables.
	 * @param simulationParameter is double parameter vary on the simulation type 
	 * @param points is the quantity of points on the "x" axis in observables graphic
	 */
	private final void initializeMinSampleTime(double simulationParameter, int points) { 
		if (points == -1) {
			timeSampleMin = (simulationParameter / SimulationArguments.DEFAULT_NUMBER_OF_POINTS);
		} else {
			timeSampleMin = (simulationParameter / points);
		}
	}

	/**
	 * This method handles observables in current time/event. 
	 * If there's need to save information about observables state (quantity, activity, etc), 
	 * it saves it.
	 * @param time current time
	 * @param count current event number
	 * @param isTime <tt>true</tt>, if we have to save an information about current time and 
	 * observables list, otherwise <tt>false</tt>
	 */
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

	/**
	 * This method indicates if we have to save an information about current time and 
	 * observables list
	 * @param time current time
	 * @param count is the number of current simulation event
	 * @param isTime true if we run "time" simulation
	 * @return <tt>true</tt>, if we have to save an information about current time and 
	 * observables list, otherwise <tt>false</tt>
	 */
	private final boolean isCalculateNow(double time, long count, boolean isTime) {
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
					this.initializeMinSampleTime(fullTime, points);
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

	/**
	 * This method saves current observables state (quantity, activity, etc).  
	 * @param replaceLast <tt>true</tt> if we need to overwrite the latest information, or <tt>false</tt>
	 * if we don't 
	 */
	private final void calculateAll(boolean replaceLast) {
		for (IObservablesComponent cc : componentList) {
			cc.calculate(replaceLast);
		}
	}

	/**
	 * This method handles information on observables in the latest moment of simulation 
	 * @param time current time
	 */
	private final void updateLastValueAll(double time) {
		for (IObservablesComponent cc : componentList) {
			cc.updateLastValue();
		}
		lastTime = time;
		changeLastTime = true;
	}

	/**
	 * This method saves observables state (quantity, activity, etc) in the latest moment of simulation.  
	 * @param time current time
	 */
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

	//--------------------------ADDERS------------------------------------------
	
	/**
	 * This method creates observable component using list of connected 
	 * components from solution
	 * @param list list of connected componetns from solution
	 * @param name name of the observable
	 * @param line kappa file line, which describes this observable component
	 * @param nameID observable id
	 */
	public final void addConnectedComponents(List<IConnectedComponent> list,
			String name, String line, int nameID) {
		boolean unique;
		if (list.size() > 1)
			unique = false;
		else
			unique = true;
		if (ocamlStyleObsName) {
			line = SimulationUtils.printPartRule(list, ocamlStyleObsName);
		}

		for (IConnectedComponent component : list) {
			IObservablesConnectedComponent oCC = new ObservablesConnectedComponent(
					component.getAgents(), name, line, nameID, unique);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
			componentList.add(oCC);
		}
	}

	/**
	 * This method returns adds Observable-rule from given collection of rules by it's name
	 * @param name name of the rule
	 * @param obsRuleNameID new observable rule id
	 * @param rules list of rules
	 * @return <tt>true</tt> if we've founded and added such rule to observables, otherwise <tt>false</tt>
	 */
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

	/**
	 * For each observable component this method calculates whether it's unique  
	 * canonical representative or not and saves this information to the component
	 */
	public final void checkAutomorphisms() {
		for (IObservablesConnectedComponent oCC : connectedComponentList) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
				for (IObservablesConnectedComponent oCCIn : connectedComponentList) {
					if (!(oCC == oCCIn)
							&& oCCIn.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
						if (oCC.getAgents().size() == oCCIn.getAgents().size())
							if (oCC.isAutomorphicTo(oCCIn.getAgents().get(0))) {
								int index = connectedComponentList.indexOf(oCC);
								oCC.addAutomorphicObservables(index);
								oCCIn.setMainAutomorphismNumber(index);
							}
					}
				}
			}
		}
	}
	
	//------------------------GETTERS AND SETTERS------------------------------
	
	public final List<IObservablesComponent> getComponentList() {
		return Collections.unmodifiableList(componentList);
	}
	
	public final List<IObservablesComponent> getComponentListForXMLOutput() {
		if (componentListForXMLOutput != null)
			return componentListForXMLOutput;
		List<Integer> map = new ArrayList<Integer>();
		List<IObservablesComponent> list = new ArrayList<IObservablesComponent>();
		for (IObservablesComponent cc : componentList) {
			if (!map.contains(cc.getId())) {
				map.add(cc.getId());
				list.add(cc);
			}
		}
		componentListForXMLOutput = list;
		return list;
	}

	public final List<IObservablesConnectedComponent> getConnectedComponentList() {
		return Collections.unmodifiableList(connectedComponentList);
	}

	public final List<IObservablesConnectedComponent> getConnectedComponentListForXMLOutput() {
		List<Integer> map = new ArrayList<Integer>();
		List<IObservablesConnectedComponent> list = new ArrayList<IObservablesConnectedComponent>();
		for (IObservablesConnectedComponent cc : connectedComponentList) {
			if (!map.contains(cc.getId())) {
				map.add(cc.getId());
				list.add(cc);
			}
		}
		return list;
	}
	
	public void setConnectedComponentList(List<IObservablesConnectedComponent> connectedComponentList) {
		this.connectedComponentList = connectedComponentList;
	}

	public void setComponentList(List<IObservablesComponent> componentList) {
		this.componentList = componentList;
	}
	
	public final void setOcamlStyleObsName(boolean ocamlStyleObsName) {
		this.ocamlStyleObsName = ocamlStyleObsName;
	}

	public final boolean isOcamlStyleObsName() {
		return ocamlStyleObsName;
	}
}
