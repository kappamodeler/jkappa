package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.abstractmodel.observables.AbstractObservables;
import com.plectix.simulator.parser.util.IdGenerator;

public class KappaModel {
	
	private List<AbstractPerturbation> myPerturbations = new ArrayList<AbstractPerturbation>();
	
	private AbstractObservables myObservables = new AbstractObservables();
	private AbstractSolution mySolution = new AbstractSolution(); // soup of initial components
	private AbstractStories myStories = null;
	
	private Collection<AbstractRule> myRules = new ArrayList<AbstractRule>();
	
	private final IdGenerator myAgentIdGenerator = new IdGenerator();
	private final IdGenerator myRuleIdGenerator = new IdGenerator();
	
	public IdGenerator getAgentIdGenerator() {
		return myAgentIdGenerator;
	}
	
	public IdGenerator getRuleIdGenerator() {
		return myRuleIdGenerator;
	}
	
	public AbstractObservables getObservables() {
		return myObservables;
	}

	public Collection<AbstractRule> getRules() {
		return myRules;
	}

	public AbstractSolution getSolution() {
		return mySolution;
	}

	public void setSolution(AbstractSolution solution) {
		mySolution = solution;
	}
	
	public void setStories(AbstractStories stories) {
		myStories = stories;
	}

	public void setRules(Collection<AbstractRule> rules) {
		myRules = rules;
	}

	public void addRule(AbstractRule rule) {
		myRules.add(rule);
	}
	
	public AbstractStories getStories() {
		return myStories;
	}

	public void setObservables(AbstractObservables observables) {
		myObservables = observables;
	}
	
	public void setPerturbations(List<AbstractPerturbation> perturbations) {
		myPerturbations = perturbations;
	}
	
	public List<AbstractPerturbation> getPerturbations() {
		return myPerturbations;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (AbstractRule rule : myRules) {
			sb.append(rule + "\n");
		}
		sb.append("\n" + mySolution + "\n");
		sb.append(myObservables + "\n");
		sb.append(myStories + "\n");
		for (AbstractPerturbation perturbation : myPerturbations) {
			sb.append(perturbation + "\n");
		}
		return sb.toString();
	}
}
