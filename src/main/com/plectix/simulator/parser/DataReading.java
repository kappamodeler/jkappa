package com.plectix.simulator.parser;

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

	private static final String STRING_INITIAL_CONDITIONS_PREFIX = "%init"; //7;
	private static final String STRING_SIMULATION_PREFIX = "%obs"; // 6;
	private static final String STRING_STORIFY_PREFIX = "%story"; // 8;
	private static final String STRING_MOD_PREFIX = "%mod"; // 6;

	public DataReading(String filename) {
		this.filePatch = filename;
	}

	private boolean startsWithWhiteSpace(String str) {
		return str.startsWith(" ") || str.startsWith("\t");
	}

	private final String handleModifier(int index, String line, String modifier)
			throws ParseErrorException {

		String significant = line;

		if (!line.startsWith(modifier)) {
			throw new ParseErrorException(new CDataString(index, line), "'"
					+ modifier + "' expected in line : " + line);
		} else {
			significant = line.substring(modifier.length());
		}

		boolean separated = false;
		if (startsWithWhiteSpace(significant)) {
			significant = significant.trim();
			separated = true;
		}

		if (significant.startsWith(":")) {
			significant = significant.substring(1).trim();
		} else {
			if (!separated) {
				throw new ParseErrorException(new CDataString(index, line), "'"
						+ modifier + "' expected in line : " + line);
			}
		}

		return significant;
	}

	public final void readData() throws FileReadingException,
			ParseErrorException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePatch));
			String line;

			int index = 0;
			while ((line = in.readLine()) != null) {
				index++;
				if (line.startsWith("#"))
					continue;
				if (line.indexOf("#") != -1)
					line = dellComment(line);

				if (line.indexOf("\\") != -1) {
					String nextLine;
					nextLine = in.readLine().trim();
					line = line.replace("\\", "");
					line = line + nextLine;
				}

				if (line.startsWith(STRING_MOD_PREFIX)) {
					String significant = handleModifier(index, line,
							STRING_MOD_PREFIX);
					mods.add(new CDataString(index, significant));
				} else if (line.startsWith("%story")) {
					String significant = handleModifier(index, line,
							STRING_STORIFY_PREFIX);
					story.add(new CDataString(index, significant));
				} else if (line.startsWith(STRING_SIMULATION_PREFIX)) {
					String significant = handleModifier(index, line,
							STRING_SIMULATION_PREFIX);
					observables.add(new CDataString(index, significant));
				} else if (line.startsWith(STRING_INITIAL_CONDITIONS_PREFIX)) {
					String significant = handleModifier(index, line,
							STRING_INITIAL_CONDITIONS_PREFIX);
					inits.add(new CDataString(index, significant));
				} else if (line.trim().length() > 0) {
					rules.add(new CDataString(index, new String(line)));
				}

			}
			in.close();
		} catch (IOException e) {
			throw new FileReadingException(e.getMessage());
		}

		// checking of the components of data
		if (rules.isEmpty())
			throw new FileReadingException(
					"There are no rules in the input data");
		else if (inits.isEmpty())
			throw new FileReadingException(
					"There are no inits in the input data (use '%init:')");
	}

	private final String dellComment(String line) {
		String st = new String(line);

		int indexComment = st.lastIndexOf("#");
		if (indexComment == -1)
			return line;
		int indexStart = st.indexOf("'");
		if (indexStart == -1)
			line.substring(0, indexComment);

		byte count = 0;
		int index = 0;
		for (char ch : line.toCharArray()) {
			if (ch == "'".hashCode())
				count++;
			if (ch == "#".hashCode())
				if (count != 1) {
					return line.substring(0,index);
				}
			if(count==2)
				count=0;
			index++;

		}

		return line;

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
