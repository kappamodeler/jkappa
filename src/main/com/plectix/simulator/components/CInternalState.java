package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CInternalState implements IInternalState{

	private IState state = null;
	
	
	public CInternalState(IState state){
		this.state=state;
	}
	
	
	@Override
	public IState getState() {
		return state;
	}
	
	
	
	@Override
	public void setState(IState state) {
		this.state=state;
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
