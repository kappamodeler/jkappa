package com.plectix.simulator.components.solution;

import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

public class SuperSubstance implements ISolutionComponent {
	private int myQuantity = 0;
	private final IConnectedComponent myComponent;
	
	//TODO build hash only with first needed
	private String hash;
	
	public SuperSubstance(IConnectedComponent component) {
		myComponent = component;
	}
	
	// TODO catch exception
	public IConnectedComponent extract() {
		if (isEmpty()) { 
			throw new UnsupportedOperationException();
		}
		myQuantity--;
//		IConnectedComponent copy = ;.................
		return myComponent;
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
	
	public IConnectedComponent getConnectedComponent() {
		return myComponent;
	}

	@Override
	public void addAgentFromSolutionForRHS(IAgent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAgentsFromSolutionForRHS() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doPositiveUpdate(List<IConnectedComponent> rightHandSide) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IAgent> getAgentFromSolutionForRHS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IAgent> getAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IAgent> getAgentsSortedByIdInRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInjection getFirstInjection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInjection createInjection(IAgent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IInjection> getInjectionsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInjection getRandomInjection(IRandom random) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initSpanningTreeMap() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAutomorphism(IAgent agent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeInjection(IInjection injection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInjection(IInjection inj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRule(IRule rule) {
		// TODO Auto-generated method stub
		
	}

	@Override
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
}
