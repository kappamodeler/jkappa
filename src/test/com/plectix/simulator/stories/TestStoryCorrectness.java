package com.plectix.simulator.stories;

import java.io.File;
import java.util.Collection;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.enums.EActionOfAEvent;
import com.plectix.simulator.components.stories.enums.EState;
import com.plectix.simulator.components.stories.enums.ETypeOfWire;
import com.plectix.simulator.components.stories.storage.AState;
import com.plectix.simulator.components.stories.storage.AtomicEvent;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.components.stories.storage.CStateOfLink;
import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.components.stories.storage.IEventIterator;
import com.plectix.simulator.components.stories.storage.WireHashKey;
import com.plectix.simulator.stories.InitStoriesTests;

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
		super(path, fileName, false, true, true);

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

