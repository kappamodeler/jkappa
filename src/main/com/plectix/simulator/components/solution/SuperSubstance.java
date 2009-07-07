package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.string.ConnectedComponentToSmilesString;
import com.plectix.simulator.interfaces.*;

public class SuperSubstance {
	private long myQuantity = 0;
	private IConnectedComponent myComponent;
	
	//TODO build hash only with first needed
	private String myHash;
	
	public SuperSubstance(IConnectedComponent component) {
		myComponent = component;
		myComponent.setSuperSubstance(this);
		refreshHash();
	}
	
	public SuperSubstance(long quant, IConnectedComponent component) {
		myComponent = component;
		myQuantity = quant;
		myComponent.setSuperSubstance(this);
		refreshHash();
	}
	
	private void refreshHash() {
		myHash = ConnectedComponentToSmilesString.getInstance().toUniqueString(myComponent);
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
//		refreshHash();
	}
	
	public List<CAgent> getAgents() {
		return myComponent.getAgents();
	}
	
	public boolean isEmpty() {
		return myQuantity == 0;
	}
	
	public void add() {
		myQuantity++;
	}
	
	public void add(long quant) {
		myQuantity += quant;
	}
	
	public IConnectedComponent getComponent() {
		return myComponent;
	}

	public long getQuantity() {
		return myQuantity;
	}
	
	public String getHash() {
		return myHash;
	}
	
	public String toString() {
		return "ss:" + myComponent + " * " + myQuantity;
	}
}
