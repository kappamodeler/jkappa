package com.plectix.simulator.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CDataString;

// get input strings from input file (or something else)  
// i.e. makes lists of strings with different prefix such as 
// %rule, %init, %obs

public class DataReading {

	private List<CDataString> rules = new ArrayList<CDataString>();
	// rules in the input

	private List<CDataString> observables = new ArrayList<CDataString>();
	// observables in the input

	private List<CDataString> story = new ArrayList<CDataString>();
	// story in the input

	private List<CDataString> inits = new ArrayList<CDataString>();
	// init conditions in the input

	private List<CDataString> mods = new ArrayList<CDataString>();
	// mod conditions in the input

	private String filePatch = null; // "C:/workspace/Example.tmp";

	private static final int STRING_INITIAL_CONDITIONS_PREFIX = "%init:"
			.length(); // 7; // "%init: "
	private static final int STRING_SIMULATION_PREFIX = "%obs:".length(); // 6;
	private static final int STRING_STORIFY_PREFIX = "%story:".length(); // 8;
	private static final int STRING_MOD_PREFIX = "%mod:".length(); // 6;

	public DataReading(String filename) {
		this.filePatch = filename;
	}

	public final void readData() throws IOException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePatch));
			String line;

			int index = 0;
			while ((line = in.readLine()) != null) {
				index++;
				if (line.startsWith("#"))
					continue;
				if (line.indexOf("#") != -1)
					line = line.substring(0, line.indexOf("#"));
				String nextLine;
				if (line.indexOf("\\") != -1) {
					nextLine = in.readLine().trim();
					line = line.replace("\\", "");
					line = line + nextLine;
				}

				if (line.startsWith("%mod"))
					mods.add(new CDataString(index, new String(line.substring(
							STRING_MOD_PREFIX, line.length()))));
				else if (line.startsWith("%story"))
					story.add(new CDataString(index, new String(line.substring(
							STRING_STORIFY_PREFIX, line.length()))));
				else if (line.startsWith("%obs"))
					observables.add(new CDataString(index,
							new String(line.substring(STRING_SIMULATION_PREFIX,
									line.length()))));
				else if (line.startsWith("%init")) {
					inits.add(new CDataString(index, new String(line.substring(
							STRING_INITIAL_CONDITIONS_PREFIX, line.length()))));
				} else if (line.trim().length() > 0)
					rules.add(new CDataString(index, new String(line)));

			}
			in.close();
		} catch (IOException e) {
			throw e;
		}

		// checking of the components of data
		if (rules.isEmpty())
			throw new IOException("There are no rules in the input data");
		else if (inits.isEmpty())
			throw new IOException("There are no inits in the input data");
	}

	public final List<CDataString> getInits() {
		return inits;
	}

	public final List<CDataString> getObservables() {
		return observables;
	}

	public final List<CDataString> getRules() {
		return rules;
	}

	public List<CDataString> getStory() {
		return story;
	}

	public final List<CDataString> getMods() {
		return mods;
	}

}
