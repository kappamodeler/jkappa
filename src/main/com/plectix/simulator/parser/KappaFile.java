package com.plectix.simulator.parser;

import java.util.*;

public class KappaFile {
	private KappaFileParagraph myRules = new KappaFileParagraph();
	private KappaFileParagraph myObservables = new KappaFileParagraph();
	private KappaFileParagraph myStories = new KappaFileParagraph();
	private KappaFileParagraph myInitialSolution = new KappaFileParagraph();
	private KappaFileParagraph myModConditions = new KappaFileParagraph();
	
	public boolean hasNoRules() {
		return myRules.isEmpty();
	}
	
	//----------------------ADDERS =)------------------------------
	
	public void addRuleLine(KappaFileLine line) {
		myRules.addLine(line);
	}
	
	public void addObservableLine(KappaFileLine line) {
		myObservables.addLine(line);
	}
	
	public void addStoryLine(KappaFileLine line) {
		myStories.addLine(line);
	}
	
	public void addInitialSolutionLine(KappaFileLine line) {
		myInitialSolution.addLine(line);
	}
	
	public void addModLine(KappaFileLine line) {
		myModConditions.addLine(line);
	}
	
	//----------------------GETTERS-------------------------------
	
	public KappaFileParagraph getRules() {
		return myRules;
	}

	public KappaFileParagraph getObservables() {
		return myObservables;
	}

	public KappaFileParagraph getStories() {
		return myStories;
	}

	public KappaFileParagraph getInitialSolution() {
		return myInitialSolution;
	}

	public KappaFileParagraph getModConditions() {
		return myModConditions;
	}

	
	
}
