package com.plectix.simulator.parser;

import java.io.*;

public class KappaFileReader extends Parser<KappaFile> {

	private static final String STRING_INITIAL_CONDITIONS_PREFIX = "%init"; //7;
	private static final String STRING_SIMULATION_PREFIX = "%obs"; // 6;
	private static final String STRING_STORIFY_PREFIX = "%story"; // 8;
	private static final String STRING_MOD_PREFIX = "%mod"; // 6;
	
	public KappaFileReader(String path) {
		super(path);
	}

	@Override
	protected KappaFile unsafeParse() throws FileReadingException, 
				ParseErrorException, IOException {
		
		KappaFile kappaFile = new KappaFile();

		EasyFileReader fileReader = getFileReader();
		String line;
		int index = 0;
		while ((line = fileReader.getLine()) != null) {
			index++;
			if (line.startsWith("#"))
				continue;
			if (line.indexOf("#") != -1)
				line = dellComment(line);

			if (line.indexOf("\\") != -1) {
				String nextLine;
				nextLine = fileReader.getLine().trim();
				line = line.replace("\\", "");
				line = line + nextLine;
			}

			if (line.startsWith(STRING_MOD_PREFIX)) {
				String significant = handleModifier(index, line,
						STRING_MOD_PREFIX);
				kappaFile.addModLine(new KappaFileLine(index, significant));
			} else if (line.startsWith("%story")) {
				String significant = handleModifier(index, line,
						STRING_STORIFY_PREFIX);
				kappaFile.addStoryLine(new KappaFileLine(index, significant));
			} else if (line.startsWith(STRING_SIMULATION_PREFIX)) {
				String significant = handleModifier(index, line,
						STRING_SIMULATION_PREFIX);
				kappaFile.addObservableLine(new KappaFileLine(index, significant));
			} else if (line.startsWith(STRING_INITIAL_CONDITIONS_PREFIX)) {
				String significant = handleModifier(index, line,
						STRING_INITIAL_CONDITIONS_PREFIX);
				kappaFile.addInitialSolutionLine(new KappaFileLine(index, significant));
			} else if (line.trim().length() > 0) {
				kappaFile.addRuleLine(new KappaFileLine(index, new String(line)));
			}

		}

		if (kappaFile.hasNoRules())
			throw new FileReadingException(
					"There are no rules in the input data");

		return kappaFile;
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
					return line.substring(0, index);
				}
			if (count == 2)
				count = 0;
			index++;

		}
		return line;
	}

	private final String handleModifier(int index, String line, String modifier)
			throws ParseErrorException {

		String significant = line;

		if (!line.startsWith(modifier)) {
			throw new ParseErrorException(new KappaFileLine(index, line), "'"
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
				throw new ParseErrorException(new KappaFileLine(index, line),
						"'" + modifier + "' expected in line : " + line);
			}
		}

		return significant;
	}

	private final boolean startsWithWhiteSpace(String str) {
		return str.startsWith(" ") || str.startsWith("\t");
	}
	
}
