package com.plectix.simulator.components.string;

import java.util.Comparator;

import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;

/**
 * This class compares two Sites. We assume that the Agent Names are the same. Here is the comparison rules:
 * 
 * <ul>
 * <li> First we compare the Site names.
 * <li> If two Sites have the same name, we compare their Internal State names.
 * <li> If two Sites are still equivalent, then a free site comes before a bound site
 * <li> If both Sites are bound, we compare the name of the Sites they are bound to
 * <li> If these names are the same, then we compare the Internal State names of the Sites they are bound to
 * <li> If still equivalent, then we compare the Agent names they are bound to
 * </ul>
 * 
 * @author ecemis
 */
public final class SiteComparator implements Comparator<CSite> {

	public static final SiteComparator SITE_COMPARATOR = new SiteComparator();
	
	/**
	 * Default constructor.
	 */
	private SiteComparator() {
		super();
	}
	
	
	public final int compare(final CSite o1, final CSite o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o2 == null) {
				return 1;
			} else {
				// don't handle this case here... go below...
			}
		}
		
		// first compare the site names:
		int result = o1.getName().compareTo(o2.getName());
		if (result != 0) {
			return result;
		}
		
		// then compare the names of the internal states:
		result = o1.getInternalState().getName().compareTo(o2.getInternalState().getName());
		if (result != 0) {
			return result;
		}
		
		// both sites have the same name and same internal states...
		
		// so let's compare their link sites:
		
		CSite linkSite1 = getLinkSite(o1);
		CSite linkSite2 = getLinkSite(o2);
		
		if (linkSite1 == null) {
			if (linkSite2 == null) {
				// both sites are free:
				return 0;
			} else {
				// let's have free site before bound site:
				return -1;
			}
		} else {
			if (linkSite2 == null) {
				// let's have free site before bound site:
				return +1;
			} else {
				// both sites are bound:
				// so let's compare the site names they are bound to:
				result = linkSite1.getName().compareTo(linkSite2.getName());
				if (result != 0) {
					return result;
				}
				
				// both sites are bound to a site with the same name!
				// then compare the names of the internal states:
				result = linkSite1.getInternalState().getName().compareTo(linkSite2.getInternalState().getName());
				if (result != 0) {
					return result;
				}
				
				// so let's compare the name of the Agents they are bound to:
				return linkSite1.getParentAgent().getName().compareTo(linkSite2.getParentAgent().getName());
			}
		}
			
		// we assume that the agents have the same name, so we can't use the agentLink to, CAgent agentLink = o1.getAgentLink();	
		// can't use linkIndex as an invariant: int linkIndex = o1.getLinkIndex();
	}

	private static final CSite getLinkSite(final CSite site) {
		CLink linkState = site.getLinkState();
		CLinkStatus statusLink = linkState.getStatusLink();
		
		if (statusLink == CLinkStatus.BOUND) {
			return linkState.getConnectedSite();
		} else if (statusLink == CLinkStatus.WILDCARD) {
			return null;
		} else if (statusLink == CLinkStatus.FREE) {
			return null;
		} else {
			// we expect that the site will be either BOUND, WILDCARD, or FREE. throw exception otherwise:
			throw new RuntimeException("Unexpected State: Link state is neither BOUND nor WILDCARD nor FREE.");
		}
	}
	

	/**
	 * We can also use the Strings made from this method to compare Sites... 
	 * I don't know which one would be faster...
	 * 
	 * <br><br>
	 * Example: <code>EGFR(Y1016~u!1),PTP(s!1)</code>
	 * 
	 * <br>
	 * Here we will make the following Strings: "<code>Y1016~u!BOUND-s-NO_INDEX-PTP</code>" 
	 * and "<code>s~NO_INDEX!BOUND-Y1016-u-EGFR</code>"
	 * 
	 * @param site
	 */
	public final String makeStringToCompare(CSite site) {
		String result = site.getName() + "~" + site.getInternalState().getName() + "!";

		CLink linkState = site.getLinkState();
		CLinkStatus statusLink = linkState.getStatusLink();
		if (statusLink == CLinkStatus.BOUND) {
			CSite connectedSite = site.getLinkState().getConnectedSite();
			if (connectedSite == null) {
				return result + "_";
			} else {
				return result + "BOUND-" + connectedSite.getName() 
	    		+ "-" + connectedSite.getInternalState().getName()
	    		+ "-" + connectedSite.getParentAgent().getName();
			}
		} else if (statusLink == CLinkStatus.WILDCARD) {
			return result + "WILD";
		} else if (statusLink == CLinkStatus.FREE) {
			return result + "FREE";
		} else {
			// we expect that the site will be either BOUND, WILDCARD, or FREE. throw exception otherwise:
			throw new RuntimeException("Unexpected State: Link state is neither BOUND nor WILDCARD nor FREE.");
		}
	}
}