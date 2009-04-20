package com.plectix.simulator.components.contactMap;

import com.plectix.simulator.action.CActionType;

/**
 * Util class. Used for save information about type of modification.
 * @author avokhmin
 *
 */
class CContactMapAtomicAction {
	private CContactMapAbstractSite site;
	private CActionType type;

	/**
	 * Constructor of CContactMapAtomicAction.
	 * @param type type of modification
	 * @param site given site
	 */
	public CContactMapAtomicAction(CActionType type,
			CContactMapAbstractSite site) {
		this.type = type;
		this.site = site;
	}

	/**
	 * This method returns site
	 * @return site
	 */
	public CContactMapAbstractSite getSite() {
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
