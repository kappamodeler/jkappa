package com.plectix.simulator.component;

/**
 * Class implements link rank of the link.<br>
 * For example:
 * <li>A(x) - link rank of site "x" does "FREE"</li>
 * <li>A(x!1),B(y!1) - link rank of site "x" does "BOUND"</li>
 * <li>A(x!_) - link rank of site "x" does "SEMI_LINK"</li>
 * <li>A(x?) - link rank of site "x" does "BOUND_OR_FREE"</li>
 * @see Link
 * @author avokhmin
 *
 */
public enum LinkRank {
	BOUND_OR_FREE(1),
	SEMI_LINK(2),
	BOUND(3),
	FREE(4);
	
	private final int orderNumber;
	
	private LinkRank(int order) {
		orderNumber = order;
	}
	
	/**
	 * This method compares current rank with given.
	 * @param rank given LinkRank
	 * @return <tt>true</tt> if current rank has less priority with given linkRank,
	 * otherwise <tt>false</tt>
	 */
	public final boolean lessPriority(LinkRank rank) {
		return orderNumber < rank.orderNumber;
	}
}
