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

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.contactMap.CContactMap;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.initialization.StraightStorageInjectionBuilder;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class KappaSystem {
	private List<IRule> rules = null;
	private CStories stories = null;
	private List<CPerturbation> perturbations = null;
	private IObservables observables = new CObservables();
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
				rules = (List<IRule>) ois.readObject();
				observables = (IObservables) ois.readObject();
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
		List<IRule> rules = getRules();

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
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(observables);
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
			for (IRule rule : rules) {
				rule.createInhibitedRulesList(rules);
				rule.createInhibitedObservablesList(observables);
			}
			mySimulationData.stopTimer(outputType, timer, "--Abstraction:");
			mySimulationData.addInfo(outputType, InfoType.INFO,
					"--Inhibition map computed");
		}

		//!!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			(new StraightStorageInjectionBuilder(this)).build();
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

	public final void doPositiveUpdate(IRule rule,
			List<IInjection> currentInjectionsList) {
		if (mySimulationData.getSimulationArguments().isActivationMap()) {
			SimulationUtils.positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			SimulationUtils.positiveUpdate(getRules(), observables
					.getConnectedComponentList(), rule);
		}

		List<IAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgents(freeAgents);
	}

	public final void doPositiveUpdateForContactMap(IRule rule,
			List<IInjection> currentInjectionsList, List<IRule> invokedRules) {
		SimulationUtils.positiveUpdateForContactMap(getRules(), rule,
				invokedRules);
		List<IAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgentsForContactMap(freeAgents, invokedRules);
	}

	private final void doPositiveUpdateForDeletedAgents(List<IAgent> agentsList) {
		for (IAgent agent : agentsList) {
			for (IRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.createInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
			}
			for (IObservablesConnectedComponent obsCC : observables
					.getConnectedComponentList()) {
				IInjection inj = obsCC.createInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	private final void doPositiveUpdateForDeletedAgentsForContactMap(
			List<IAgent> agentsList, List<IRule> rules) {
		for (IAgent agent : agentsList) {
			for (IRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.createInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
				if (rule.isInvokedRule() && !rule.includedInCollection(rules)) {
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

	public final List<IRule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	public final IRule getRulesByID(int ruleID) {
		for (IRule rule : rules)
			if (rule.getRuleID() == ruleID)
				return rule;
		return null;
	}

	public final ISolution getSolution() {
		return solution;
	}

	public final IObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public CContactMap getContactMap() {
		return contactMap;
	}

	public final long generateNextRuleId() {
		return myRuleIdGenerator.generateNextAgentId();
	}

	public final long generateNextAgentId() {
		return myAgentsIdGenerator.generateNextAgentId();
	}

	public final List<CPerturbation> getPerturbations() {
		return Collections.unmodifiableList(perturbations);
	}

	// ----------------------SETTERS / ADDERS-------------------------------

	public final void setRules(List<IRule> rules) {
		this.rules = rules;
	}

	public final void addRule(CRule rule) {
		rules.add(rule);
	}

	public void setSolution(ISolution solution) {
		this.solution = solution;
	}

	public void setObservables(IObservables observables) {
		this.observables = observables;
	}

	public final void setStories(CStories stories) {
		this.stories = stories;
	}

	public final void addStories(String name) {
		byte index = 0;
		List<Integer> ruleIDs = new ArrayList<Integer>();
		for (IRule rule : rules) {
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
