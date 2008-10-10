package com.plectix.simulator.simulator;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
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
		long clash = 0;
		CRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				model.getSimulationData().getRules(), model.getSimulationData()
						.getSeed());

		model.getSimulationData().getObservables().calculateObs(currentTime);
		while (currentTime <= model.getSimulationData().getTimeLength()) {
			rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				LOGGER.info("end of simulation: there are no active rules");
				model.getSimulationData().setTimeLength(currentTime);
				return;
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Rule: " + rule.getName());

			List<CInjection> injectionsList = ruleProbabilityCalculation
					.getSomeInjectionList(rule);
			System.out.println("Time = " + currentTime);
			currentTime += ruleProbabilityCalculation.getTimeValue();

			if (!isClash(injectionsList)) {
				// negative update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				rule.applyRule(injectionsList);

				doNegativeUpdate(injectionsList);

				model.getSimulationData().getObservables().PrintObsCount();

				// positive update
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("positive update");

				doPositiveUpdate(rule);

				model.getSimulationData().getObservables().PrintObsCount();
			} else {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Clash");
				clash++;
			}
		}

		outputData();
	}

	public final void doPositiveUpdate(CRule rule) {
		for (CRule rules : rule.getActivatedRule()) {
			for (CConnectedComponent cc : rules.getLeftHandSide()) {
				cc.doPositiveUpdate(rule.getRightHandSide());
			}
		}

		for (ObservablesConnectedComponent oCC : model.getSimulationData()
				.getObservables().getConnectedComponentList()) {
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
		LOGGER.info("end of simulation: time");
		model.getSimulationData().setTimeLength(currentTime);
		CXMLWriter xmlWriter = new CXMLWriter();
		try {
			xmlWriter.writeToXML(model.getSimulationData().getXmlSessionName());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
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
