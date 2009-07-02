package com.plectix.simulator.components.complex.subviews.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;

public class AbstractAction {
	private CAbstractAgent leftHandSideAgent;
	private CAbstractAgent rightHandSideAgent;
	private List<CAbstractSite> testedSites;
	private List<CAbstractSite> modificatedSites;
	private List<ISubViews> subViewsList;
	private List<CAbstractSite> sitesSideEffect;
	private List<Integer> sideEffect;
	private EAbstractActionType actionType;
	private boolean canApply = false;

	public AbstractAction(CAbstractAgent leftHandSideAgent,
			CAbstractAgent rightAbstractAgent) {
		subViewsList = new LinkedList<ISubViews>();
		this.leftHandSideAgent = leftHandSideAgent;
		this.rightHandSideAgent = rightAbstractAgent;
		init();
	}

	private void init() {
		if (leftHandSideAgent == null) {
			actionType = EAbstractActionType.ADD;
			return;
		}
		testedSites = new LinkedList<CAbstractSite>();
		if (rightHandSideAgent == null) {
			actionType = EAbstractActionType.DELETE;
			testedSites.addAll(leftHandSideAgent.getSitesMap().values());
			return;
		}
		modificatedSites = new LinkedList<CAbstractSite>();
		actionType = EAbstractActionType.TEST_ONLY;

		for (CAbstractSite leftSite : leftHandSideAgent.getSitesMap().values()) {
			CAbstractSite rightSite = rightHandSideAgent.getSite(leftSite
					.getNameId());

			testlinkStates(leftSite, rightSite);
			if (!leftSite.getInternalState().equalz(
					rightSite.getInternalState())) {
				modificatedSites.add(leftSite);
				actionType = EAbstractActionType.TEST_AND_MODIFICATION;
				continue;
			}

			if (!leftSite.getLinkState().equalz(rightSite.getLinkState())) {
				modificatedSites.add(leftSite);
				actionType = EAbstractActionType.TEST_AND_MODIFICATION;
				continue;
			}
			testedSites.add(leftSite);
		}
	}

	private void testlinkStates(CAbstractSite leftSite, CAbstractSite rightSite) {
		CAbstractLinkState leftLinkState = leftSite.getLinkState();
		CAbstractLinkState rightLinkState = rightSite.getLinkState();

		if (leftLinkState.equalz(rightLinkState))
			return;
		if (leftLinkState.getStatusLink() != CLinkStatus.FREE
				&& leftLinkState.getLinkSiteNameID() == CSite.NO_INDEX) {
			if (sideEffect == null)
				sideEffect = new LinkedList<Integer>();
			sideEffect.add(leftSite.getNameId());
			// if (sitesShouldBeBreak == null)
			// sitesShouldBeBreak = new LinkedList<CAbstractSite>();
			// sitesShouldBeBreak.add(leftSite);
		}
	}

	public void clearSitesSideEffect() {
		if (sitesSideEffect != null)
			sitesSideEffect.clear();
	}

	public void addSiteSideEffect(CAbstractSite breakingSite) {
		if (sitesSideEffect == null)
			sitesSideEffect = new LinkedList<CAbstractSite>();
		sitesSideEffect.add(breakingSite);
	}

	public void addSubViews(ISubViews subViews) {
		subViewsList.add(subViews);
	}

	public List<ISubViews> getSubViews() {
		return subViewsList;
	}

	public List<CAbstractSite> getTestedSites() {
		return testedSites;
	}

	public List<CAbstractSite> getModificatedSites() {
		return modificatedSites;
	}

	public CAbstractAgent getLeftHandSideAgent() {
		return leftHandSideAgent;
	}

	public CAbstractAgent getRightHandSideAgent() {
		return rightHandSideAgent;
	}

	public List<CAbstractSite> getSitesSideEffect() {
		return sitesSideEffect;
	}

	public EAbstractActionType getActionType() {
		return actionType;
	}

	public List<Integer> getSideEffect() {
		return sideEffect;
	}
	
	public boolean canApply(){
		return canApply;
	}
	
	public void setApply(boolean b){
		canApply = b;
	}

	public void initSubViews(Map<Integer, List<ISubViews>> subViewsMap) {
		CAbstractAgent agent = leftHandSideAgent;
		if (leftHandSideAgent == null)
			agent = rightHandSideAgent;
		List<ISubViews> subViewsList = subViewsMap.get(agent.getNameId());
		for (ISubViews subViews : subViewsList) {
			if (actionType != EAbstractActionType.TEST_ONLY
					&& actionType != EAbstractActionType.DELETE) {
				if (subViews.isAgentFit(agent))
					addSubViews(subViews);
			} else {
				if (agent.getSitesMap().isEmpty()
						|| actionType == EAbstractActionType.DELETE)
					addSubViews(subViews);
				else {
					for (CAbstractSite site : agent.getSitesMap().values()) {
						if (subViews.getSubViewClass().isHaveSite(
								site.getNameId())) {
							addSubViews(subViews);
							break;
						}
					}
				}
			}
		}
	}

}
