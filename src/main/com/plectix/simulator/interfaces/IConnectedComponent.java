package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.solution.SuperSubstance;
/**
 * Interface of Connected Component.
 * @author avokhmin
 *
 */
public interface IConnectedComponent extends ISolutionComponent {

	/**
	 * Sets parent super substance
	 * @param superSubstance new parent super substance
	 */
	public void setSuperSubstance(SuperSubstance superSubstance);

	/**
	 * Returns parent super substance 
	 * @return parent super substance
	 */
	public SuperSubstance getSubstance();

	/**
	 * This method indicates emptiness of this connected component
	 * @return <tt>true</tt>, if this ConnectedComponent is empty, otherwise <tt>false</tt>.
	 */
	public boolean isEmpty();

	public int getCommonPower();

	public void burnInjections();

	public String getHash();

	public void deleteAllInjections();
}
