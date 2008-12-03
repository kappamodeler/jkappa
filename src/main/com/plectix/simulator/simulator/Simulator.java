package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CNetworkNotation;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.TimerSimulation;

public class Simulator {
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private Model model;

	private double currentTime = 0.;

	private boolean storyMode = false;

	public boolean isStoryMode() {
		return storyMode;
	}

	private boolean isIteration = false;
	int timeStepCounter = 0;

	public Simulator(Model model) {
		this.model = model;
		model.initialize();
	}

	public void run(Integer iteration_num) {
		model.getSimulationData().addInfo(
				new Info(Info.TYPE_INFO, "-Simulation..."));
		TimerSimulation timer = new TimerSimulation(true);
		long clash = 0;
		IRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				model.getSimulationData().getRules(), model.getSimulationData()
						.getSeed());

		boolean isEndRules = false;

		boolean hasSnapshot = false;
		if (model.getSimulationData().getSnapshotTime() >= 0.0)
			hasSnapshot = true;

		long count = 0;

		long max_clash = 0;
		while (!model.getSimulationData().isEndSimulation(currentTime, count,
				iteration_num)
				&& max_clash <= model.getSimulationData().getMaxClashes()) {
			if (hasSnapshot
					&& model.getSimulationData().getSnapshotTime() <= currentTime) {
				hasSnapshot = false;
				model.getSimulationData().setSnapshot(
						new CSnapshot((CSolution) model.getSimulationData()
								.getSolution()));
				model.getSimulationData().setSnapshotTime(currentTime);
			}
			checkPerturbation();
			rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				isEndRules = true;
				model.getSimulationData().setTimeLength(currentTime);
				System.out.println("#");
				break;
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rule: " + rule.getName());

			List<IInjection> injectionsList = ruleProbabilityCalculation
					.getSomeInjectionList(rule);
			if (!rule.isInfinityRate())
				currentTime += ruleProbabilityCalculation.getTimeValue();

			if (!rule.isClash(injectionsList)) {
				// negative update
				max_clash = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				count++;
				rule.applyRule(injectionsList);

				doNegativeUpdate(injectionsList);
				// positive update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("positive update");

				doPositiveUpdate(rule, injectionsList);

				model.getSimulationData().getObservables().calculateObs(
						currentTime, count, model.getSimulationData().isTime());
			} else {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Clash");
				clash++;
				max_clash++;
			}

			if (isIteration)
				addIteration(iteration_num);
		}
		model.getSimulationData().getObservables()
				.calculateObsLast(currentTime);
		outToLogger(isEndRules, timer);
		if (!isIteration)
			outputData(count);
	}

	public final void doNegativeUpdate(List<IInjection> injectionsList) {
		for (IInjection injection : injectionsList) {
			if (injection != CInjection.EMPTY_INJECTION) {
				for (ISite site : injection.getChangedSites()) {
					site.getAgentLink().getEmptySite()
							.removeInjectionsFromCCToSite(injection);
					site.getAgentLink().getEmptySite().clearLiftList();
					site.removeInjectionsFromCCToSite(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (ISite site : injection.getSiteList()) {
						if (!injection
								.checkSiteExistanceAmongChangedSites(site)) {
							site.removeInjectionFromLift(injection);
						}
					}
					injection.getConnectedComponent()
							.removeInjection(injection);
				}
			}
		}
	}

	public final List<IAgent> doNegativeUpdateForDeletedAgents(IRule rule,
			List<IInjection> injectionsList) {
		List<IAgent> freeAgents = new ArrayList<IAgent>();
		for (IInjection injection : injectionsList) {
			for (ISite checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					IAgent checkedAgent = checkedSite.getAgentLink();
					addToAgentList(freeAgents, checkedAgent);
					for (ILiftElement lift : checkedAgent.getEmptySite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getEmptySite().clearLiftList();
					for (ILiftElement lift : checkedSite.getLift()) {

						for (ISite site : lift.getInjection().getSiteList()) {
							if (site != checkedSite)
								site.removeInjectionFromLift(lift
										.getInjection());
						}

						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLiftList();
				}
			}
		}
		for (ISite checkedSite : rule.getSitesConnectedWithBroken()) {
			IAgent checkedAgent = checkedSite.getAgentLink();
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	private final void addToAgentList(List<IAgent> list, IAgent agent) {
		for (IAgent agentL : list)
			if (agentL == agent)
				return;
		list.add(agent);
	}

	public final void doPositiveUpdateForDeletedAgents(List<IAgent> agentsList) {
		for (IAgent agent : agentsList) {
			for (IRule rule : model.getSimulationData().getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
			}
			for (IObservablesConnectedComponent obsCC : SimulationMain
					.getSimulationManager().getSimulationData()
					.getObservables().getConnectedComponentList()) {
				IInjection inj = obsCC.getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	private final void positiveUpdate(List<IRule> rulesList,
			List<IObservablesConnectedComponent> list, IRule rule) {
		for (IRule rules : rulesList) {
			for (IConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}
		for (IObservablesConnectedComponent oCC : list) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	public final void doPositiveUpdate(IRule rule,
			List<IInjection> myCurrentInjectionsList) {
		if (model.getSimulationData().isActivationMap()) {
			positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			positiveUpdate(model.getSimulationData().getRules(), model
					.getSimulationData().getObservables()
					.getConnectedComponentList(), rule);
		}

		doPositiveUpdateForDeletedAgents(doNegativeUpdateForDeletedAgents(rule,
				myCurrentInjectionsList));

	}

	private final void checkPerturbation() {
		if (model.getSimulationData().getPerturbations().size() != 0) {
			for (CPerturbation pb : model.getSimulationData()
					.getPerturbations()) {
				switch (pb.getType()) {
				case CPerturbation.TYPE_TIME: {
					if (!pb.isDo())
						pb.checkCondition(currentTime);
					break;
				}
				case CPerturbation.TYPE_NUMBER: {
					pb.checkCondition(model.getSimulationData()
							.getObservables());
					break;
				}
				case CPerturbation.TYPE_ONCE: {
					if (!pb.isDo())
						pb.checkConditionOnce(currentTime);
					break;
				}
				}

			}

		}
	}

	private final void outToLogger(boolean isEndRules, TimerSimulation timer) {
		model.getSimulationData().stopTimer(timer, "-Simulation:");
		// System.out.println("-Simulation: " + timer.getTimer() + " sec. CPU");
		if (!isEndRules)
			LOGGER.info("end of simulation: time");
		else
			LOGGER.info("end of simulation: there are no active rules");
	}

	public final void outputData(long count) {
		TimerSimulation timerOutput = new TimerSimulation();
		timerOutput.startTimer();

		model.getSimulationData().setTimeLength(currentTime);
		model.getSimulationData().setEvent(count);
		try {
			model.getSimulationData().writeToXML(timerOutput);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		// System.out.println("-Results outputted in xml session: "
		// + SimulationMain.getSimulationManager().getTimer()
		// + " sec. CPU");
	}

	public final void runStories() {
		CStories stories = model.getSimulationData().getStories();
		int count = 0;
		for (int i = 0; i < CStories.numberOfSimulations; i++) {
			model.getSimulationData().addInfo(
					new Info(Info.TYPE_INFO, "-Simulation..."));
			SimulationMain.getSimulationManager().startTimer();
			TimerSimulation timer = new TimerSimulation(true);
			boolean isEndRules = false;
			long clash = 0;
			IRule rule;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
					model.getSimulationData().getRules(), model
							.getSimulationData().getSeed());
			long max_clash = 0;
			model.getSimulationData().resetBar();
			while (!model.getSimulationData().isEndSimulation(currentTime,
					count, null)
					&& max_clash <= model.getSimulationData().getMaxClashes()) {
				checkPerturbation();
				rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					model.getSimulationData().setTimeLength(currentTime);
					System.out.println("#");
					break;
				}

				List<IInjection> injectionsList = ruleProbabilityCalculation
						.getSomeInjectionList(rule);
				currentTime += ruleProbabilityCalculation.getTimeValue();
				if (!rule.isClash(injectionsList)) {
					CNetworkNotation netNotation = new CNetworkNotation(count,
							rule,injectionsList,model.getSimulationData().getSolution());
					// <<<<<<< .mine
					// if (rule.getRuleID()==1)
					// System.out.println();
					// =======
					max_clash = 0;
					// >>>>>>> .r5077
					if (stories.checkRule(rule.getRuleID(), i)) {
						rule.applyLastRuleForStories(injectionsList,
								netNotation);
						rule.applyRuleForStories(injectionsList, netNotation);
						stories.addToNetworkNotationStory(i, netNotation);
						count++;
						isEndRules = true;
						System.out.println("#");
						break;
					}
					rule.applyRuleForStories(injectionsList, netNotation);
					stories.addToNetworkNotationStory(i, netNotation);
					count++;

					doNegativeUpdate(injectionsList);
					doPositiveUpdate(rule, injectionsList);
				} else {
					clash++;
					max_clash++;
				}
			}
			count = 0;
			outToLogger(isEndRules, timer);
			stories.handling(i);
			if (i < CStories.numberOfSimulations - 1)
				resetSimulation();
		}
		stories.merge();
		outputData(count);
	}

	public final void runIterations() {
		isIteration = true;
		int seed = model.getSimulationData().getSeed();
		model.getSimulationData().initIterations();
		for (int iteration_num = 0; iteration_num < model.getSimulationData()
				.getIterations(); iteration_num++) {
			// Initialize the Random Number Generator with seed = initialSeed +
			// i
			// We also need a new command line argument to feed the initialSeed.
			// See --seed argument of simplx.
			model.getSimulationData().setSeed(seed + iteration_num);

			// run the simulator
			timeStepCounter = 0;
			// while (true) {
			// run the simulation until the next time to dump the results
			run(iteration_num);
			// }

			// if the simulator's initial state is cached, reload it for next
			// run
			if (iteration_num < model.getSimulationData().getIterations() - 1)
				resetSimulation();

		}

		// we are done. report the results
		model.getSimulationData().createTMPReport();

	}

	public final void resetSimulation() {
		model.getSimulationData().addInfo(
				new Info(Info.TYPE_INFO, "-Reset simulation data."));
		model.getSimulationData().addInfo(
				new Info(Info.TYPE_INFO, "-Initialization..."));
		SimulationMain.getSimulationManager().startTimer();
		model.getSimulationData().clearRules();
		model.getSimulationData().getObservables().resetLists();
		model.getSimulationData().getSolution().clearAgents();
		model.getSimulationData().getSolution().clearSolutionLines();
		if (model.getSimulationData().getPerturbations() != null)
			model.getSimulationData().clearPerturbations();

		currentTime = 0;

		SimulationMain.getInstance().readSimulatonFile();
		SimulationMain.getInstance().initialize();
		Model modelNew = new Model(SimulationMain.getSimulationManager()
				.getSimulationData());
		this.model = modelNew;
		model.initialize();
	}

	private final void addIteration(Integer iteration_num) {

		List<ArrayList<RunningMetric>> runningMetrics = model
				.getSimulationData().getRunningMetrics();
		int number_of_observables = model.getSimulationData().getObservables()
				.getComponentListForXMLOutput().size();
		// .getComponentList().size();

		if (iteration_num == 0) {
			model.getSimulationData().getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			double x = // x is the value for the observable_num at the current
			// time
			model.getSimulationData().getObservables()
					.getComponentListForXMLOutput().get(observable_num)
					.getSize(model.getSimulationData().getObservables());
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

}
