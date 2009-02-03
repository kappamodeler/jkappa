package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.util.NameDictionary;

public interface IAgent extends Comparable<IAgent> {
	
	public int getIdInConnectedComponent();  //Returns the identifier of this object in the database.
	
	public Collection<ISite> getSites();  //Returns this Agent�s Sites
	
	public void addSite(ISite siteAdd);

	public ISite getSite(int nameId);
	
	public long getId();
	
	public long getHash();
	
	public IAgent findLinkAgent(IAgent agent, List<ISite> sitesFrom);

	public int getNameId();

	public boolean isAgentHaveLinkToConnectedComponent(
			IConnectedComponent connectedComponent, IInjection inj);

	public ISite getEmptySite();

	public int getIdInRuleSide();

	public void setIdInRuleSide(int counter);

	public Map<Integer, ISite> getSiteMap();

	public void setIdInConnectedComponent(int i);

	public String getName();

	public boolean equalz(IAgent lhsAgent);

	public boolean siteMapsAreEqual(IAgent lhsAgent);

	public boolean includedInCollection(Collection<IAgent> list);
}
