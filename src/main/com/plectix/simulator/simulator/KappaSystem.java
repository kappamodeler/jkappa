package com.plectix.simulator.simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.subviews.CMainSubViews;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.probability.WeightedItemSelector;
import com.plectix.simulator.probability.skiplist.SkipListSelector;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class KappaSystem {
	private WeightedItemSelector<CRule> rules = new SkipListSelector<CRule>(ThreadLocalData.getRandom());
	private List<CRule> orderedRulesList = new ArrayList<CRule>();
	private CStories stories = null;
	private List<CPerturbation> perturbations = null;
	private CObservables observables = new CObservables();
	private ISolution solution;// = new CSolution(); // soup of initial components
	private CContactMap contactMap = new CContactMap();

	private final IdGenerator agentsIdGenerator = new IdGenerator();
	private final IdGenerator ruleIdGenerator = new IdGenerator();

	private final SimulationData simulationData;

	public KappaSystem(SimulationData data) {
		simulationData = data;
	}

	public final void initialize(InfoType outputType) {
		SimulationArguments args = simulationData.getSimulationArguments();
		if (args.getSerializationMode() == SimulationArguments.SerializationMode.READ) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(args
						.getSerializationFileName()));
				solution = (ISolution) ois.readObject();
				rules = (WeightedItemSelector<CRule>) ois.readObject();
				orderedRulesList = (List<CRule>) ois.readObject();
				observables = (CObservables) ois.readObject();
				perturbations = (List<CPerturbation>) ois.readObject();
				simulationData.setSnapshotTimes((List<Double>) ois
						.readObject());
				args.setEvent((long) ois.readLong());
				args.setTimeLength((double) ois.readDouble());
				ois.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (args.getSerializationMode() == SimulationArguments.SerializationMode.SAVE) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(args.getSerializationFileName()));
				oos.writeObject(solution);
				oos.writeObject(rules);
				oos.writeObject(orderedRulesList);
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeObject(simulationData.getSnapshotTimes());
				oos.writeLong(args.getEvent());
				oos.writeDouble(args.getTimeLength());
				oos.flush();
				oos.close();
				args
						.setSerializationMode(SimulationArguments.SerializationMode.READ);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		observables.init(args.getTimeLength(), args.getInitialTime(), args
				.getEvent(), args.getPoints(), args.isTime());
		List<CRule> rules = getRules();

		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			// contactMap.addCreatedAgentsToSolution(this.solution, rules);
			// contactMap.setSolution(this.solution);
		}

		observables.checkAutomorphisms();

		if (args.isActivationMap()) {
			PlxTimer timer = new PlxTimer();
			simulationData.addInfo(outputType, InfoType.INFO,
					"--Abstracting activation map...");

			timer.startTimer();
			for (CRule rule : rules) {
				rule.updateActivatedRulesList(rules);
				rule.initializeActivatedObservablesList(observables);
			}
			simulationData.stopTimer(outputType, timer, "--Abstraction:");
			simulationData.addInfo(outputType, InfoType.INFO,
					"--Activation map computed");
		}

		if (args.isInhibitionMap()) {
			PlxTimer timer = new PlxTimer();
			simulationData.addInfo(outputType, InfoType.INFO,
					"--Abstracting inhibition map...");

			timer.startTimer();
			for (CRule rule : rules) {
				rule.updateInhibitedRulesList(rules);
				rule.initializeInhibitedObservablesList(observables);
			}
			simulationData.stopTimer(outputType, timer, "--Abstraction:");
			simulationData.addInfo(outputType, InfoType.INFO,
					"--Inhibition map computed");
		}

		//!!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			(new InjectionsBuilder(this)).build();
		}
		
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!

		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {

//			contactMap.initAbstractSolution();
//			contactMap.constructAbstractRules(rules);
//			contactMap.constructAbstractContactMap();
			IAllSubViewsOfAllAgents subViews = new CMainSubViews();
			subViews.build(solution, rules);
			contactMap.initAbstractSolution();
			contactMap.constructAbstractRules(rules);
			contactMap.constructAbstractContactMapFromSubViews(subViews);
			// contactMap.constructReachableRules(rules);
			// contactMap.constructContactMap();
		}
	}

	// ---------------------POSITIVE UPDATE-----------------------------

	public final void doPositiveUpdate(CRule rule,
			List<CInjection> currentInjectionsList) {
		if (simulationData.getSimulationArguments().isActivationMap()) {
			SimulationUtils.positiveUpdate(rule.getActivatedRules(), rule
					.getActivatedObservable(), rule);
		} else {
			SimulationUtils.positiveUpdate(getRules(), observables
					.getConnectedComponentList(), rule);
		}

		List<CAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgents(freeAgents);
	}

	public final void doPositiveUpdateForContactMap(CRule rule,
			List<CInjection> currentInjectionsList, List<CRule> invokedRules) {
		SimulationUtils.positiveUpdateForContactMap(getRules(), rule,
				invokedRules);
		List<CAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgentsForContactMap(freeAgents, invokedRules);
	}

	private final void doPositiveUpdateForDeletedAgents(List<CAgent> agentsList) {
		for (CAgent agent : agentsList) {
			for (CRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					CInjection inj = cc.createInjection(agent);
					if (inj != null) {
						if (!agent.hasSimilarInjection(inj))
							cc.setInjection(inj);
					}
				}
			}
			for (IObservablesConnectedComponent obsCC : observables
					.getConnectedComponentList()) {
				CInjection inj = obsCC.createInjection(agent);
				if (inj != null) {
					if (!agent.hasSimilarInjection(inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	private final void doPositiveUpdateForDeletedAgentsForContactMap(
			List<CAgent> agentsList, List<CRule> rules) {
		for (CAgent agent : agentsList) {
			for (CRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					CInjection inj = cc.createInjection(agent);
					if (inj != null) {
						if (!agent.hasSimilarInjection(inj))
							cc.setInjection(inj);
					}
				}
				if (rule.canBeApplied() && !rule.includedInCollection(rules)) {
					rules.add(rule);
				}
			}
		}
	}

	// ------------------MISC--------------------------------

	public void setRules(List<CRule> rules2) {
		// TODO Auto-generated method stub
		orderedRulesList = rules2;
		rules.updatedItems(rules2);
	}
	
	public final void checkPerturbation(double currentTime) {
		if (perturbations.size() != 0) {
			for (CPerturbation pb : perturbations) {
				switch (pb.getType()) {
				case TIME: {
					if (!pb.isDo())
						pb.checkCondition(currentTime);
					break;
				}
				case NUMBER: {
					pb.checkCondition(observables);
					break;
				}
				case ONCE: {
					if (!pb.isDo())
						pb.checkConditionOnce(currentTime);
					break;
				}
				}

			}
		}
	}

	// ----------------------GETTERS-------------------------------

	public final List<CRule> getRules() {
//		if (rules != null) {
//			return rules.asSet();
//		} else { 
//			return null;
//		}
		return Collections.unmodifiableList(orderedRulesList);
	}

	public final CRule getRuleByID(int ruleID) {
		// TODO: We are scanning a list linearly, can't we use a HashMap here?
		for (CRule rule : orderedRulesList) {
			if (rule.getRuleID() == ruleID) {
				return rule;
			}
		}
		return null;
	}

//	public final CRule getRuleByNumber(int i) {
//		return orderedRulesList.get(i);
//	}
	
	public final ISolution getSolution() {
		return solution;
	}

	public final CObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public final CContactMap getContactMap() {
		return contactMap;
	}

	public final long generateNextRuleId() {
		return ruleIdGenerator.generateNext();
	}

	public final long generateNextAgentId() {
		return agentsIdGenerator.generateNext();
	}

	public final List<CPerturbation> getPerturbations() {
		return Collections.unmodifiableList(perturbations);
	}

	// ----------------------SETTERS / ADDERS-------------------------------

	public final void addRule(CRule rule) {
		orderedRulesList.add(rule);
		rules.updatedItem(rule);
	}

	public void setSolution(ISolution solution) {
		this.solution = solution;
	}

	public void setObservables(CObservables observables) {
		this.observables = observables;
	}

	public final void setStories(CStories stories) {
		this.stories = stories;
	}

	public final void addStories(String name) {
		byte index = 0;
		List<Integer> ruleIDs = new ArrayList<Integer>();
		for (CRule rule : orderedRulesList) {
			if ((rule.getName() != null)
					&& (rule.getName().startsWith(name) && ((name.length() == rule
							.getName().length()) || ((rule.getName()
							.startsWith(name + "_op")) && ((name.length() + 3) == rule
							.getName().length()))))) {
				ruleIDs.add(rule.getRuleID());
				index++;
			}
			if (index == 2) {
				this.stories.addToStories(ruleIDs);
				return;
			}
		}
		this.stories.addToStories(ruleIDs);
	}

	public final void setPerturbations(List<CPerturbation> perturbations) {
		this.perturbations = perturbations;
	}

	public void resetIdGenerators() {
		agentsIdGenerator.reset();
		ruleIdGenerator.reset();
	}

	public void clearRules() {
		rules = new SkipListSelector<CRule>(ThreadLocalData.getRandom());
		orderedRulesList.clear();
	}

	public void clearPerturbations() {
		perturbations.clear();
	}
	
	//--------------------METHODS FROM PROBABILITY CALCULATION--------------------
	
	public final CRule getRandomRule() {
		Set<CRule> updatedElements = new HashSet<CRule>();
		for (CRule rule : orderedRulesList) {
			double oldActivity = rule.getActivity();
			rule.calculateActivity();
			if (rule.getActivity() != oldActivity) {
				updatedElements.add(rule);
			}
		}
		rules.updatedItems(updatedElements);
		return rules.select();
	}
	
	public final double getTimeValue() {
		double randomValue = 0;

		while (randomValue == 0.0)
			randomValue = ThreadLocalData.getRandom().getDouble();

		return -1. / rules.getTotalWeight() * java.lang.Math.log(randomValue);
	}
	
//	private final void calculation() {
//		calculateRulesActivity();
//		recalculateCommonActivity();
//		calculateProbability();
//	}

//	private final int getRandomIndex() {
//
//		for (int i = 0; i < rulesProbability.length; i++) {
//			if (rules.get(i).isInfiniteRated() && (rules.get(i).getActivity()>0.0) 
//					&& (!(rules.get(i).isClashForInfiniteRule())))
//				return i;
//		}
//
//		for (int i = 0; i < rulesProbability.length; i++) {
//			if (randomValue < rulesProbability[i])
//				return i;
//		}
//		return -1;
//	}
//	
//	private final void recalculateCommonActivity() {
//		commonActivity = 0.;
//		for (CRule rule : rules) {
//			commonActivity += rule.getActivity();
//		}
//	}
	
//	private final void calculateRulesActivity() {
//		for (CRule rule : rules)
//			rule.calcultateActivity();
//	}
//
//	private final void calculateProbability() {
//		rulesProbability[0] = rules.get(0).getActivity() / commonActivity;
//		for (int i = 1; i < rulesProbability.length; i++) {
//			rulesProbability[i] = rulesProbability[i - 1]
//					+ rules.get(i).getActivity() / commonActivity;
//		}
//	}

	
}
