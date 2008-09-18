package com.plectix.simulator.components;


import java.util.List;

import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ILift.LiftElement;

public class CLinkState implements ILinkState{
	
	public static final byte STATUS_LINK_CONNECTED = 0x01;
	public static final byte STATUS_LINK_MAY_BE = 0x02;
	public static final byte STATUS_LINK_FREE = 0x04;
	
	private byte statusLink;
	private ISite linkSite = null;
	
	public CLinkState(byte statusLink){
		this.statusLink=statusLink;
	}
	
	public ISite getSite(){
		return linkSite;
	}
	
	public void setSite(ISite site){
		linkSite=site;
		if(linkSite != null)
			statusLink=STATUS_LINK_CONNECTED;
	}
	
	
	public void setStatusLink(byte statusLink){
		this.statusLink=statusLink;
	}
	
	public byte getStatusLink(){
		return statusLink;
	}
	@Override
	public List<LiftElement> getLift() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void removeLiftElement(LiftElement element) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setLift(List<LiftElement> lift) {
		// TODO Auto-generated method stub
		
	}
	
	
}
