package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.interfaces.ISolution;

public class SimulationData {
	private List<CRule> rules;
	private List<CStories> stories;

	private CObservables observables = new CObservables();
	private double intialTime;
	private double timeLength = 0;
	private double seed = 0;
	private String xmlSessionName = "simplx.xml";

	private long event;

	private long numPoints;
	private ISolution solution = new CSolution(); // soup of initial components
	private boolean compile = false;
	private boolean storify = false;

	public CObservables getObservables() {
		return observables;
	}

	public final List<CStories> getStories() {
		return stories;
	}

	public final void setStories(List<CStories> list) {
		this.stories = list;
	}

	public final void addStories(String name) {
		byte index = 0;
		for (CRule rule : rules) {
			if (rule.getName().startsWith(name)
					&& ((name.length() == rule.getName().length()) || ((rule
							.getName().startsWith(name + "_op")) && ((name
							.length() + 3) == rule.getName().length())))) {
				stories.add(new CStories(rule.getRuleID()));
				index++;
			}
			if (index == 2)
				return;
		}
	}

	public boolean isStorify() {
		return storify;
	}

	public void setStorify(boolean storify) {
		this.storify = storify;
	}

	public String getXmlSessionName() {
		return xmlSessionName;
	}

	public void setXmlSessionName(String xmlSessionName) {
		this.xmlSessionName = xmlSessionName;
	}

	public double getSeed() {
		return seed;
	}

	public void setSeed(double seed) {
		this.seed = seed;
	}

	public void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
	}

	public final void initializeLifts() {
		// creates lifts for all rules
	}

	public final void initializeInjections() {
		// creates injections for all rules
	}

	public final double getTimeLength() {
		// TODO Auto-generated method stub
		return timeLength;
	}

	public final ISolution getSolution() {
		return solution;
	}

	public final void setRules(List<CRule> rules) {
		this.rules = rules;
	}

	public final List<CRule> getRules() {
		return rules;
	}

	public boolean isCompile() {
		return compile;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}

	public final long getEvent() {
		return event;
	}

	public final void setEvent(long event) {
		this.event = event;
	}
}
