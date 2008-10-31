package com.plectix.simulator.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.CXMLWriter;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
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

		model.getSimulationData().getObservables().calculateObs(currentTime);
		boolean isEndRules = false;

		boolean hasSnapshot = false;
		if (model.getSimulationData().getSnapshotTime() >= 0.0)
			hasSnapshot = true;

		while (currentTime <= model.getSimulationData().getTimeLength()) {
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
			currentTime += ruleProbabilityCalculation.getTimeValue();
			if (!isClash(injectionsList)) {
				// negative update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				rule.applyRule(injectionsList);

				doNegativeUpdate(injectionsList);
				// positive update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("positive update");

				doPositiveUpdate(rule);

				doPositiveUpdateForDeletedAgents(doNegativeUpdateForDeletedAgents(
						rule, injectionsList));
				model.getSimulationData().getObservables().calculateObs(currentTime);
			} else {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Clash");
				clash++;
			}

			if (isIteration)
				addIteration(iteration_num);

		}
		outToLogger(isEndRules);
		if (!isIteration)
			outputData();
	}

	public final void doNegativeUpdate(List<CInjection> injectionsList) {
		for (CInjection injection : injectionsList) {
			if (injection != CConnectedComponent.EMPTY_INJECTION) {
				for (CSite site : injection.getChangedSites()) {
					site.getAgentLink().EMPTY_SITE
							.removeInjectionsFromCCToSite(injection);
					site.removeInjectionsFromCCToSite(injection);
					site.getLift().clear();
				}
				if (injection.getChangedSites().size() != 0) {
					for (CSite site : injection.getSiteList()) {
						if (!injection
								.checkSiteExistanceAmongChangedSites(site)) {
							site.removeInjectionFromLift(injection);
						}
					}
					injection.getConnectedComponent().getInjectionsList()
							.remove(injection);
				}
			}
		}
	}

	public final List<CAgent> doNegativeUpdateForDeletedAgents(CRule rule,
			List<CInjection> injectionsList) {
		List<CAgent> freeAgents = new ArrayList<CAgent>();
		for (CInjection injection : injectionsList) {
			for (CSite checkedSite : rule.getAgentsConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					CAgent checkedAgent = checkedSite.getAgentLink();
					addToAgentList(freeAgents, checkedAgent);
					for (CLiftElement lift : checkedAgent.EMPTY_SITE.getLift()) {
						lift.getConnectedComponent().getInjectionsList().remove(lift.getInjection());
					}
					checkedAgent.EMPTY_SITE.clearLiftList();
					for (CLiftElement lift : checkedSite.getLift()) {
						lift.getConnectedComponent().getInjectionsList().remove(lift.getInjection());
					}
					checkedSite.clearLiftList();
				}
			}
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
					if (!agent.isAgentHaveLinkToConnectedComponent(cc)) {
						cc.setInjections(agent);
					}
				}
			}
			for (ObservablesConnectedComponent obsCC : SimulationMain
					.getSimulationManager().getSimulationData()
					.getObservables().getConnectedComponentList()) {
				if (!agent.isAgentHaveLinkToConnectedComponent(obsCC)) {
					obsCC.setInjections(agent);
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
			if (((ObservablesConnectedComponent) oCC)
					.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}
	}

	public final void doPositiveUpdate(CRule rule) {
		if (model.getSimulationData().isActivationMap()) {
			positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			positiveUpdate(model.getSimulationData().getRules(), model
					.getSimulationData().getObservables()
					.getConnectedComponentList(), rule);
		}

		
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

	public final void outputData() {
		SimulationMain.getSimulationManager().startTimer();

		model.getSimulationData().setTimeLength(currentTime);
		CXMLWriter xmlWriter = new CXMLWriter();
		try {
			xmlWriter.writeToXML(model.getSimulationData().getXmlSessionName());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println("-Results outputted in xml session: "
				+ SimulationMain.getSimulationManager().getTimer()
				+ " sec. CPU");
	}

	private boolean isClash(List<CInjection> injections) {
		if (injections.size() == 2) {
			for (CSite siteCC1 : injections.get(0).getSiteList())
				for (CSite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
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
				if (!isClash(injectionsList)) {
					if (stories.checkRule(rule.getRuleID(), i))
						break;
					rule.applyRule(injectionsList);
					doNegativeUpdate(injectionsList);
					doPositiveUpdate(rule);
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
		createTMPReport();

	}

	private final void createTMPReport() {
		// model.getSimulationData().updateData();
		SimulationMain.getSimulationManager().startTimer();

		int number_of_observables = model.getSimulationData().getObservables()
				.getConnectedComponentList().size();
		List<Double> timeStamps = model.getSimulationData().getTimeStamps();
		List<ArrayList<RunningMetric>> runningMetrics = model
				.getSimulationData().getRunningMetrics();

		try {
			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				int oCamlObservableNo = number_of_observables - observable_num
						- 1; // everything is backward with OCaml!
				BufferedWriter writer = new BufferedWriter(new FileWriter(model
						.getSimulationData().getTmpSessionName()
						+ "-" + oCamlObservableNo));

				// writer.write("Observable " + observable_num + "\r\n");
				// writer.flush();
				for (int timeStepCounter = 0; timeStepCounter < timeStamps
						.size(); timeStepCounter++) {
					String st = timeStamps.get(timeStepCounter)
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMin()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMax()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMean()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getStd();

					writer.write(st);
					writer.newLine();
					// writer.flush();
				}

				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("-Results outputted in tmp session: "
				+ SimulationMain.getSimulationManager().getTimer()
				+ " sec. CPU");

	}

	public final void resetSimulation() {
		model.getSimulationData().getRules().clear();
		model.getSimulationData().getObservables().getConnectedComponentList()
				.clear();
		model.getSimulationData().getObservables().getCountTimeList().clear();
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
				.getConnectedComponentList().size();

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
					.getConnectedComponentList().get(observable_num)
					.getInjectionsList().size();
			if (timeStepCounter >= runningMetrics.get(observable_num).size()) {
				runningMetrics.get(observable_num).add(new RunningMetric());
			}
			runningMetrics.get(observable_num).get(timeStepCounter).add(x);
		}

		timeStepCounter++;
	}

}
