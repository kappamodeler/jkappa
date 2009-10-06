package com.plectix.simulator.parser;

/**
 * Inner representation of kappa file content.
 * It consists of KappaFileParagraphs, which in their order, consist of KappaFileLines.
 * There are 5 different paragraphs, each for each purpose description:
 * rules, initial species, stories, observables and perturbations
 */
public final class KappaFile {
	private final KappaFileParagraph rulesParagraph = new KappaFileParagraph();
	private final KappaFileParagraph observablesParagraph = new KappaFileParagraph();
	private final KappaFileParagraph storiesParagraph = new KappaFileParagraph();
	private final KappaFileParagraph solutionParagraph = new KappaFileParagraph();
	private final KappaFileParagraph perturbationsParagraph = new KappaFileParagraph();
	
	public final boolean containsNoRules() {
		return rulesParagraph.isEmpty();
	}
	
	//----------------------ADDERS =)------------------------------
	
	public final void addRuleLine(KappaFileLine line) {
		rulesParagraph.addLine(line);
	}
	
	public final void addObservableLine(KappaFileLine line) {
		observablesParagraph.addLine(line);
	}
	
	public final void addStoryLine(KappaFileLine line) {
		storiesParagraph.addLine(line);
	}
	
	public final void addInitialSolutionLine(KappaFileLine line) {
		solutionParagraph.addLine(line);
	}
	
	public final void addPerturbationLine(KappaFileLine line) {
		perturbationsParagraph.addLine(line);
	}
	
	//----------------------GETTERS-------------------------------
	
	public final KappaFileParagraph getRules() {
		return rulesParagraph;
	}

	public final KappaFileParagraph getObservables() {
		return observablesParagraph;
	}

	public final KappaFileParagraph getStories() {
		return storiesParagraph;
	}

	public final KappaFileParagraph getSolution() {
		return solutionParagraph;
	}

	public final KappaFileParagraph getPerturbations() {
		return perturbationsParagraph;
	}
}
