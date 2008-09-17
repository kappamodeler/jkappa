package com.plectix.simulator.interfaces;

import java.util.List;


public interface IAgent {
	
	public String getName();  //Return this Agent�s name Agent
	
	public Long getDBId();  //Returns the identifier of this object in the database.
	
	public List<ISite> getSites();  //Returns this Agent�s Sites
	
	public void addSite(ISite site);
	
	
	//TODO  it's the set of the agent's sites. maybe we don't need it.
	public List<String> getInterface();  //Returns this Agent�s Interface which is a list of Strings.
	
	public ILinkState getSiteLinkState(ISite site);  //Returns the links state of the given Site. The
										 //Link state can be Free, Bound, or Wildcard.
	
	public void setSiteLinkState(ISite site, ILinkState link_state);  //Sets the links state of the given Site to
												//the given State.
	
	public IInternalState getSiteInternalState(ISite internal_state);    //Returns the internal state of the given Site.
												//The Internal state can be a String or Wildcard.
	
	public void setSiteInternalState(ISite site, IInternalState internal_state);  //Sets the internal state of the given
													//Site to the given State.
	
}
