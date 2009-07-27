package com.plectix.simulator.utilsForTest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadAndParserFile {

	private BufferedReader reader;
	private String spliter;
	private LinkedHashMap<String, Map<String, Integer>> mapData = new LinkedHashMap<String, Map<String,Integer>>(); 
	
	private static final String selector = "#"; 
	
	public ReadAndParserFile(String path, String spliter) {
		try {
			reader = new BufferedReader(new FileReader(path));
			this.spliter = spliter;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File for result : FileNotFoundException");
			e.printStackTrace();
		}
	}
	
	public void addTypeData(String typeData, Map<String, Integer> map) {
		mapData.put(typeData, map);
	}
	
	public String readLine() throws IOException {
		return reader.readLine();
	}
	
	public void close() throws IOException {
		reader.close();		
	}
	
	private void parseLine(String type, String line) {
		
		if(mapData.containsKey(type)) {
			String[] parseLine = line.split(spliter);
			if(parseLine.length == 2) {
				mapData.get(type).put(parseLine[0], Integer.valueOf(parseLine[1]));
			}
		}
		
	}

	public void parseFile() {
		try {
			String line = readLine(); 
			String type = "";
			while (line != null) {
				
				if(line.contains(selector)) {
					
					if(mapData.containsKey(line)) {
						type = line;
						line = readLine();
						continue;
					}
					
					type = "";
				}
				
				parseLine(type, line);

				line = readLine();
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
