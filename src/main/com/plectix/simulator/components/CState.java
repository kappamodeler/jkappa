package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public abstract class CState implements IState{

	private List<CConnectedComponent> lift = new ArrayList<CConnectedComponent>();
	
	public void setLift(List<CConnectedComponent> lift) {
		this.lift = lift;
	}

	public void addToLift(CConnectedComponent cc) {
		this.lift.add(cc);
	}
	
	public List<CConnectedComponent> getLift() {
		return lift;
	}

	public abstract boolean isRankRoot();
	
//	@Override
//	public List<LiftElement> getLift() {
//		// TODO Auto-generated method stub
//		return lift;
//	}

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

//	@Override
//	public void setLift(List<LiftElement> lift) {
//		// TODO Auto-generated method stub
//		
//	}

}
