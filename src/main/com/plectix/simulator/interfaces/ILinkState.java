package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CLinkRank;
import com.plectix.simulator.components.CLinkStatus;

public interface ILinkState {

	public ISite getSite();

	public CLinkRank getStatusLinkRank();

	public CLinkStatus getStatusLink();

	public boolean isLeftBranchStatus();

	public boolean isRightBranchStatus();

	public void setSite(ISite site);

	public void setStatusLink(CLinkStatus statusLinkFree);

	public void setLinkStateID(int indexLink);

	public int getLinkStateID();

}
