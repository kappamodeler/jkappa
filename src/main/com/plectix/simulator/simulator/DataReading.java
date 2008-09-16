package com.plectix.simulator.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// get input strings from input file (or something else)  
// i.e. makes lists of strings with different preffics such as 
// %rule, %init, %obs

public class DataReading {

	private List<String> rules=new ArrayList<String>(); 
	// rules in the input

	private List<String> observables=new ArrayList<String>(); 
	// observables in the input

	private List<String> inits=new ArrayList<String>(); 
	// init conditions in the input

	private File file;

	private String filePatch = "C:/workspace/Example.tmp";

	private static final String STRING_RULES = "# Rules:";
	private static final String STRING_INITIAL_CONDITIONS = "# Initial Conditions:";
	private static final String STRING_SIMULATION = "# Simulation:";
	private static final byte STRING_INITIAL_CONDITIONS_PREFIX =7;	// "%init: "
	private static final byte STRING_SIMULATION_PREFIX =6; //"%obs: "
	

	private static final byte KEY_RULE = 1;
	private static final byte KEY_INITIAL_CONDITIONS = 2;
	private static final byte KEY_SIMULATION = 3;

	private byte keyList=0;
	public DataReading() {
int u=0;
u++;
	}

	public DataReading(String str) {
		this.filePatch = str;
	}

	public void ReadData() throws IOException {
		// reading of the file
		// ....

		// FileInputStream fr=new FileInputStream("C:/workspace/Example.tmp");

		try {
			BufferedReader in = new BufferedReader(new FileReader(filePatch));
			String line;

			while ((line = in.readLine()) != null) {

				// Checking line for containt main identifier
				// ("# Rules:","# Initial Conditions:",
				// "# Simulation:") and definition, where add next Data.
				if (checkType(line))
					continue;
				addData(line);
			}
			in.close();
		} catch (IOException e) {
			System.err.println("File not found.");
		}

		// checking of the components of data
		if (rules.isEmpty())
			throw new IOException("There are no rules in the input data");
		else if (observables.isEmpty())
			throw new IOException("There are no observables in the input data");
		else if (inits.isEmpty())
			throw new IOException("There are no inits in the input data");
	}

	// Add lines to necessary List.
	private void addData(String line) {
		if ((line.contains("#")) || line.equalsIgnoreCase(""))
			return;
		switch (keyList) {
		case KEY_RULE: {
			rules.add(new String(line));
			break;
		}
		case KEY_INITIAL_CONDITIONS: {
			inits.add(new String(line.substring(STRING_INITIAL_CONDITIONS_PREFIX, line.length())));
			break;
		}
		case KEY_SIMULATION: {
			observables.add(new String(line.substring(STRING_SIMULATION_PREFIX, line.length())));
			break;
		}
		}
	}

	// Checking line for containt main identifier
	// ("# Rules:","# Initial Conditions:",
	// "# Simulation:") and definition, where add next Data.
	private boolean checkType(String line) {
		if (line.equalsIgnoreCase(STRING_RULES)) {
			keyList = KEY_RULE;
			return true;
		}
		if (line.equalsIgnoreCase(STRING_INITIAL_CONDITIONS)) {
			keyList = KEY_INITIAL_CONDITIONS;
			return true;
		}
		if (line.equalsIgnoreCase(STRING_SIMULATION)) {
			keyList = KEY_SIMULATION;
			return true;
		}
		return false;
	}

	public List<String> getInits() {
		return inits;
	}

	public List<String> getObservables() {
		return observables;
	}

	public List<String> getRules() {
		return rules;
	}

}
