package com.plectix.simulator.harness;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
// TODO rename it somehow but RunAllFile_ISSUES_ENG_110 =)
public class RunTest {

	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator + "file_110"
			+ separator;

	private static final String pathForSourseFile = allPath + "all" + separator;
	private static final String pathForResults = allPath + "results"
			+ separator;

	private static final String suffixFiles = ".ka";

	private SimulatorTest sim;

	@Parameters
	public static Collection<Object[]> configs() {
		List<Object[]> listFile = new LinkedList<Object[]>();

		try {
			File testFolder = new File(pathForSourseFile);
			getDirectory(testFolder, listFile);
		} catch (Exception e) {
			e.printStackTrace();
			org.junit.Assert.fail("Cannot instantiate fileName parameters");
		}
		return listFile;
	}

	private static void getDirectory(File testFolder, List<Object[]> listFile) {
		File[] listFiles = testFolder.listFiles();
		for (File folders : listFiles) {
			if (folders.isDirectory()) {
				getDirectory(folders, listFile);
			}
			if (folders.isFile()) {
				String fileName = folders.getName();
				if (checkFile(fileName)) {
					listFile.add(new Object[] { folders.getPath() });
				}
			}
		}
	}

	private static boolean checkFile(String fileName) {
		return fileName.endsWith(suffixFiles);
	}

	private String getFileName(String path) {
		File file = new File(path);
		String fileName = file.getName();
		fileName = fileName.substring(0, (fileName.length() - suffixFiles
				.length()));
		return fileName;
	}

	public RunTest(String path) {

		String fileName = getFileName(path);

		sim = new SimulatorTest(path, pathForResults, fileName);
	}

	@Test
	public void testStartSimulator() throws InterruptedException {
		sim.start();
		sim.join();
	}
}
