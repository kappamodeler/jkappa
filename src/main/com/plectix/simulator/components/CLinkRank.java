package com.plectix.simulator.components;

/**
 * Class implements link rank of LinkState.<br>
 * Examples:
 * <blockquote><pre>
 * A(x) - link rank of site "x" does "FREE"<br>
 * A(x!1),B(y!1) - link rank of site "x" does "BOUND"<br>
 * A(x!_) - link rank of site "x" does "SEMI_LINK"<br>
 * A(x?) - link rank of site "x" does "BOUND_OR_FREE"<br>
 * </blockquote></pre>
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
	 * This method compares current LinkRank with given.
	 * @param arg given LinkRank
	 * @return <tt>true</tt> if current LinkRank less priority with given linkRank,
	 * otherwise <tt>false</tt>
	 */
	public boolean smaller(CLinkRank arg) {
		return myOrderNumber < arg.myOrderNumber;
	}
}
