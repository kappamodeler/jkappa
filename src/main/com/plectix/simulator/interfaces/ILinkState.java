package com.plectix.simulator.interfaces;

public interface ILinkState {

	public ISite getSite();

	public byte getStatusLinkRank();

	public byte getStatusLink();

	public boolean isLeftBranchStatus();

	public boolean isRightBranchStatus();

	public void setSite(ISite site);

	public void setStatusLink(byte statusLinkFree);

	public void setLinkStateID(int indexLink);

	public int getLinkStateID();

}
