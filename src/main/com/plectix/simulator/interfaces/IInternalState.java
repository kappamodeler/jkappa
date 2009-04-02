package com.plectix.simulator.interfaces;

/**
 * Interface of InternalState.
 * @author avokhmin
 *
 */
public interface IInternalState {

	public int getNameId();

	public String getName();

	public boolean isRankRoot();

	public void setNameId(int nameInternalStateId);

	public boolean compareInternalStates(IInternalState solutionInternalState);

	public  boolean fullEqualityInternalStates(IInternalState solutionInternalState);
}
