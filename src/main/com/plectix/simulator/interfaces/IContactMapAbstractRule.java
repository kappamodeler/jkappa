package com.plectix.simulator.interfaces;

import java.util.Collection;

import com.plectix.simulator.components.CRule;

public interface IContactMapAbstractRule {

	public boolean equalz(IContactMapAbstractRule rule);

	public CRule getRule();

	public boolean includedInCollection(
			Collection<IContactMapAbstractRule> collection);
}
