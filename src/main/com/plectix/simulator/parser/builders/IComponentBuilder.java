package com.plectix.simulator.parser.builders;

import com.plectix.simulator.parser.abstractmodel.IAbstractComponent;

public interface IComponentBuilder<E> {
	public E build(IAbstractComponent template);
}
