package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.parser.abstractmodel.observables.ModelObservables;
import com.plectix.simulator.util.IdGenerator;

public final class KappaModel {
	private List<ModelPerturbation> perturbations = new ArrayList<ModelPerturbation>();
	private ModelObservables observables = new ModelObservables();
	private ModelSolution solution = new ModelSolution();
	private ModelStories stories = null;
	private Collection<ModelRule> rules = new ArrayList<ModelRule>();
	
	private final IdGenerator agentIdGenerator = new IdGenerator();
	private final IdGenerator ruleIdGenerator = new IdGenerator();
	
	public final IdGenerator getAgentIdGenerator() {
		return agentIdGenerator;
	}
	
	public final IdGenerator getRuleIdGenerator() {
		return ruleIdGenerator;
	}
	
	public final ModelObservables getObservables() {
		return observables;
	}

	public final Collection<ModelRule> getRules() {
		return rules;
	}

	public final ModelSolution getSolution() {
		return solution;
	}

	public final void setSolution(ModelSolution solution) {
		this.solution = solution;
	}
	
	public final void setStories(ModelStories stories) {
		this.stories = stories;
	}

	public final void setRules(Collection<ModelRule> rules) {
		this.rules = rules;
	}

	public final void addRule(ModelRule rule) {
		this.rules.add(rule);
	}
	
	public final ModelStories getStories() {
		return stories;
	}

	public final void setObservables(ModelObservables observables) {
		this.observables = observables;
	}
	
	public final void setPerturbations(List<ModelPerturbation> perturbations) {
		this.perturbations = perturbations;
	}
	
	public final List<ModelPerturbation> getPerturbations() {
		return perturbations;
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		for (ModelRule rule : rules) {
			sb.append(rule + "\n");
		}
		sb.append("\n" + solution + "\n");
		sb.append(observables + "\n");
		sb.append(stories + "\n");
		for (ModelPerturbation perturbation : perturbations) {
			sb.append(perturbation + "\n");
		}
		return sb.toString();
	}
}
