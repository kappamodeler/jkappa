package com.plectix.simulator.interfaces;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public interface IContactMapAbstractRule {

	public boolean equalz(IContactMapAbstractRule rule);

	public IRule getRule();

	public boolean includedInCollection(
			Collection<IContactMapAbstractRule> collection);
}
