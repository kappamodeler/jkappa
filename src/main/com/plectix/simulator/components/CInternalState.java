package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CInternalState implements IState {

	
	private String state = null; 
	
	public CInternalState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CInternalState))
			return false;
		
		return state.equals(((CInternalState)obj).state);
	}

	@Override
	public List<LiftElement> getLift() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeLiftElement(LiftElement element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLift(List<LiftElement> lift) {
		// TODO Auto-generated method stub
		
	}

}
