package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CState implements IState{
	private String name;

	public CState(String name){
		this.name=name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<LiftElement> getLift() {
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
