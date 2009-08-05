package com.plectix.simulator.rulecompression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class CommonPartFinder {
	// string is a representation of a path to the agent
	private Map<String, CAgent> agents = new HashMap<String, CAgent>();
	private final KappaSystem ks;
	private Map<CSite, CSite> registeredSitesFromLHS = new LinkedHashMap<CSite, CSite>();
	private final CAgent root;
	private final SubstanceMaster substanceMaster;
	
	// we've got one root fixed
	public CommonPartFinder(KappaSystem ks, CAgent root, SubstanceMaster master) {
		this.root = root;
		this.ks = ks;
		this.substanceMaster = master;
	}
	
	public CAgent getAgent(String pathHash) {
		return agents.get(pathHash.intern());
	}
	
	public void removeAgent(String pathHash) {
		agents.remove(pathHash.intern());
	}
	
	public void addPath(SitePath path) {
		// path shouldn't be == null! and roots should be the same
		if (path.getValue().isEmpty() || (path.getRoot() != this.root)) {
			return;
		}
		
		StringBuffer pathHead = new StringBuffer();
		Iterator<CSite> infoIterator = path.getValue().iterator();
		CSite outcomingSite = null;
		CSite incomingSite = null;
		CSite previousIncomingSite = null;
		CSite outSite = null;
		while (infoIterator.hasNext()) {
			// we put new agent only when leave it going to other edges
			CAgent agent = agents.get(pathHead.toString());
			outcomingSite = infoIterator.next();
			if (agent == null) {
				agent = new CAgent(outcomingSite.getParentAgent().getName(), ks.generateNextAgentId());
				agents.put(pathHead.toString(), agent);
			}
			
			// the root case only
			if (previousIncomingSite != null) {
				if (agent.getSiteByNameId(previousIncomingSite.getNameId()) == null) { 
					agent.addSite(previousIncomingSite);
				}
			}
			
			// add sites to their agent 
			outSite = registeredSitesFromLHS.get(outcomingSite);
			if (outSite == null) {
				outSite = agent.getSiteByNameId(outcomingSite.getNameId());
				if (outSite == null && (outcomingSite.getNameId() != CSite.NO_INDEX)) {
					outSite = getSiteClone(outcomingSite);
					agent.addSite(outSite);
				}
				registeredSitesFromLHS.put(outcomingSite, outSite);
			}
			
			if (infoIterator.hasNext()) {
				incomingSite = infoIterator.next();	
				
				CSite inSite = registeredSitesFromLHS.get(incomingSite);
				if (inSite == null) {
					inSite = getSiteClone(incomingSite);
					substanceMaster.connect(inSite, outSite);
					registeredSitesFromLHS.put(incomingSite, inSite);
					previousIncomingSite = inSite;
				} else {
					previousIncomingSite = null;
				}
				
				
				/*
				 * we are to add this site on the next iteration, which will certainly be,
				 * because for each incoming site there's always outcoming one (maybe the final one) 
				 */
			}
			pathHead.append(outcomingSite + "_");
			pathHead.append(incomingSite + "_");
		}
		
		// we should check whether the last outcoming site  
		// (the one which out path ends on) is bound to someone
		CLinkStatus ls = outcomingSite.getLinkState().getStatusLink();
		if (ls != CLinkStatus.FREE) {
			outSite.getLinkState().setStatusLink(ls);
			if (ls == CLinkStatus.BOUND) {
				CSite connectedOne = outcomingSite.getLinkState().getConnectedSite();
				CSite addedCopy = registeredSitesFromLHS.get(connectedOne);
				if (addedCopy != null) {
					substanceMaster.connect(outSite, addedCopy);
				}
			}
		}
	}
	
	private CSite getSiteClone(CSite arg) {
		CSite inSite = new CSite(arg.getNameId());
		inSite.setInternalState(new CInternalState(arg.getInternalState().getNameId()));
		return inSite;
	}
	
	public Collection<CAgent> getAgents() { 
		return agents.values();
	}

	public void addOrCompleteAgent(String key, CAgent agent) {
		CAgent target = agents.get(key.intern());
		if (target == null) {
			agents.put(key.intern(), agent);
		} else {
			for (CSite site : agent.getSites()) {
				int nameId = site.getNameId();
				if (target.getSiteByNameId(nameId) == null) {
					target.addSite(getSiteClone(site));
				}
			}
		}
		
	}
	
	public String toString() {
		return agents + "";
	}
}
