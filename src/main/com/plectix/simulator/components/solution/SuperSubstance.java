package com.plectix.simulator.components.solution;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

public class SuperSubstance implements ISolutionComponent {
	private long myQuantity = 0;
	private IConnectedComponent myComponent;
	
	//TODO build hash only with first needed
	private String hash;
	
	public SuperSubstance(IConnectedComponent component) {
		myComponent = component;
		myComponent.setSuperSubstance(this);
	}
	
	public SuperSubstance(long quant, IConnectedComponent component) {
		myComponent = component;
		myQuantity = quant;
		myComponent.setSuperSubstance(this);
	}
	
	// TODO catch exception
	public IConnectedComponent extract() {
		if (isEmpty()) { 
			return null;
		}
		myQuantity--;
		return myComponent;
	}
	
	public void setComponent(IConnectedComponent component) {
		myComponent = component;
	}
	
	public List<IAgent> getAgents() {
		return myComponent.getAgents();
	}
	
	public boolean isEmpty() {
		return myQuantity == 0;
	}
	
	public void add() {
		myQuantity++;
	}
	
	public boolean matches(IConnectedComponent component) {
		return myComponent.equals(component);
	}
	
	public IConnectedComponent getComponent() {
		return myComponent;
	}

	public void addAgentFromSolutionForRHS(IAgent agent) {
		// TODO Auto-generated method stub
		
	}

	public void clearAgentsFromSolutionForRHS() {
		// TODO Auto-generated method stub
		
	}

	public void doPositiveUpdate(List<IConnectedComponent> rightHandSide) {
		// TODO Auto-generated method stub
		
	}

	public List<IAgent> getAgentFromSolutionForRHS() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IAgent> getAgentsSortedByIdInRule() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInjection getFirstInjection() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInjection createInjection(IAgent agent) {
		return null;
	}

	public Collection<IInjection> getInjectionsList() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInjection getRandomInjection(IRandom random) {
		// TODO Auto-generated method stub
		return null;
	}

	public void initSpanningTreeMap() {
		// TODO Auto-generated method stub
		
	}

	public boolean isAutomorphism(IAgent agent) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeInjection(IInjection injection) {
		// TODO Auto-generated method stub
		
	}

	public void setInjection(IInjection inj) {
		// TODO Auto-generated method stub
		
	}

	public void setRule(CRule rule) {
		// TODO Auto-generated method stub
		
	}

	public boolean unify(IAgent agent) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SuperSubstance)) {
			return false;
		}
		SuperSubstance arg = (SuperSubstance)obj;
		return arg.myComponent.equals(arg.myComponent);
	}

	public long getQuantity() {
		return myQuantity;
	}
}
