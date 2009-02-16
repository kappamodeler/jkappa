package com.plectix.simulator.components.injections;

import java.io.Serializable;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.interfaces.*;

public final class CLiftElement implements ILiftElement, Serializable {
	
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
