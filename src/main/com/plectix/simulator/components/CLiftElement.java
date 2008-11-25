package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.*;

public final class CLiftElement implements ILiftElement {
	
	private final CConnectedComponent connectedComponent;
	private final IInjection injection;
	
	public CLiftElement (CConnectedComponent cc, IInjection injection){
		this.connectedComponent = cc;
		this.injection = injection;
	}
	
	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	public IInjection getInjection(){
		return injection;
	}
	
}
