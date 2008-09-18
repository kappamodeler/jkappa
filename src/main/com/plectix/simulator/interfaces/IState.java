package com.plectix.simulator.interfaces;

import java.util.List;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public interface IState {

	public boolean isChanged();
	
	public List<LiftElement> getLift();
	
	public void setLift(List<ILift.LiftElement> lift);
	
	void removeLiftElement(LiftElement element);
	
	public String getName();
}
