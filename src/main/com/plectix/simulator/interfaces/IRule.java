package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.ConstraintData;
import com.plectix.simulator.simulator.SimulationData;

public interface IRule {
	
	public boolean isRHSEqualsLHS();

	public int getRuleID();

	public boolean isClash(List<IInjection> injectionsList);

	public void applyLastRuleForStories(List<IInjection> injectionsList,
			INetworkNotation netNotation);

	public void applyRuleForStories(List<IInjection> injectionsList,
			INetworkNotation netNotation, SimulationData simulationData, boolean isLast);

	public List<IRule> getActivatedRule();

	public List<IRule> getActivatedRuleForXMLOutput();

	public List<IObservablesConnectedComponent> getActivatedObservable();

	public List<IObservablesConnectedComponent> getActivatedObservableForXMLOutput();

	public List<IRule> getInhibitedRule();

	public List<IObservablesConnectedComponent> getInhibitedObservable();

	public List<IConnectedComponent> getRightHandSide();

	public List<IConnectedComponent> getLeftHandSide();

	public String getName();

	public boolean isInfinityRate();

	public void applyRule(List<IInjection> injectionsList, SimulationData simulationData);

	public List<ISite> getSitesConnectedWithDeleted();

	public List<ISite> getSitesConnectedWithBroken();

	public double getActivity();

	public int getCountAgentsLHS();

	public List<IAction> getActionList();

	public void calcultateActivity();

	public boolean isClashForInfiniteRule();

	public void createActivatedRulesList(List<IRule> rules);

	public void createInhibitedRulesList(List<IRule> rules);

	public void createActivatedObservablesList(IObservables observables);

	public double getRuleRate();

	public void setInfinityRate(boolean b);

	public void setRuleRate(double d);

	public String getData(boolean isOcamlStyleObsName);

	public void setData(String data);

	public void createInhibitedObservablesList(IObservables observables);
	
//	public ConstraintData getConstraintData();
	
	public boolean includedInCollection(Collection<IRule> list);
	
	public boolean isInvokedRule();

	public List<Long> getAgentsAddedID();

	public boolean isLHSisEmpty();

	public boolean isRHSisEmpty();
}
