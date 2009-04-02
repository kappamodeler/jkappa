package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;

/**
 * Interface LinkState.
 * @author avokhmin
 *
 */
public interface ILinkState {

	public CSite getSite();

	public CLinkRank getStatusLinkRank();

	public CLinkStatus getStatusLink();

	public boolean isLeftBranchStatus();

	public boolean isRightBranchStatus();

	public void setSite(CSite site);

	public void setStatusLink(CLinkStatus statusLinkFree);

	public void setLinkStateID(int indexLink);

	public int getLinkStateID();

	public void setFreeLinkState();

	public boolean fullEqualityLinkStates(ILinkState solutionLinkState);

	public boolean compareLinkStates(ILinkState solutionLinkState);
}
