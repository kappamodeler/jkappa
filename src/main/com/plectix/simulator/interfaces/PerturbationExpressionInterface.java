package com.plectix.simulator.interfaces;

import com.plectix.simulator.component.Observables;
import com.plectix.simulator.component.perturbations.Perturbation;

/**
 * This class implements standard interface, used for
 * left/right handSide "perturbation expressions".<br>
 * For example:<br>
 * %mod: ['a'] > LHS do 'intro a':= RHS<br>
 * where<br>
 * <code>LHS</code> - left handSide expression;<br>
 * <code>RHS</code> - right handSide expression.
 * @author avokhmin
 * @see Perturbation
 */
public interface PerturbationExpressionInterface {
	
	/**
	 * This method returns name of watch expression.
	 * @return name of watch expression.
	 */
	public String getName();

	/**
	 * This method returns correction factor for current expression.
	 * @return correction factor for current expression.
	 */
	public String getValueToString();

	/**
	 * This method returns current state of this (calculate current expression).
	 * @param obs observables storage.
	 * @return current state of this.
	 */
	public double getMultiplication(Observables obs);

	/**
	 * This method returns correction factor for current expression.
	 * @return correction factor for current expression.
	 */
	public double getValue();

	/**
	 * This method sets correction factor for current expression.
	 * @param value given correction factor.
	 */
	public void setValue(double value);
}
