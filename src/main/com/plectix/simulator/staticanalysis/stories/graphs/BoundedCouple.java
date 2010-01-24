package com.plectix.simulator.staticanalysis.stories.graphs;

import com.plectix.simulator.staticanalysis.InternalState;

final class BoundedCouple {
	private final long firstAgentId;
	private final long secondAgentId;
	private final String firstSiteName;
	private final String secondSiteName;
	private Integer link = null;
	private String firstInternalState = InternalState.DEFAULT_NAME;
	private String secondInternalState = InternalState.DEFAULT_NAME;

	public BoundedCouple(long firstAgentId, String firstSiteName, 
			long secondAgentId, String secondSiteName) {
		this.firstAgentId = firstAgentId;
		if (secondAgentId == -1)
			this.secondAgentId = Long.MIN_VALUE;
		else
			this.secondAgentId = secondAgentId;
		this.firstSiteName = firstSiteName;
		this.secondSiteName = secondSiteName;
	}

	public final long getFirstAgentId() {
		return firstAgentId;
	}

	public final long getSecondAgentId() {
		return secondAgentId;
	}

	public final void setLink(int link) {
		this.link = link;
	}

	public final int getLink() {
		return link;
	}

	public final void setFirstInternalState(String internal) {
		this.firstInternalState = internal;
	}

	public final String getFirstInternalState() {
		return firstInternalState;
	}

	public final void setSecondInternalState(String internalState2) {
		this.secondInternalState = internalState2;
	}

	public final String getSecondInternalState() {
		return secondInternalState;
	}

	public final boolean isSame(BoundedCouple couple) {
		return (this.firstAgentId == couple.getSecondAgentId()
				&&
				// this.agent2.equals(c.getAgent1()) &&
				((this.secondAgentId != Long.MIN_VALUE 
						&& this.secondAgentId == couple.getFirstAgentId()) 
						|| (this.secondAgentId == Long.MIN_VALUE && couple
								.getFirstAgentId() == Long.MIN_VALUE))
				&& this.firstSiteName.equals(couple.secondSiteName)
				&& this.secondSiteName.equals(couple.firstSiteName)
				&& this.firstInternalState.equals(couple.getSecondInternalState()) 
				&& this.secondInternalState.equals(couple.getFirstInternalState()));
	}

	public final String getFirstSite() {
		return firstSiteName;
	}

	public final String getSecondSite() {
		return secondSiteName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != this.getClass())
			return false;
		BoundedCouple couple = (BoundedCouple) obj;
		return (this.firstAgentId == couple.getFirstAgentId()
				&& this.secondAgentId == couple.getSecondAgentId()
				&& this.firstSiteName.equals(couple.getFirstSite())
				&& this.secondSiteName.equals(couple.getSecondSite())
				&& this.firstInternalState.equals(couple.getFirstInternalState()) && this.secondInternalState == couple
				.getSecondInternalState());
	}

	@Override
	public final int hashCode() {
		int result = 101;
		result = getResult(result, (int) (firstAgentId ^ (firstAgentId >>> 32)));
		if (secondAgentId != Long.MIN_VALUE)
			result = getResult(result, (int) (secondAgentId ^ (secondAgentId >>> 32)));

		result = getResult(result, firstSiteName);
		result = getResult(result, secondSiteName);

		result = getResult(result, firstInternalState);
		result = getResult(result, secondInternalState);

		return result;
	}

	private static final int getResult(int result, Object obj) {
		return 37 * result + obj.hashCode();
	}
}
