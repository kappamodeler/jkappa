package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CObservables;

public interface IObservablesComponent {
//	public final static byte TYPE_CONNECTED_COMPONENT = 0;
//	public final static byte TYPE_RULE_COMPONENT = 1;

	public final static boolean CALCULATE_WITH_REPLASE_LAST = true;
	public final static boolean CALCULATE_WITH_NOT_REPLASE_LAST = false;

	/**
	 * This method saves current observable state (quantity, activity, etc).  
	 * @param replaceLast <tt>true</tt> if we need to overwrite the latest information, or <tt>false</tt>
	 * if we don't 
	 */
	public void calculate(boolean replaceLast);

	/**
	 * This method returns the name of this observable.
	 * @return the name of this observable.
	 */
	public String getName();

	/**
	 * This method returns the line of this observable, uses for output to xml.
	 * @return the line of this observable
	 */
	public String getLine();

	/**
	 * this methor returns the unique nameId of this observable group.
	 * @return the unique nameId of this observable group.
	 */
	public int getNameID();

	/**
	 * This method returns current state of observable. <br>
	 * If this observable for rule, then returns current rule rate.<br> 
	 * If this observable for connected components group (may be one), 
	 * then returns multiply injections.
	 * @param obs observables storage. 
	 * @return current state of observable.
	 */
	public double getCurrentState(CObservables obs);

	/**
	 * This method returns current state of observable on given <b>step</b>.
	 * @param step given step
	 * @param obs observables storage.
	 * @return current state of observable on given <b>step</b>
	 */
	public String getStringItem(int step, CObservables obs);

	/**
	 * This method returns current state of observable on given <b>step</b>.
	 * @param step given step
	 * @param obs observables storage.
	 * @return current state of observable on given <b>step</b>
	 */
	public long getLongItem(int step, CObservables obs);

	/**
	 * This method updates last state of observable. Util method.
	 */
	public void updateLastValue();
	
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
	
	/**
	 * This method returns <tt>true</tt> if current observable not includes to group, 
	 * otherwise <tt>false</tt> 
	 * @return <tt>true</tt> if current observable not includes to group, 
	 * otherwise <tt>false</tt> 
	 */
	public boolean isUnique();
}
