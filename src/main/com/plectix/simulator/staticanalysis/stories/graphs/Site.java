package com.plectix.simulator.staticanalysis.stories.graphs;

import com.plectix.simulator.util.NameDictionary;

final class Site {
	private final String name;
	private final int linkIndex;
	private final String internalStateName;

	public Site(String name, String internalState) {
		this.name = name;
		this.linkIndex = -1;
		this.internalStateName = internalState;
	}

	public Site(String name, int linkState, String internalState) {
		this.name = name;
		this.linkIndex = linkState;
		this.internalStateName = internalState;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Site))
			return false;

		Site in = (Site) obj;
		if (this.name.equals(in.name) && this.linkIndex == in.linkIndex
				&& this.internalStateName.equals(in.internalStateName))
			return true;
		return false;
	}

	@Override
	public final int hashCode() {
		int result = 101;
		result = getResult(result, name);
		result = getResult(result, linkIndex);
		result = getResult(result, internalStateName);
		return result;
	}

	private static int getResult(int result, Object obj) {
		return 37 * result + obj.hashCode();
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		if (!NameDictionary.isDefaultInternalStateName(internalStateName)) {
			sb.append("~"
					+ internalStateName);
		}

		if (linkIndex != -1) {
			sb.append("!" + linkIndex);
		}

		return sb.toString();
	}
}
