package com.plectix.simulator.simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.plectix.simulator.components.contactMap.CContactMap;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class KappaSystem {
	private List<CRule> rules = null;
	private CStories stories = null;
	private List<CPerturbation> perturbations = null;
	private CObservables observables = new CObservables();
	private ISolution solution;// = new CSolution(); // soup of initial
	// components
	private CContactMap contactMap = new CContactMap();

	private final IdGenerator myAgentsIdGenerator = new IdGenerator();
	private final IdGenerator myRuleIdGenerator = new IdGenerator();

	private final SimulationData mySimulationData;

	public KappaSystem(SimulationData data) {
		mySimulationData = data;
	}

	public final void initialize(InfoType outputType) {
		SimulationArguments args = mySimulationData.getSimulationArguments();
		if (args.getSerializationMode() == SimulationArguments.SerializationMode.READ) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(args
						.getSerializationFileName()));
				solution = (ISolution) ois.readObject();
				rules = (List<CRule>) ois.readObject();
				observables = (CObservables) ois.readObject();
				perturbations = (List<CPerturbation>) ois.readObject();
				mySimulationData.setSnapshotTimes((List<Double>) ois
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
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeObject(mySimulationData.getSnapshotTimes());
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
			mySimulationData.addInfo(outputType, InfoType.INFO,
					"--Abstracting activation map...");

			timer.startTimer();
			for (CRule rule : rules) {
				rule.updateActivatedRulesList(rules);
				rule.initializeActivatedObservablesList(observables);
			}
			mySimulationData.stopTimer(outputType, timer, "--Abstraction:");
			mySimulationData.addInfo(outputType, InfoType.INFO,
					"--Activation map computed");
		}

		if (args.isInhibitionMap()) {
			PlxTimer timer = new PlxTimer();
			mySimulationData.addInfo(outputType, InfoType.INFO,
					"--Abstracting inhibition map...");

			timer.startTimer();
			for (CRule rule : rules) {
				rule.updateInhibitedRulesList(rules);
				rule.initializeInhibitedObservablesList(observables);
			}
			mySimulationData.stopTimer(outputType, timer, "--Abstraction:");
			mySimulationData.addInfo(outputType, InfoType.INFO,
					"--Inhibition map computed");
		}

		//!!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			(new InjectionsBuilder(this)).build();
		}
		
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!

		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {

			contactMap.initAbstractSolution();
			contactMap.constructAbstractRules(rules);
			contactMap.constructAbstractContactMap();

			// contactMap.constructReachableRules(rules);
			// contactMap.constructContactMap();
		}
	}

	// ---------------------POSITIVE UPDATE-----------------------------

	public final void doPositiveUpdate(CRule rule,
			List<CInjection> currentInjectionsList) {
		if (mySimulationData.getSimulationArguments().isActivationMap()) {
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
		return Collections.unmodifiableList(rules);
	}

	public final CRule getRulesByID(int ruleID) {
		for (CRule rule : rules)
			if (rule.getRuleID() == ruleID)
				return rule;
		return null;
	}

	public final ISolution getSolution() {
		return solution;
	}

	public final CObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public CContactMap getContactMap() {
		return contactMap;
	}

	public final long generateNextRuleId() {
		return myRuleIdGenerator.generateNext();
	}

	public final long generateNextAgentId() {
		return myAgentsIdGenerator.generateNext();
	}

	public final List<CPerturbation> getPerturbations() {
		return Collections.unmodifiableList(perturbations);
	}

	// ----------------------SETTERS / ADDERS-------------------------------

	public final void setRules(List<CRule> rules) {
		this.rules = rules;
	}

	public final void addRule(CRule rule) {
		rules.add(rule);
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
		for (CRule rule : rules) {
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
		myAgentsIdGenerator.reset();
		myRuleIdGenerator.reset();
	}

	public void clearRules() {
		rules.clear();
	}

	public void clearPerturbations() {
		perturbations.clear();
	}
}
