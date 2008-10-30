package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.util.RunningMetric;

public class SimulationData {
	private static List<Double> timeStamps;
	private static List<ArrayList<RunningMetric>> runningMetrics;

	private List<CRule> rules;
	private CStories stories = null;
	private List<CPerturbation> perturbations;

	private CObservables observables = new CObservables();
	private Double intialTime = 0.0;

	private Double rescale = 1.0;
	private Integer points = -1;
	private double timeLength = 0;
	private int seed = 0;
	private String xmlSessionName = "simplx.xml";
	private String tmpSessionName = "simplx.tmp";
	private boolean activationMap = true;

	private double snapshotTime = -1.;

	public double getSnapshotTime() {
		return snapshotTime;
	}

	public void setSnapshotTime(double snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	private String randomizer;
	private int iterations = 0;

	private long event;

	private long numPoints;
	private ISolution solution = new CSolution(); // soup of initial components
	private boolean compile = false;
	private boolean storify = false;

	private CSnapshot snapshot = null;

	public CSnapshot getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(CSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	public CObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public final void setStories(CStories list) {
		this.stories = list;
	}

	public final void addStories(String name) {
		byte index = 0;
		List<Integer> ruleIDs = new ArrayList<Integer>();
		for (CRule rule : rules) {
			if ((rule.getName() != null)
					&& (rule.getName().startsWith(name) && ((name.length() == rule
							.getName().length()) || ((rule.getName()
							.startsWith(name + "_op")) && ((name.length() + 3) == rule
							.getName().length()))))) {
				ruleIDs.add(rule.getRuleID());
				index++;
			}
			if (index == 2){
				this.stories.addToStories(ruleIDs);
				return;
			}
		}
		this.stories.addToStories(ruleIDs);
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

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
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

	public String getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(String randomizer) {
		this.randomizer = randomizer;
	}

	public final int getIterations() {
		return iterations;
	}

	public final void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public final List<CPerturbation> getPerturbations() {
		return perturbations;
	}

	public final void setPerturbations(List<CPerturbation> perturbations) {
		this.perturbations = perturbations;
	}

	public final static List<Double> getTimeStamps() {
		return timeStamps;
	}

	public final static void setTimeStamps(List<Double> timeStamps) {
		SimulationData.timeStamps = timeStamps;
	}

	public final static List<ArrayList<RunningMetric>> getRunningMetrics() {
		return runningMetrics;
	}

	public final static void setRunningMetrics(
			List<ArrayList<RunningMetric>> runningMetrics) {
		SimulationData.runningMetrics = runningMetrics;
	}

	public final void initIterations() {
		SimulationData.timeStamps = new ArrayList<Double>();
		SimulationData.runningMetrics = new ArrayList<ArrayList<RunningMetric>>();
		int observable_num = observables.getConnectedComponentList().size();
		for (int i = 0; i < observable_num; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}

	}

	public final String getTmpSessionName() {
		return tmpSessionName;
	}

	public final void setTmpSessionName(String tmpSessionName) {
		this.tmpSessionName = tmpSessionName;
	}

	public final boolean isActivationMap() {
		return activationMap;
	}

	public final void setActivationMap(boolean activationMap) {
		this.activationMap = activationMap;
	}

	public final Double getIntialTime() {
		return intialTime;
	}

	public final void setIntialTime(double intialTime) {
		this.intialTime = intialTime;
	}

	public final Double getRescale() {
		return rescale;
	}

	public final void setRescale(double rescale) {
		if ((rescale > 10.0) || (rescale < 0.10))
			this.rescale = 1.0;
		else
			this.rescale = rescale;
	}

	public final Integer getPoints() {
		return points;
	}

	public final void setPoints(int points) {
		this.points = points;
	}
}
