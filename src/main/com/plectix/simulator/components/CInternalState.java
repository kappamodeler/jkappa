package com.plectix.simulator.components;

import java.util.List;

import com.plectix.simulator.interfaces.IState;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CInternalState implements IState {

	
	private int nameId; 
	
	public CInternalState(int id) {
		this.nameId = id;
	}

	@Override
	public final List<LiftElement> getLift() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final void removeLiftElement(LiftElement element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public final void setLift(List<LiftElement> lift) {
		// TODO Auto-generated method stub
		
	}

	public int getStateNameId() {
		return nameId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CInternalState))
			return false;
		return ((CInternalState)obj).nameId == nameId;
	}

}
