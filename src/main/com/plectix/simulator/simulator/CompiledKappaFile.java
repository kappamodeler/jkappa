package com.plectix.simulator.simulator;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileParagraph;

public class CompiledKappaFile {
	private final KappaFile kappaFile;
	
	public CompiledKappaFile(KappaFile kappaFile) {
		this.kappaFile = kappaFile;
	}
	
	
	//---------GETTERS-----------------------------
	
	public final KappaFileParagraph getRules() {
		return kappaFile.getRules();
	}

	public final KappaFileParagraph getObservables() {
		return kappaFile.getObservables();
	}

	public final KappaFileParagraph getStories() {
		return kappaFile.getStories();
	}

	public final KappaFileParagraph getSolution() {
		return kappaFile.getSolution();
	}

	public final KappaFileParagraph getPerturbations() {
		return kappaFile.getPerturbations();
	}
}
