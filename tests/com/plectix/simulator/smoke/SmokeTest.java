package com.plectix.simulator.smoke;

import java.io.File;

import org.junit.Test;

import com.plectix.simulator.util.DefaultPropertiesForTest;

public abstract class SmokeTest extends DefaultPropertiesForTest{
	protected static final String separator = File.separator;
	protected static final String inputDirectory = "test.data" + separator
			+ "smoke_test" + separator + "source" + separator;

	@Test
	public abstract void test() throws Exception;
	
	protected abstract String[] prepareTestArgs();
}
