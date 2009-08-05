package com.plectix.simulator.components.stories.storage.graphs;


public class BoundedCouple {
	private Long agent1;
	private Long agent2;
	private Integer site1;
	private Integer site2 = -1;
	private Integer link = null;
	private Integer internalState1 = -1;
	private Integer internalState2 = -1;

	
	public BoundedCouple(Long agent1, Integer site1, long agent2,
			int site2) {
		this.agent1 = agent1;
		if(agent2 == -1)
			this.agent2 = null;
		else
			this.agent2 = agent2;
		this.site1 = site1;
		this.site2 = site2;
	}

	public BoundedCouple(Long key) {
		this.agent1 = key;
	}

	public void addSite(int site2){
		this.site2 = site2;
	}
	
	public Long getAgent1() {
		return agent1;
	}
	public Long getAgent2() {
		return agent2;
	}
	
	public boolean isFull(){
		return (site2!=-1);
	}
	
	public void setLink(int link) {
		this.link = link;
	}
	public int getLink() {
		return link;
	}
	
	public boolean isSame(BoundedCouple c){
		return (this.agent1.equals(c.getAgent2()) &&				
//				this.agent2.equals(c.getAgent1()) &&
				((this.agent2!= null && this.agent2.equals(c.getAgent1())) || (this.agent2 == null && c.getAgent1() == null))&&
				this.site1.equals(c.getSite2()) &&
				this.site2.equals(c.getSite1()) &&
				this.internalState1.equals(c.getInternalState2()) &&
				this.internalState2.equals(c.getInternalState1()));
	}

	public Integer getSite1() {
		return site1;
	}

	public Integer getSite2() {
		return site2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != this.getClass())
			return false;
		BoundedCouple c = (BoundedCouple)obj;
		return (this.agent1 == c.getAgent1() &&
				this.agent2 == c.getAgent2() &&
				this.site1 == c.getSite1() &&
				this.site2 == c.getSite2()&&
				this.internalState1.equals(c.getInternalState1()) &&
				this.internalState2.equals(c.getInternalState2()));
	}
	
	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, (int) (agent1 ^ (agent1 >>> 32)));
		if (agent2!=null)
			result = getResult(result, (int) (agent2 ^ (agent2 >>> 32)));
		
		
		result = getResult(result, site1);
		result = getResult(result, site2);
		
		result = getResult(result, internalState1);
		result = getResult(result, internalState2);
		
		
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	public void setInternalState1(Integer internal) {
		this.internalState1 = internal;
	}

	public Integer getInternalState1() {
		return internalState1;
	}

	public void setInternalState2(Integer internalState2) {
		this.internalState2 = internalState2;
	}

	public Integer getInternalState2() {
		return internalState2;
	}

}
