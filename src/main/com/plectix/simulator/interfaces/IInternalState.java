package com.plectix.simulator.interfaces;

public interface IInternalState {

	public int getNameId();

	public String getName();

	public boolean isRankRoot();

	public int getStateNameId();

	public void setNameId(int nameInternalStateId);

	public boolean compareInternalStates(IInternalState solutionInternalState);

	public  boolean fullEqualityInternalStates(IInternalState solutionInternalState);
}
