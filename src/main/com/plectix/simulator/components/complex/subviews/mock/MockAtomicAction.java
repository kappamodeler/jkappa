package com.plectix.simulator.components.complex.subviews.mock;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

/**
 * Util class. Used for save information about type of modification.
 * @author avokhmin
 *
 */
class MockAtomicAction {
	private CAbstractSite site;
	private CActionType type;

	/**
	 * Constructor of CContactMapAtomicAction.
	 * @param type type of modification
	 * @param site given site
	 */
	public MockAtomicAction(CActionType type,
			CAbstractSite site) {
		this.type = type;
		this.site = site;
	}

	/**
	 * This method returns site
	 * @return site
	 */
	public CAbstractSite getSite() {
		return site;
	}

	/**
	 * This method returns type of modifications
	 * @return type of modifications
	 */
	public CActionType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
