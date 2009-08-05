package com.plectix.simulator.stories;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;

public class TestStoryCorrectness extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
	// + separator + "simple"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(path);
	}

	private String fileName;

	public TestStoryCorrectness(String fileName) {

		//3 boolean variables: isSlow, isWeak, isStrong 
		super(path, fileName, false, true, false);

		this.fileName = fileName;
	}

	@Test
	public void testCorrectness() {
		System.out
				.println("\n\n**************************************fileName = "
						+ fileName);
		CStories stories = getStories();
		Map<Integer, IWireStorage> storages = stories
				.getEventsMapForCurrentStory();
		try {
			for (IWireStorage storage : storages.values()) {
				if (!storage.isImportantStory())
					continue;

//				printStorage(storage);
				StoryCorrectness.testStorage(storage);

				//StoryCorrectness.testWires(storage);
				StoryCorrectness.testOfStates(storage);
				StoryCorrectness.testOfParallelCorrectness(storage);
				StoryCorrectness.testInternalStatesIterator(storage);
				StoryCorrectness.testWireWithMinUnresolved(storage);
				StoryCorrectness.testCompareStorageMaps(storage);
				StoryCorrectness.testCountUnresolvedOnWire(storage);

			}
		} catch (StoryStorageException e) {
			e.printStackTrace();
		}

	}

	

	

	

	

	

	
}

