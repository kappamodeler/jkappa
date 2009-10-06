package com.plectix.simulator.stories;

import com.plectix.simulator.component.stories.storage.AbstractStorage;
import com.plectix.simulator.component.stories.storage.StoriesAgentTypesStorage;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.stories.unitTests.FakeEvent;

public class StorageBuilder {
	
	private final static int DEFAULT = 1 ;
	static AbstractStorage storage;
	
	StorageBuilder(){
		storage = new AbstractStorage(StorageBuilder.DEFAULT, new StoriesAgentTypesStorage()); 
	}
	
	public static void addFakeEvent(FakeEvent fe) throws StoryStorageException{
		storage.addEventContainer(fe);
	}
	
	public static void addLastFakeEvent(FakeEvent fe) throws StoryStorageException{
		storage.addLastEventContainer(fe, DEFAULT);
	}
	
	
	public AbstractStorage getStorage(){
		return storage;
	}
	

}
