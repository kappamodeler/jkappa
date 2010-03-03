package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;

/**
 * TODO sooner or later we will use this class, so then we should put it in util package 
 * or something like that 
 * @author evlasov
 *
 */
public class ConnectedComponentPattern implements Pattern<ConnectedComponentInterface> {
	private final String componentStringRepresentation;
	
	public ConnectedComponentPattern(String template) {
		this.componentStringRepresentation = template;
	}
	
	public ConnectedComponentPattern(ConnectedComponentInterface connectedComponent) {
		if (connectedComponent == null) {
			componentStringRepresentation = "";
		} else {
			componentStringRepresentation = connectedComponent.getSmilesString();
		}
	}
	
	public boolean matches(ConnectedComponentInterface connectedComponent) {
		if (connectedComponent == null) {
			return false;
		}
		return componentStringRepresentation.equals(connectedComponent.getSmilesString());
	}
	
	public boolean matches(String componentRepresentation) {
		return componentStringRepresentation.equals(componentRepresentation);
	}
	
	@Override
	public final String toString() {
		return componentStringRepresentation;
	}
}
