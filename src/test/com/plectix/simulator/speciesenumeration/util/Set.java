package com.plectix.simulator.speciesenumeration.util;

public class Set {

	private String name;

	public Set(String _name) {
		this.name = _name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object aSet) {

		if (this == aSet)
			return true;

		if (aSet == null)
			return false;

		if (getClass() != aSet.getClass())
			return false;

		Set set = (Set) aSet;

		return set.name.equals(this.name);

	}

}
