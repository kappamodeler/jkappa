package com.plectix.simulator.util;

import java.util.*;
import java.io.*;

import org.junit.runner.RunWith;
import org.junit.runners.*;

@RunWith(Parameterized.class)
public abstract class DirectoryTestsRunner {

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
			System.err.println(e.getMessage());
		}
		return Collections.unmodifiableList(parameters);
	}

	public abstract String getPrefixFileName();
}
