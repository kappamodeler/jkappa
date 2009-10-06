package com.plectix.simulator.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileComparator {
	private final String myFirstPath;
	private final String mySecondPath;

	public FileComparator(String path1, String path2) {
		myFirstPath = path1;
		mySecondPath = path2;
	}

	/**
	 * 
	 * @return line, where the first difference found or -1 if there's no
	 *         difference
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public int compare() throws FileNotFoundException, IOException {
		BufferedReader myFirstReader = new BufferedReader(new FileReader(
				myFirstPath));
		BufferedReader mySecondReader = new BufferedReader(new FileReader(
				mySecondPath));
		String first;
		String second;
		int line = 1;
		try {
			first = myFirstReader.readLine();
			second = mySecondReader.readLine();
			for (; (first != null) && (second != null);) {
				if (!first.equals(second)) {
					return line;
				}
				first = myFirstReader.readLine();
				second = mySecondReader.readLine();
				line++;
			}
		} finally {
			myFirstReader.close();
			mySecondReader.close();
		}
		if ((first == null) && (second == null)) {
			return -1;
		} else {
			return line;
		}
	}
}
