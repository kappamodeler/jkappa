package com.plectix.simulator.stories;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.stories.weakCompression.TestWeakCompression;


@RunWith(value = Suite.class)
@SuiteClasses(value = { 
//		TestStories.class,
//		TestStoryTrees.class
		TestStoryCorrectness.class
//		TestWeakCompression.class
		})
public class RunTestStories {

}

