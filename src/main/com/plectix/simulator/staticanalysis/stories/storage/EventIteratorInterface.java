package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.Iterator;

public interface EventIteratorInterface extends Iterator<Long> {
	Event value();
}

