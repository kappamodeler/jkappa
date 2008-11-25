package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.ILiftElement;
import com.plectix.simulator.interfaces.IState;

public abstract class CState implements IState {

	public abstract boolean isRankRoot();

	
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void removeLiftElement(ILiftElement element) {
		// TODO Auto-generated method stub
	}

}
