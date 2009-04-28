package com.plectix.simulator.components.stories.events;

public class UHashKey {
	private final long agentId;
	private final int siteId;
	private final EKeyOfState keyOfState;
	
	public UHashKey(long agentId,int siteId, EKeyOfState state){
		this.agentId = agentId;
		this.siteId = siteId;
		this.keyOfState = state;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof UHashKey))
			return false;
		
		UHashKey in = (UHashKey) obj;
		if(this.agentId == in.agentId && this.siteId == in.siteId && this.keyOfState == in.keyOfState)
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = getResult(result, (int)(agentId ^ (agentId >>> 32)));		
		result = getResult(result, siteId);
		result = getResult(result, keyOfState.getId());
		return result;
	}
	
	private static int getResult(int result, int c){
		return 37*result + c;
	}
	

}
