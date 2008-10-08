package com.plectix.simulator.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.SimulationMain;

// get input strings from input file (or something else)  
// i.e. makes lists of strings with different prefix such as 
// %rule, %init, %obs

public class DataReading {

	private List<String> rules = new ArrayList<String>();
	// rules in the input

	private List<String> observables = new ArrayList<String>();
	// observables in the input

	private List<String> story = new ArrayList<String>();
	// story in the input

	private List<String> inits = new ArrayList<String>();
	// init conditions in the input

	private String filePatch = null; // "C:/workspace/Example.tmp";

	private static final int STRING_INITIAL_CONDITIONS_PREFIX = "%init: "
			.length(); // 7; // "%init: "
	private static final int STRING_SIMULATION_PREFIX = "%obs: ".length(); // 6;
	private static final int STRING_STORIFY_PREFIX = "%story: ".length(); // 8;

	public DataReading(String filename) {
		this.filePatch = filename;
	}

	public final void readData() throws IOException {
		// reading of the file
		// ....

		try {
			BufferedReader in = new BufferedReader(new FileReader(filePatch));
			String line;

			while ((line = in.readLine()) != null) {

				if (line.startsWith("#"))
					continue;

				if (line.startsWith("%story"))
					story.add(new String(line.substring(
							STRING_STORIFY_PREFIX, line.length())));
				else if (line.startsWith("%obs"))
					observables.add(new String(line.substring(
							STRING_SIMULATION_PREFIX, line.length())));
				else if (line.startsWith("%init")) {
					inits.add(new String(line.substring(
							STRING_INITIAL_CONDITIONS_PREFIX, line.length())));
				} else if (line.trim().length() > 0)
					rules.add(new String(line));

			}
			in.close();
		} catch (IOException e) {
			// TODO: remove the try block if not doing anything here...
			// System.err.println("File not found.");
			throw e;
		}

		// checking of the components of data
		if (rules.isEmpty())
			throw new IOException("There are no rules in the input data");
		else if (observables.isEmpty())
			throw new IOException("There are no observables in the input data");
		else if (inits.isEmpty())
			throw new IOException("There are no inits in the input data");
	}

	public final List<String> getInits() {
		return inits;
	}

	public final List<String> getObservables() {
		return observables;
	}

	public final List<String> getRules() {
		return rules;
	}

	public List<String> getStory() {
		return story;
	}

}
