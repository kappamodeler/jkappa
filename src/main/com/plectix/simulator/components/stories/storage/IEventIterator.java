package com.plectix.simulator.components.stories.storage;

import java.util.Iterator;

public interface IEventIterator extends Iterator<Long>
{
	CEvent value();
}

