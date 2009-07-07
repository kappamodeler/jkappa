package com.plectix.simulator.components.stories.storage;

public class WireHashKey {
	private final long agentId;
	//=0 if test existence
	private final int siteId;
	private final ETypeOfWire keyOfState;
	//number of modify event on this wire
	private int numberOfEventOnWire;

	public WireHashKey(long agentId, int siteId, ETypeOfWire state) {
		this.agentId = agentId;
		this.siteId = siteId;
		this.keyOfState = state;
	}

	public WireHashKey(long agentId, ETypeOfWire state) {
		this.agentId = agentId;
		this.siteId = 0;
		this.keyOfState = state;
	}

	public ETypeOfWire getKeyOfState() {
		return keyOfState;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof WireHashKey))
			return false;

		WireHashKey in = (WireHashKey) obj;
		if (this.agentId == in.agentId && this.siteId == in.siteId
				&& this.keyOfState == in.keyOfState)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = getResult(result, (int) (agentId ^ (agentId >>> 32)));
		result = getResult(result, siteId);
		result = getResult(result, keyOfState.getId());
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	@Override
	public String toString() {
		String str;
		if (keyOfState == ETypeOfWire.AGENT)
			str = "agentId= " + agentId + " type= " + keyOfState.toString();
		else
			str = "agentId=" + agentId + " siteId=" + siteId + " type="
					+ keyOfState.toString();

		return str;
	}

	public void setNumberOfEventOnWire(int numberOfEventOnWire) {
		this.numberOfEventOnWire = numberOfEventOnWire;
	}

	public int getNumberOfUnresolvedEventOnWire() {
		return numberOfEventOnWire;
	}

	public void incNumberOfUnresolvedEvent(boolean up) {
		if (up){
			numberOfEventOnWire++;
		}else{
			numberOfEventOnWire--;
		}
		
	}
	
	public Integer getSiteId(){
		return siteId;
	}

}
