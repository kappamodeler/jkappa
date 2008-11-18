package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CLiftElement;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.util.RunningMetric;

public class Simulator {
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private Model model;

	private double currentTime = 0.;

	private int randomSeed;

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
		SimulationMain.getSimulationManager().startTimer();
		long clash = 0;
		CRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				model.getSimulationData().getRules(), model.getSimulationData()
						.getSeed());

		boolean isEndRules = false;

		boolean hasSnapshot = false;
		if (model.getSimulationData().getSnapshotTime() >= 0.0)
			hasSnapshot = true;

		long count = 0;

		long max_clash = 0;
		while (!model.getSimulationData().isEndSimulation(currentTime, count)
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
				break;
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rule: " + rule.getName());

			List<CInjection> injectionsList = ruleProbabilityCalculation
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
						currentTime, model.getSimulationData().isTime());
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
		outToLogger(isEndRules);
		if (!isIteration)
			outputData(count);
	}

	public final void doNegativeUpdate(List<CInjection> injectionsList) {
		for (CInjection injection : injectionsList) {
			if (injection != CConnectedComponent.EMPTY_INJECTION) {
				for (CSite site : injection.getChangedSites()) {
					site.getAgentLink().EMPTY_SITE
							.removeInjectionsFromCCToSite(injection);
					site.getAgentLink().EMPTY_SITE.clearLiftList();
					site.removeInjectionsFromCCToSite(injection);
					site.clearLiftList();
				}
				if (injection.getChangedSites().size() != 0) {
					for (CSite site : injection.getSiteList()) {
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

	public final List<CAgent> doNegativeUpdateForDeletedAgents(CRule rule,
			List<CInjection> injectionsList) {
		List<CAgent> freeAgents = new ArrayList<CAgent>();
		for (CInjection injection : injectionsList) {
			for (CSite checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					CAgent checkedAgent = checkedSite.getAgentLink();
					addToAgentList(freeAgents, checkedAgent);
					for (CLiftElement lift : checkedAgent.EMPTY_SITE.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.EMPTY_SITE.clearLiftList();
					for (CLiftElement lift : checkedSite.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLiftList();
				}
			}
		}
		for (CSite checkedSite : rule.getSitesConnectedWithBroken()) {
			CAgent checkedAgent = checkedSite.getAgentLink();
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}

	private final void addToAgentList(List<CAgent> list, CAgent agent) {
		for (CAgent agentL : list)
			if (agentL == agent)
				return;
		list.add(agent);
	}

	public final void doPositiveUpdateForDeletedAgents(List<CAgent> agentsList) {
		for (CAgent agent : agentsList) {
			for (CRule rule : model.getSimulationData().getRules()) {
				for (CConnectedComponent cc : rule.getLeftHandSide()) {
					CInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
			}
			for (ObservablesConnectedComponent obsCC : SimulationMain
					.getSimulationManager().getSimulationData()
					.getObservables().getConnectedComponentList()) {
				CInjection inj = obsCC.getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	private final void positiveUpdate(List<CRule> rulesList,
			List<ObservablesConnectedComponent> obs, CRule rule) {
		for (CRule rules : rulesList) {
			for (CConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}
		for (ObservablesConnectedComponent oCC : obs) {
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	public final void doPositiveUpdate(CRule rule,
			List<CInjection> injectionsList) {
		if (model.getSimulationData().isActivationMap()) {
			positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			positiveUpdate(model.getSimulationData().getRules(), model
					.getSimulationData().getObservables()
					.getConnectedComponentList(), rule);
		}

		doPositiveUpdateForDeletedAgents(doNegativeUpdateForDeletedAgents(rule,
				injectionsList));

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
				}

			}

		}
	}

	private final void outToLogger(boolean isEndRules) {
		System.out.println("-Simulation: "
				+ SimulationMain.getSimulationManager().getTimer()
				+ " sec. CPU");
		if (!isEndRules)
			LOGGER.info("end of simulation: time");
		else
			LOGGER.info("end of simulation: there are no active rules");
	}

	public final void outputData(long count) {
		SimulationMain.getSimulationManager().startTimer();

		model.getSimulationData().setTimeLength(currentTime);
		model.getSimulationData().setEvent(count);
		try {
			model.getSimulationData().writeToXML();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println("-Results outputted in xml session: "
				+ SimulationMain.getSimulationManager().getTimer()
				+ " sec. CPU");
	}

	public final void runStories() {
		CStories stories = model.getSimulationData().getStories();
		for (int i = 0; i < CStories.numberOfSimulations; i++) {
			SimulationMain.getSimulationManager().startTimer();
			long clash = 0;
			CRule rule;
			CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
					model.getSimulationData().getRules(), model
							.getSimulationData().getSeed());
			while (currentTime <= model.getSimulationData().getTimeLength()) {
				checkPerturbation();
				rule = ruleProbabilityCalculation.getRandomRule();

				if (rule == null) {
					model.getSimulationData().setTimeLength(currentTime);
					break;
				}

				List<CInjection> injectionsList = ruleProbabilityCalculation
						.getSomeInjectionList(rule);
				currentTime += ruleProbabilityCalculation.getTimeValue();
				if (!rule.isClash(injectionsList)) {
					if (stories.checkRule(rule.getRuleID(), i))
						break;
					rule.applyRule(injectionsList);
					doNegativeUpdate(injectionsList);
					doPositiveUpdate(rule, injectionsList);
				} else {
					clash++;
				}
			}
			resetSimulation();
		}
		stories.handling();
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
			resetSimulation();

		}

		// we are done. report the results
		model.getSimulationData().createTMPReport();

	}

	public final void resetSimulation() {
		model.getSimulationData().getRules().clear();
		model.getSimulationData().getObservables().getConnectedComponentList()
				.clear();
		model.getSimulationData().getObservables().getCountTimeList().clear();
		model.getSimulationData().getObservables().getComponentList().clear();
		((CSolution) model.getSimulationData().getSolution()).getAgentMap()
				.clear();
		((CSolution) model.getSimulationData().getSolution())
				.getSolutionLines().clear();
		if (model.getSimulationData().getPerturbations() != null)
			model.getSimulationData().getPerturbations().clear();

		currentTime = 0;

		SimulationMain.getSimulationManager().startTimer();
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
				.getComponentList().size();

		if (iteration_num == 0) {
			model.getSimulationData().getTimeStamps().add(currentTime);

			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
		}

		for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
			double x = // x is the value for the observable_num at the current
			// time
			model.getSimulationData().getObservables().getComponentList().get(
					observable_num).getSize();
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

}
