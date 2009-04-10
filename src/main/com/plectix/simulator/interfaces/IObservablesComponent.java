package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CObservables;

/**
 * This interface describes observable component entity.
 * In fact, it can be connected components or rule.
 * @see CObservables
 * @author avokhmin
 *
 */
public interface IObservablesComponent {
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
	 * This method returns the line of this observable, used for output to xml.
	 * @return the line of this observable
	 */
	public String getLine();

	/**
	 * This method returns unique id of this observable component
	 * @return unique nameId of this observable component.
	 */
	public int getId();

	/**
	 * This method returns current state of observable. <br>
	 * If this observable is for the rule, then state means this rule's rate.<br> 
	 * If this observable is for connected components group, then returns multiply injections.
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
	public double getItem(int step, CObservables obs);

	/**
	 * This method updates last state of observable. Util method.
	 */
	public void updateLastValue();
	
	/**
	 * This method returns <tt>true</tt> if current observable not includes to group, 
	 * otherwise <tt>false</tt> 
	 * @return <tt>true</tt> if current observable not includes to group, 
	 * otherwise <tt>false</tt> 
	 */
	public boolean isUnique();
}
