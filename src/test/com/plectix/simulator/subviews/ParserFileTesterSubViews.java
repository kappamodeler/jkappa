package com.plectix.simulator.subviews;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ParserFileTesterSubViews {

	private BufferedReader reader;
	private String spliter = " ";

	public ParserFileTesterSubViews(String path) {
		try {
			reader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	public String readLine() throws IOException {
		return reader.readLine();
	}

	public void close() throws IOException {
		reader.close();
	}

	// private String[] parseLine(String line) {
	//		
	// String[] parseLine = line.split(" ");
	// if(parseLine.length == 2) {
	// subViewsrClassesMap.put(parseLine[0], Integer.valueOf(parseLine[1]));
	// }
	//		
	// }

	public void parseFile(Map<String, Integer> classesAgentsMap,
			Map<String, Integer> subViewsrClassesMap) {

		try {

			String line = readLine();

			int type = -1;

			while (line != null) {

				if (line.equals("#CLASS")) {
					type = 0;
					line = readLine();
					continue;
				}
				if (line.equals("#SUBVIEW")) {
					type = 1;
					line = readLine();
					continue;
				}
				if (type == 0) {

					String[] parseLine = line.split(" ");
					if (parseLine.length == 2) {
						classesAgentsMap.put(parseLine[0], Integer
								.valueOf(parseLine[1]));
					}
					line = readLine();
					continue;
				}
				if (type == 1) {

					String[] parseLine = line.split(" ");
					if (parseLine.length == 2) {
						subViewsrClassesMap.put(parseLine[0], Integer
								.valueOf(parseLine[1]));
					}
					line = readLine();
					continue;

				} else {
					type = -1;
				}

				line = readLine();

			}

			close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
