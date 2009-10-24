package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.storage.StoriesAgentTypesStorage;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireHashKey;

public interface ActionObserverInteface {

	void addToEvent(Agent agentFromInSolution, ActionOfAEvent type,
			Agent agentFrom);

	void addSiteToEvent(Site site);

	void deleteAddNonFixedSites(Agent agentFromInSolution) throws StoryStorageException;

	void deleteAddToEvent(Site siteFromSolution, boolean stateFlag);

	void modifyAddSite(Site site, boolean stateFlag);

	void addAtomicEvent(WireHashKey wireHashKey, Site site,
			ActionOfAEvent modification, boolean afterState);

	void setTypeById(StoriesAgentTypesStorage storiesAgentTypesStorage);

	void registerAgent(Agent agent);

	void boundAddToEventContainer(Site siteByName, boolean afterState);

	void breakAddToEvent(Site linkSite, boolean beforeState);

}
