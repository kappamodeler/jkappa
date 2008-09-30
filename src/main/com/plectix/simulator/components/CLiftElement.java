package com.plectix.simulator.components;

public class CLiftElement {
	
	private CConnectedComponent connectedComponent;

	private CInjection injection;
	
	public CLiftElement (CConnectedComponent cc, CInjection injection){
		this.connectedComponent = cc;
		this.injection = injection;
	}
	
	
	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	public void setConnectedComponent(CConnectedComponent connectedComponent) {
		this.connectedComponent = connectedComponent;
	}
	
	public void setInjection(CInjection injection) {
		this.injection = injection;
	}

	public CInjection getInjection(){
		return injection;
	}
	
}
