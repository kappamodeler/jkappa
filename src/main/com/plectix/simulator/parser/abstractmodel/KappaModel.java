package com.plectix.simulator.parser.abstractmodel;

import java.util.*;

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
		return Collections.unmodifiableCollection(myRules);
	}

	public AbstractSolution getSolution() {
		return mySolution;
	}

	public void setSolution(AbstractSolution solution) {
		mySolution = solution;
	}
	
	public void setPerturbations(List<AbstractPerturbation> perturbations) {
		myPerturbations = perturbations;
	}

	public List<AbstractPerturbation> getPerturbations() {
		return Collections.unmodifiableList(myPerturbations);
	}
	
	public void setStories(AbstractStories stories) {
		myStories = stories;
	}

	public void addStories(String name) {
		byte index = 0;
		List<Integer> ruleDs = new ArrayList<Integer>();
		for (AbstractRule rule : myRules) {
			if ((rule.getName() != null)
					&& (rule.getName().startsWith(name) && ((name.length() == rule
							.getName().length()) || ((rule.getName()
							.startsWith(name + "_op")) && ((name.length() + 3) == rule
							.getName().length()))))) {
				ruleDs.add(rule.getID());
				index++;
			}
			if (index == 2) {
				myStories.addToStories(ruleDs);
				return;
			}
		}
		myStories.addToStories(ruleDs);
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
}
