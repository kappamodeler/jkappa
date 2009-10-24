package com.plectix.simulator.interfaces;

import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;

/**
 * Interface of Connected Component.
 */
public interface ConnectedComponentInterface extends SolutionElementInterface {

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

	/**
	 * @return smiles canonical representation of this component as String
	 */
	public String getSmilesString();

	/**
	 * @return the sum weight of injections from this connected component   
	 */
	public long getInjectionsWeight();

	/**
	 * Completely erases all injection, pointing to this connected component.
	 * It means, that these injections abort their existance
	 */
	public void deleteIncomingInjections();
	
	/**
	 * Sets the weight of all injection, pointing to this connected component, to the 1.
	 */
	public void burnIncomingInjections();

	/**
	 * Increments the weight of all injection, pointing to this connected component.
	 */
	public void incrementIncomingInjections();

	/**
	 * Notifies component that some injection changed it's weight 
	 * @param injection changed injection
	 * @param weightDifference weight shift
	 */
	public void updateInjection(Injection injection, long weightDifference);
}
