package com.plectix.simulator.interfaces;

/**
 * Interface of observable connected component.<br>
 * <br>
 * Example:<br>
 * <code>%obs: A(x!1),B(x!1)</code>, means observable connected component.
 * 
 * @author avokhmin
 *
 */
public interface ObservableConnectedComponentInterface extends ObservableInterface, ConnectedComponentInterface {
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