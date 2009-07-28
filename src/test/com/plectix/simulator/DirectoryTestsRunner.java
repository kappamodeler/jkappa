package com.plectix.simulator;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class DirectoryTestsRunner extends TestRunner {
	public static Collection<Object[]> getAllTestFileNames(String prefix) {
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		try {
			File testFolder = new File(prefix);
			if (testFolder.isDirectory()) {
				for (String fileName : testFolder.list()) {
					if (fileName.startsWith("test")) {
						parameters.add(new Object[] { fileName });
					}
				}
			}
		} catch (Exception e) {
			org.junit.Assert.fail("Cannot instantiate fileName parameters");
		}
		//return Collections.unmodifiableList(parameters);
		return parameters;
	}

	public abstract String getPrefixFileName();
}
