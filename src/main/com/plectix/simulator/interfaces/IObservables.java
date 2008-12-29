package com.plectix.simulator.interfaces;

import java.util.List;

public interface IObservables {

	public List<IObservablesComponent> getComponentList();

	public List<IObservablesConnectedComponent> getConnectedComponentList();

	public List<IObservablesComponent> getComponentListForXMLOutput();
	
	public List<IObservablesConnectedComponent> getConnectedComponentListForXMLOutput();
	
	public double getTimeSampleMin();

	public void resetLists();

	public void calculateObs(double currentTime, long count, boolean time);

	public void calculateObsLast(double currentTime);

	public void checkAutomorphisms();

	public void init(double timeLength, double initialTime, long event,
			int points, boolean time);

	public void addConnectedComponents(
			List<IConnectedComponent> buildConnectedComponents, String name,
			String line, int obsNameID);

	public boolean addRulesName(String name, int obsNameID, List<IRule> rules);

	public List<Double> getCountTimeList();

	public void setOcamlStyleObsName(boolean ocamlStyleObsName);

	public boolean isOcamlStyleObsName();

	
	
}
