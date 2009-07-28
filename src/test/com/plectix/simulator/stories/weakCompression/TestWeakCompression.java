package com.plectix.simulator.stories.weakCompression;


import static org.junit.Assert.assertTrue;

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
//		+ separator + "simple1"
//		+ separator + "123"
	
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}
	private String fileName;

	public TestWeakCompression(String fileName) {
		super(path, fileName, false, true, false);
		this.fileName = path + fileName;
	}
	
	
	@Test
	public void testWeakCompression() throws StoryStorageException{
		System.out.println("_____________test: " + fileName);
		Map<Integer, IWireStorage> storages =  getStories()
				.getEventsMapForCurrentStory();
		WeakCompressionVeryBruteForse compress;
		System.out.println("number of storages:  " + storages.size());
		
			for (IWireStorage storage : storages.values()) {
				int counter = 0;
				if (!storage.getEvents().isEmpty()){
					compress = new WeakCompressionVeryBruteForse(storage, getKappaSystem());
					compress.bruteForse();
				} else {
					counter++;
				}
				if (counter == storages.size())
					assertTrue("all storages are empty..", false);
				
				compress = null;
			}

	}

}
