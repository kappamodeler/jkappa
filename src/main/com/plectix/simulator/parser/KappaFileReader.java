package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * File reader, which ignores kappa-style comments.
 * @see EasyReader 
 * @author evlasov
 */
public class KappaFileReader extends Parser<KappaFile> {
	public KappaFileReader(String path, boolean isFilename) throws FileNotFoundException {
		super(path, isFilename);
	}
	
	public KappaFileReader(char[] buf) {
		super(buf);
	}
	
	@Override
	protected KappaFile unsafeParse() throws FileReadingException, 
				ParseErrorException, IOException, DocumentFormatException {
		
		KappaFile kappaFile = new KappaFile();

		EasyReader easyReader = getReader();
		String line;
		int index = 0;
		while ((line = easyReader.getLine()) != null) {
			index++;
			if (line.startsWith("#"))
				continue;
			if (line.indexOf("#") != -1)
				line = removeComment(line);

			if (line.indexOf("\\") != -1) {
				String nextLine;
				nextLine = easyReader.getLine().trim();
				line = line.replace("\\", "");
				line = line + nextLine;
			}

			KappaParagraphModifier modifier = KappaParagraphModifier.getValue(line);
			String significant = null;
			if (modifier != null)
				significant = handleModifier(index, line, modifier);
			
			if (modifier == KappaParagraphModifier.MOD_PREFIX) {
				kappaFile.addPerturbationLine(new KappaFileLine(index, significant));
			} else if (modifier == KappaParagraphModifier.STORIFY_PREFIX) {
				kappaFile.addStoryLine(new KappaFileLine(index, significant));
			} else if (modifier == KappaParagraphModifier.SIMULATION_PREFIX) {
				kappaFile.addObservableLine(new KappaFileLine(index, significant));
			} else if (modifier == KappaParagraphModifier.INITIAL_CONDITIONS_PREFIX) {
				kappaFile.addInitialSolutionLine(new KappaFileLine(index, significant));
			} else if (line.trim().length() > 0) {
				kappaFile.addRuleLine(new KappaFileLine(index, line));
			}

		}

		if (kappaFile.containsNoRules()) {
			throw new DocumentFormatException("There are no rules in the input data");
		}

		return kappaFile;
	}

	private final String removeComment(String line) {
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

	private final String handleModifier(int index, String line, KappaParagraphModifier mod)
			throws ParseErrorException {
		String modifier = mod.getString();

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
