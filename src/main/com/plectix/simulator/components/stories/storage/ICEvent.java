package com.plectix.simulator.components.stories.storage;

public interface ICEvent
{
	int getAtomicEventCount ();
	WireHashKey    getWireKey (int index);
	AtomicEvent<?> getAtomicEvent (int index);
	ETypeOfWire    getAtomicEventType (int index);
}
