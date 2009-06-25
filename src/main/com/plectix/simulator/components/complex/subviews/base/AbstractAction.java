package com.plectix.simulator.components.complex.subviews.base;

import java.util.LinkedList;
import java.util.List;

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
	private List<CAbstractSite> sitesShouldBeBreak;
	private boolean isDeleted = false;

	public AbstractAction(CAbstractAgent leftHandSideAgent,
			CAbstractAgent rightAbstractAgent) {
		subViewsList = new LinkedList<ISubViews>();
		this.leftHandSideAgent = leftHandSideAgent;
		this.rightHandSideAgent = rightAbstractAgent;
		init();
	}

	private void init() {
		if (leftHandSideAgent == null) {
			return;
		}
		if(rightHandSideAgent == null){
			isDeleted = true;
			return;
		}
		testedSites = new LinkedList<CAbstractSite>();
		modificatedSites = new LinkedList<CAbstractSite>();

		for (CAbstractSite leftSite : leftHandSideAgent.getSitesMap().values()) {
			CAbstractSite rightSite = rightHandSideAgent.getSite(leftSite
					.getNameId());

			testlinkStates(leftSite, rightSite);
			if (!leftSite.getInternalState().equalz(
					rightSite.getInternalState())) {
				modificatedSites.add(leftSite);
				continue;
			}

			if (!leftSite.getLinkState().equalz(rightSite.getLinkState())) {
				modificatedSites.add(leftSite);
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
				&& leftLinkState.getLinkSiteNameID() == CSite.NO_INDEX){
			if(sitesShouldBeBreak == null)
				sitesShouldBeBreak = new LinkedList<CAbstractSite>();
			sitesShouldBeBreak.add(leftSite);
		}
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
	
	public List<CAbstractSite> getBreakingSites(){
		return sitesShouldBeBreak;
	}

	public boolean isDeleted(){
		return isDeleted;
	}
	
}
