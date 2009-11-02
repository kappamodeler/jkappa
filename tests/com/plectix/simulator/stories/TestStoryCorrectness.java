package com.plectix.simulator.stories;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

public class TestStoryCorrectness extends InitStoriesTests {

	private static final String separator = File.separator;
	private static final String path = "test.data" + separator + "stories"
	// + separator + "simple"
			+ separator;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator.generate(getAllTestFileNames(path));
	}

	private String fileName;

	public TestStoryCorrectness(String fileName, Integer opMode) {

		// 3 boolean variables: isSlow, isWeak, isStrong isFirst mode
		super(path, fileName, true, false, true, true, opMode);

		this.fileName = fileName;
	}

	@Test
	public void testCorrectness() {
		System.out
				.println("\n\n**************************************fileName = "
						+ fileName);
		Stories stories = getStories();
		Map<Integer, WireStorageInterface> storages = stories
				.getEventsMapForCurrentStory();
		try {
			for (WireStorageInterface storage : storages.values()) {
				if (!storage.isImportantStory())
					continue;

				// printStorage(storage);
				StoryCorrectness.testStorage(storage);

				StoryCorrectness.testWires(storage);
				StoryCorrectness.testOfStates(storage);
				StoryCorrectness.testOfParallelCorrectness(storage);
				StoryCorrectness.testInternalStatesIterator(storage);
				StoryCorrectness.testWireWithMinUnresolved(storage);
				StoryCorrectness.testCompareStorageMaps(storage);
				StoryCorrectness.testCountUnresolvedOnWire(storage);
				StoryCorrectness.testLinks(storage);

			}
		} catch (StoryStorageException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

}
