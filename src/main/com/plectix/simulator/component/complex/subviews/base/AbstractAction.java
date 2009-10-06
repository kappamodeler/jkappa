package com.plectix.simulator.component.complex.subviews.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractLinkState;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsInterface;
import com.plectix.simulator.util.NameDictionary;

public final class AbstractAction {
	private final AbstractAgent leftHandSideAgent;
	private final AbstractAgent rightHandSideAgent;
	private List<AbstractSite> testedSites;
	private List<AbstractSite> modificatedSites;
	private final List<SubViewsInterface> subViewsList;
	private List<AbstractSite> sitesSideEffect;
	private List<String> sideEffect;
	private AbstractActionType actionType;
	private boolean applicable = false;

	public AbstractAction(AbstractAgent leftHandSideAgent,
			AbstractAgent rightAbstractAgent) {
		subViewsList = new LinkedList<SubViewsInterface>();
		this.leftHandSideAgent = leftHandSideAgent;
		this.rightHandSideAgent = rightAbstractAgent;
		init();
	}

	private final void init() {
		if (leftHandSideAgent == null) {
			actionType = AbstractActionType.ADD;
			return;
		}
		testedSites = new LinkedList<AbstractSite>();
		if (rightHandSideAgent == null) {
			actionType = AbstractActionType.DELETE;
			testedSites.addAll(leftHandSideAgent.getSitesMap().values());
			return;
		}
		modificatedSites = new LinkedList<AbstractSite>();
		actionType = AbstractActionType.TEST_ONLY;

		for (AbstractSite leftSite : leftHandSideAgent.getSitesMap().values()) {
			AbstractSite rightSite = rightHandSideAgent.getSiteByName(leftSite
					.getName());

			testlinkStates(leftSite, rightSite);
			if (!leftSite.getInternalState().equalz(
					rightSite.getInternalState())) {
				modificatedSites.add(leftSite);
				actionType = AbstractActionType.TEST_AND_MODIFICATION;
				continue;
			}

			if (!leftSite.getLinkState().equalz(rightSite.getLinkState())) {
				modificatedSites.add(leftSite);
				actionType = AbstractActionType.TEST_AND_MODIFICATION;
				continue;
			}
			testedSites.add(leftSite);
		}
	}

	private final void testlinkStates(AbstractSite leftSite, AbstractSite rightSite) {
		AbstractLinkState leftLinkState = leftSite.getLinkState();
		AbstractLinkState rightLinkState = rightSite.getLinkState();

		if (leftLinkState.equalz(rightLinkState))
			return;
		if (leftLinkState.getStatusLink() != LinkStatus.FREE
				&& NameDictionary.isDefaultSiteName(leftLinkState.getConnectedSiteName())) {
			if (sideEffect == null)
				sideEffect = new LinkedList<String>();
			sideEffect.add(leftSite.getName());
		}
	}

	public final void clearSitesSideEffect() {
		if (sitesSideEffect != null)
			sitesSideEffect.clear();
	}

	public final void addSiteSideEffect(AbstractSite breakingSite) {
		if (sitesSideEffect == null)
			sitesSideEffect = new LinkedList<AbstractSite>();
		sitesSideEffect.add(breakingSite);
	}

	public final void addSubViews(SubViewsInterface subViews) {
		subViewsList.add(subViews);
	}

	public final List<SubViewsInterface> getSubViews() {
		return subViewsList;
	}

	public final List<AbstractSite> getTestedSites() {
		return testedSites;
	}

	public final List<AbstractSite> getModificatedSites() {
		return modificatedSites;
	}

	public final AbstractAgent getLeftHandSideAgent() {
		return leftHandSideAgent;
	}

	public final AbstractAgent getRightHandSideAgent() {
		return rightHandSideAgent;
	}

	public final List<AbstractSite> getSitesSideEffect() {
		return sitesSideEffect;
	}

	public final AbstractActionType getActionType() {
		return actionType;
	}

	public final List<String> getSideEffect() {
		return sideEffect;
	}

	public final boolean canApply() {
		return applicable;
	}

	public final void setApplicable() {
		applicable = true;
	}

	public final void initSubViews(Map<String, List<SubViewsInterface>> subViewsMap) {
		AbstractAgent agent = leftHandSideAgent;
		if (leftHandSideAgent == null)
			agent = rightHandSideAgent;
		List<SubViewsInterface> subViewsList = subViewsMap.get(agent
				.getName());
		for (SubViewsInterface subViews : subViewsList) {
			if (actionType != AbstractActionType.TEST_ONLY
					&& actionType != AbstractActionType.DELETE
					&& actionType != AbstractActionType.ADD) {
				if (subViews.isAgentFit(agent))
					addSubViews(subViews);
			} else {
				if (agent.getSitesMap().isEmpty()
						|| actionType == AbstractActionType.DELETE)
					addSubViews(subViews);
				else {
					for (AbstractSite site : agent.getSitesMap().values()) {
						if (subViews.getSubViewClass().hasSite(
								site.getName())) {
							addSubViews(subViews);
							break;
						}
					}
				}
			}
		}
	}

}
