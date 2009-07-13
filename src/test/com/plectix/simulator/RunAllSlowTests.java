package com.plectix.simulator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.plectix.simulator.XMLmaps.TestContactMap;
import com.plectix.simulator.smiles.RunSmilesTest;

@RunWith(value=Suite.class)
@SuiteClasses(value = {
		RunSmilesTest.class
//		TestContactMap.class
	})
public class RunAllSlowTests {

}
