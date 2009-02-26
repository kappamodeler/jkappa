package com.plectix.simulator.interfaces;

import java.util.Collection;

public interface IContactMapAbstractRule {

	public boolean equalz(IContactMapAbstractRule rule);

	public IRule getRule();

	public boolean includedInCollection(
			Collection<IContactMapAbstractRule> collection);
}
