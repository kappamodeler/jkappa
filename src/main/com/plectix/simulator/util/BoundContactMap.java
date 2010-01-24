package com.plectix.simulator.util;

import java.util.List;

public final class BoundContactMap {
	private final String sourceAgentName;
	private final String targetAgentName;
	private final String sourceSiteName;
	private final String targetSiteName;

	public BoundContactMap(String sourceAgentName, String sourceSiteName,
			String targetAgentName, String targetSiteName) {
		this.sourceAgentName = sourceAgentName;
		this.targetAgentName = targetAgentName;
		this.sourceSiteName = sourceSiteName;
		this.targetSiteName = targetSiteName;
	}


	final boolean equalz(BoundContactMap boundContactMap) {
		if (this == boundContactMap)
			return true;

		return (sourceAgentName.equals(boundContactMap.sourceAgentName) 
				&& targetAgentName.equals(boundContactMap.targetAgentName) 
				&& sourceSiteName.equals(boundContactMap.sourceSiteName) 
				&& targetSiteName.equals(boundContactMap.targetSiteName));
	}
	
	public final boolean includedInCollection(
			List<BoundContactMap> collection) {
		for (BoundContactMap element : collection) {
			if (this.equalz(element)) {
				return true;
			}
		}
		return false;
	}
}
