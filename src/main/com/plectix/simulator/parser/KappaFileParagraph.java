package com.plectix.simulator.parser;

import java.util.*;

public class KappaFileParagraph {
	private Collection<KappaFileLine> myParagraph = new ArrayList<KappaFileLine>();
	
	public KappaFileParagraph() {
		
	}
	
	public void addLine(KappaFileLine line) { 
		myParagraph.add(line);
	}

	public boolean isEmpty() {
		return myParagraph.isEmpty();
	}
	
	public Collection<KappaFileLine> getLines() { 
		return Collections.unmodifiableCollection(myParagraph);
	}
}
