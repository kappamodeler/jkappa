package com.plectix.simulator.speciesenumeration.util;

public class Reachables {

	private String name;
	private String cordinal;

	public Reachables(String _name, String _cordinal) {
		this.name = _name;
		this.cordinal = _cordinal;
	}

	public String getName() {
		return name;
	}

	public String getCordinal() {
		return cordinal;
	}

	@Override
	public boolean equals(Object aReachables) {

		if (this == aReachables)
			return true;

		if (aReachables == null)
			return false;

		if (getClass() != aReachables.getClass())
			return false;

		Reachables reachables = (Reachables) aReachables;

		final boolean isEqualName = reachables.name.equals(this.name);
		final boolean isEqualCardinal = reachables.cordinal
				.equals(this.cordinal);

		return (isEqualName && isEqualCardinal);

	}

}
