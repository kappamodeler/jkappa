package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.Collection;

public final class KappaFileParagraph {
	private final Collection<KappaFileLine> paragraphLines = new ArrayList<KappaFileLine>();
	
	public final void addLine(KappaFileLine line) { 
		paragraphLines.add(line);
	}

	public final boolean isEmpty() {
		return paragraphLines.isEmpty();
	}
	
	public final Collection<KappaFileLine> getLines() { 
		return paragraphLines;
	}
}
