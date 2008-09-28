package com.plectix.simulator.components;

import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CInternalState extends CState {

	private int nameId; 
	
	public CInternalState(int id) {
		this.nameId = id;
	}

	public boolean isRankRoot(){
	    if (nameId == CSite.NO_INDEX)
	    	return true;
		return false;
	}
	
//	@Override
//	public final List<LiftElement> getLift() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public final void setNameId(int id){
		this.nameId=id;		
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

//	@Override
//	public final void setLift(List<LiftElement> lift) {
//		// TODO Auto-generated method stub
//		
//	}

	public final int getStateNameId() {
		return nameId;
	}
	
	@Override
	public final boolean equals(Object obj) {
		if(!(obj instanceof CInternalState))
			return false;
		return ((CInternalState)obj).nameId == nameId;
	}

}
