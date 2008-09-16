package KappaSimulationInterfaces;

import java.util.List;

import KappaSimulationInterfaces.ILift.LiftElement;

public interface IState {

	public boolean isChanged();
	
	public List<ILift.LiftElement> getLift();

	public void setLift(List<ILift.LiftElement> lift);
	
	void removeLiftElement(LiftElement element);
}
