package com.plectix.simulator.stories.correctness;


import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.stories.InitStoriesTests;

public class TestWeakCompression extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
		+ separator + "simple1"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}
	private String fileName;

	public TestWeakCompression(String fileName) {
		super(path, fileName, false, true);
		this.fileName = path + fileName;
		System.out.println("begin began begun!!!");
	}
	
	
	@Test
	public void testWeakCompression() throws StoryStorageException{
		System.out.println("test weak compression");
		Map<Integer, IWireStorage> storages =  getStories()
				.getEventsMapForCurrentStory();
		WeakCompressionBruteForse compress;
		System.out.println("number of storages:  " + storages.size());
//		IWireStorage st = new AbstractStorage(StoryCompressionMode.WEAK);
		
			for (IWireStorage storage : storages.values()) {
				System.out.println("size of storage (events): " + storage.getWiresByEvent().size());
//				assertFalse(storage.getWiresByEvent().isEmpty());
//				assertFalse(storage.getStorageWires().isEmpty());
				if (!storage.getWiresByEvent().isEmpty()){
					compress = new WeakCompressionBruteForse(storage);
					compress.bruteForse();
				}
				compress = null;
			}

	}

}
