package com.plectix.simulator.components;

public class CLiftElement {
	
	public CLiftElement (CConnectedComponent cc, CInjection injection){
		this.connectedComponent = cc;
		this.injection = injection;
	}
	
	private CConnectedComponent connectedComponent;
	
	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	public void setConnectedComponent(CConnectedComponent connectedComponent) {
		this.connectedComponent = connectedComponent;
	}

	private CInjection injection;
	
	public void setInjection(CInjection injection) {
		this.injection = injection;
	}

	public CInjection getInjection(){
		return injection;
	}
	
}
