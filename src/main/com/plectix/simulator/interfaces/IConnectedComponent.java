package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
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

	public void burnIncomingInjections();

	public String getHash();

	/**
	 * 
	 * @return the sum weight of injections from this connected component   
	 */
	public long getInjectionsWeight();

	public CAgent findSimilarAgent(CAgent rulesSecondAgent);

	public CInjection findInjection(CAgent agentInFirstComponentToSwap);

	public void deleteIncomingInjections();
	
	public void incrementIncomingInjections();

	public void updateInjection(CInjection injection, long i);

	public int getAgentsQuantity();
}
