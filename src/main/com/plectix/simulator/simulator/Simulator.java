package com.plectix.simulator.simulator;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CXMLWriter;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;

public class Simulator {
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private Model model;

	private double currentTime = 0.;

	private int randomSeed;

	public Simulator(Model model) {
		this.model = model;
		model.initialize();
	}

	public void run() {
		SimulationMain.getSimulationManager().startTimer();
		long clash = 0;
		CRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				model.getSimulationData().getRules(), model.getSimulationData()
						.getSeed());

		model.getSimulationData().getObservables().calculateObs(currentTime);
		boolean isEndRules = false;
		while (currentTime <= model.getSimulationData().getTimeLength()) {
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

			} else {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Clash");
				clash++;
			}
		}

		outToLogger(isEndRules);
		outputData();
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

	public final void doPositiveUpdate(CRule rule) {

		for (CRule rules : model.getSimulationData().getRules()) {
			for (CConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}

		for (ObservablesConnectedComponent oCC : model.getSimulationData()
				.getObservables().getConnectedComponentList()) {
			if (((ObservablesConnectedComponent) oCC)
					.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				oCC.doPositiveUpdate(rule.getRightHandSide());
		}

		model.getSimulationData().getObservables().calculateObs(currentTime);
	}

	public final void doNegativeUpdate(List<CInjection> injectionsList) {
		for (CInjection injection : injectionsList) {
			if (injection != CConnectedComponent.EMPTY_INJECTION) {
				for (CSite site : injection.getSiteList()) {
					site.removeInjectionsFromCCToSite(injection);
					site.getLift().clear();
				}

				injection.getConnectedComponent().getInjectionsList().remove(
						injection);
			}
		}
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
}
