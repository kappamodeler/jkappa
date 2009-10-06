package com.plectix.simulator.component.stories.storage;

import com.plectix.simulator.action.ActionObserverInteface;
import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.component.stories.ActionOfAEvent;

public class NullEvent implements ActionObserverInteface {

	@Override
	public void addAtomicEvent(WireHashKey wireHashKey, Site site,
			ActionOfAEvent modification, boolean afterState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSiteToEvent(Site site) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToEvent(Agent agentFromInSolution, ActionOfAEvent type,
			Agent agentFrom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boundAddToEventContainer(Site siteByName, boolean afterState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakAddToEvent(Site linkSite, boolean beforeState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAddNonFixedSites(Agent agentFromInSolution)
			throws StoryStorageException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAddToEvent(Site siteFromSolution, boolean stateFlag) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void registerAgent(Agent newlyCreatedAgent) {
		// TODO Auto-generated method stub
	}



	@Override
	public void setTypeById(StoriesAgentTypesStorage storiesAgentTypesStorage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyAddSite(Site site, boolean stateFlag) {
		// TODO Auto-generated method stub
		
	}


}
