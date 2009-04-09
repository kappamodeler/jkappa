package com.plectix.simulator.components;

/**
 * Class implements link rank of the link.<br>
 * For example:
 * <li>A(x) - link rank of site "x" does "FREE"</li>
 * <li>A(x!1),B(y!1) - link rank of site "x" does "BOUND"</li>
 * <li>A(x!_) - link rank of site "x" does "SEMI_LINK"</li>
 * <li>A(x?) - link rank of site "x" does "BOUND_OR_FREE"</li>
 * @see CLink
 * @author avokhmin
 *
 */
public enum CLinkRank {
	BOUND_OR_FREE(1),
	SEMI_LINK(2),
	BOUND(3),
	FREE(4);
	
	private int myOrderNumber;
	
	private CLinkRank(int order) {
		myOrderNumber = order;
	}
	
	/**
	 * This method compares current rank with given.
	 * @param arg given LinkRank
	 * @return <tt>true</tt> if current rank has less priority with given linkRank,
	 * otherwise <tt>false</tt>
	 */
	public boolean lessPriority(CLinkRank arg) {
		return myOrderNumber < arg.myOrderNumber;
	}
}
