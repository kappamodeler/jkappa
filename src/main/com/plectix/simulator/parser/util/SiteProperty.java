package com.plectix.simulator.parser.util;

// TODO REMOVE!
/*package*/ final class SiteProperty {
	private String siteLine = null;
	private String propertyLine = null;

	public final String getSiteLine() {
		return siteLine;
	}

	public final String getPropertyLine() {
		return propertyLine;
	}

	public final void setSiteLine(String st1) {
		this.siteLine = st1;
	}

	public final void setPropertyLine(String st2) {
		this.propertyLine = st2;
	}

	protected SiteProperty(String st1) {
		this.siteLine = st1;
	}
}
