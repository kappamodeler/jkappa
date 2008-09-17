package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservables;

public class CObservables implements IObservables{
	private IConnectedComponent conComp;
	
	public CObservables(){
		
	}
	
	public CObservables(List<IAgent> list){
		
	}

	public IConnectedComponent getConComp() {
		return conComp;
	}

	public void setConComp(IConnectedComponent conComp) {
		this.conComp = conComp;
	}
	
}
