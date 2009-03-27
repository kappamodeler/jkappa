package com.plectix.simulator.components.string;

import java.util.Comparator;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;

/**
 * This class compares two Sites on the same Agent or two different Agents with the same name.
 * 
 * @author ecemis
 */
public final class SiteComparator implements Comparator<ISite> {

	public static final SiteComparator SITE_COMPARATOR = new SiteComparator();
	
	/**
	 * Default constructor.
	 */
	private SiteComparator() {
		super();
	}
	
	private static final ISite getLinkSite(ISite site) {
		ILinkState linkState = site.getLinkState();
		CLinkStatus statusLink = linkState.getStatusLink();
		
		if (statusLink == CLinkStatus.BOUND) {
			return linkState.getSite();
		} else if (statusLink == CLinkStatus.FREE) {
			return null;
		} else {
			// we expect that the site will be either BOUND or FREE. throw exception otherwise:
			throw new RuntimeException("Unexpected State: Link state is neither BOUND nor FREE.");
		}
	}
	
	public final int compare(ISite o1, ISite o2) {
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
		
		ISite linkSite1 = getLinkSite(o1);
		ISite linkSite2 = getLinkSite(o2);
		
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
				return linkSite1.getAgentLink().getName().compareTo(linkSite2.getAgentLink().getName());
			}
		}
			
		// we assume that the agents have the same name, so we can't use the agentLink to, IAgent agentLink = o1.getAgentLink();	
		// can't use linkIndex as an invariant: int linkIndex = o1.getLinkIndex();
	}
	

	/**
	 * We can also use the Strings made from this method to compare Sites... 
	 * I don't know which one would be faster...
	 * 
	 * <br>
	 * Example: EGFR(Y1016~u!1),PTP(s!1) 
	 * Here we have "Y1016~u!BOUND-s-NO_INDEX-PTP" and "s~NO_INDEX!BOUND-Y1016-u-EGFR"
	 * 
	 * @param site
	 */
	public final String makeStringToCompare(ISite site) {
		return site.getName() + "~" + site.getInternalState().getName() + "!"
			+ ( site.getLinkState().getStatusLink() == CLinkStatus.FREE ?
				"FREE"
			    : ("BOUND-" + site.getLinkState().getSite().getName() 
			    		+ "-" + site.getLinkState().getSite().getInternalState().getName()
			    		+ "-" + site.getLinkState().getSite().getAgentLink().getName())
			    );
	}
}