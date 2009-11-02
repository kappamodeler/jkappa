package com.plectix.simulator.stories.weakcompression;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;
import com.plectix.simulator.stories.InitStoriesTests;

public class TestWeakCompressionByWires extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
	// + separator + "simple1"
			// + separator + "elementary"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}

	private String fileName;

	public TestWeakCompressionByWires(String fileName) {
		super(path, fileName, false, true, false, true, null);
		this.fileName = path + fileName;
	}

	@Test
	public void testWeakCompression() throws StoryStorageException {
		// System.out.println("_____________test: " + fileName);
		Map<Integer, WireStorageInterface> storages = getStories()
				.getEventsMapForCurrentStory();
		BruteForseByWires compress;
		// System.out.println("number of storages:  " + storages.size());

		for (WireStorageInterface storage : storages.values()) {
			int counter = 0;
			if (!storage.getEvents().isEmpty()) {
				compress = new BruteForseByWires(storage, getKappaSystem());
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
