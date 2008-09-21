package com.plectix.simulator.simulator;

import java.io.BufferedReader;
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


	private String filePatch = "C:/workspace/Example.tmp";


	private static final byte STRING_INITIAL_CONDITIONS_PREFIX =7;	// "%init: "
	private static final byte STRING_SIMULATION_PREFIX =6; //"%obs: "
	
	public DataReading(String str) {
		this.filePatch = str;
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
				
				if(line.startsWith("%obs")) {
					observables.add(new String(line.substring(STRING_SIMULATION_PREFIX, line.length())));
				} else if (line.startsWith("%init")) {
					inits.add(new String(line.substring(STRING_INITIAL_CONDITIONS_PREFIX, line.length())));
				} else 
					if(line.trim().length()>0)
						rules.add(new String(line));
	
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

	public final List<String> getInits() {
		return inits;
	}

	public final List<String> getObservables() {
		return observables;
	}

	public final List<String> getRules() {
		return rules;
	}

}
