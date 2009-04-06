package com.plectix.simulator.interfaces;

public interface IObservablesConnectedComponent extends IObservablesComponent, IConnectedComponent {
	/**
	 * This method returns the Id of main automorphism observable. Util method.
	 * @return the Id of main automorphism observable
	 */
	public int getMainAutomorphismNumber();

	/**
	 * This method sets the Id of main automorphism observable. Util method.
	 * @param index new value of main automorphism observable
	 */
	public void setMainAutomorphismNumber(int index);

	/**
	 * This method adds <b>index</b> of observables automorphic's current. 
	 * @param id given id of observable
	 */
	public void addAutomorphicObservables(int id);
}