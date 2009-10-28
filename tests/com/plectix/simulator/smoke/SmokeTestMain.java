package com.plectix.simulator.smoke;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		TestENG229.class,
		TestENG310.class
		})
public class SmokeTestMain {

}