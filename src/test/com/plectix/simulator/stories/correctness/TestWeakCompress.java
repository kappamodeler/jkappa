package com.plectix.simulator.stories.correctness;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.compressions.WeakCompression;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.stories.InitStoriesTests;

public class TestWeakCompress extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
	// + separator + "simple"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}
	private String fileName;

	public TestWeakCompress(String fileName) {
		super(path, fileName, false, true);
		this.fileName = path + fileName;
		System.out.println("asdbegin began begun!!!");
	}
	
	
	@Test
	public void testWeakCompression() throws StoryStorageException{
		Map<Integer, IWireStorage> storages =  getStories()
				.getEventsMapForCurrentStory();
//		for (IWireStorage storage : storages.values()) {
//			WeakCompression wc = new WeakCompression(storage);
//			wc.process();
//		}
		System.out.println("\n\n");
		WeakCompressionBruteForse compress;
		System.out.println(storages.size());
		try {
			for (IWireStorage storage : storages.values()) {
				compress = new WeakCompressionBruteForse(storage);
				compress.bruteForse();
				compress = null;
			}
		} catch (StoryStorageException e) {
			e.printStackTrace();
		}

	}

}
