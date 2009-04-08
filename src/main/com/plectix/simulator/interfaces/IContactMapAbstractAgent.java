package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.Map;

import com.plectix.simulator.components.CAgent;

public interface IContactMapAbstractAgent extends IAbstractAgent {

	public void addSites(CAgent agent,
			Map<Integer, IContactMapAbstractAgent> agentNameIdToAgent);

	public boolean equalz(IAbstractAgent obj);

	public Map<Integer, IContactMapAbstractSite> getSitesMap();

	public IContactMapAbstractSite getEmptySite();

	public void setId(long id);

	public void addModelSite(IContactMapAbstractSite siteToAdd);

	public IContactMapAbstractSite getSite(int nameID);

	public void modify(IContactMapAbstractSite s);

	public void addSite(IContactMapAbstractSite newSite);

	public boolean isFit(IContactMapAbstractAgent agent);

	public IContactMapAbstractAgent clone();

	public boolean includedInCollection(
			Collection<IContactMapAbstractAgent> collection);

	public boolean includedInCollectionByName(
			Collection<IContactMapAbstractAgent> collection);

	public boolean isAdd();

	public void shouldAdd();

	public String getKey();
}
