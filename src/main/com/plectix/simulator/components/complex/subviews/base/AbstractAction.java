package com.plectix.simulator.components.complex.subviews.base;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

public class AbstractAction {
	private CAbstractAgent leftHandSideAgent;
	private CAbstractAgent rightHandSideAgent;
	private List<CAbstractSite> testedSites;
	private List<CAbstractSite> modificatedSites;
	
	
	public AbstractAction(CAbstractAgent leftHandSideAgent, CAbstractAgent rightAbstractAgent) {
		this.leftHandSideAgent = leftHandSideAgent;
		this.rightHandSideAgent = rightAbstractAgent;
		init();
	}

	private void init() {
		if(leftHandSideAgent == null || rightHandSideAgent == null){
			return;
		}
		testedSites = new LinkedList<CAbstractSite>();
		modificatedSites = new LinkedList<CAbstractSite>();
		
		for(CAbstractSite leftSite : leftHandSideAgent.getSitesMap().values()){
			CAbstractSite rightSite = rightHandSideAgent.getSite(leftSite.getNameId());
			
			if(!leftSite.getInternalState().equalz(rightSite.getInternalState())){
				modificatedSites.add(leftSite);
				continue;
			}
			
			if(!leftSite.getLinkState().equalz(rightSite.getLinkState())){
				modificatedSites.add(leftSite);
				continue;
			}
			testedSites.add(leftSite);
		}
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
	
}
