package com.plectix.simulator.parser.newtests;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.ObservablesRuleComponent;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.solution.SuperSubstance;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.builders.KappaSystemBuilder;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.util.Converter;

public class TestSimulationData {

	private KappaModel model;
	private boolean isStorify;
	private SimulationData simulationData;

	public TestSimulationData(KappaModel model, boolean isStorify) {
		this.model = model;
		this.isStorify = isStorify;
	}

	public void build() throws ParseErrorException, DocumentFormatException,
			FileNotFoundException {
		initSimulationData(isStorify);
		new KappaSystemBuilder(simulationData).build();
	}

	private void initSimulationData(boolean isStorify) {
		Simulator mySimulator = new Simulator();
		simulationData = mySimulator.getSimulationData();
		simulationData.setInitialModel(model);
		if (isStorify) {
			simulationData.getSimulationArguments().setStorify(true);
			simulationData.getSimulationArguments().setSimulationType(
					SimulationType.STORIFY);
		}
	}

	public String getData() {
		StringBuffer sb = new StringBuffer();
		// rules
		List<CRule> rules = simulationData.getKappaSystem().getRules();
		for (CRule rule : rules) {
			sb.append("'" + rule.getName() + "' " + SimulationData.getData(rule, true) + "\n");
		}
		sb.append("\n");

		// init
		TreeMap<String, Integer> initMap = new TreeMap<String, Integer>();
		Integer count = 0;
		ISolution solution = simulationData.getKappaSystem().getSolution();
		for (IConnectedComponent cc : solution.getStraightStorage().split()) {
			count = 0;
			String c = Converter.toString(cc);
			if (initMap.containsKey(c))
				count = initMap.get(c);
			initMap.put(c, ++count);
		}
		for (SuperSubstance ss : solution.getSuperStorage().getComponents()) {
			initMap.put(Converter.toString(ss.getComponent()), (int)ss.getQuantity());
		}

		for (Map.Entry<String, Integer> entry : initMap.entrySet()) {
			sb.append("%init: " + entry.getValue() + " * (");
			sb.append(entry.getKey() + ")\n");
		}
		sb.append("\n");

		// observables
		CObservables obs = simulationData.getKappaSystem().getObservables();
		for (IObservablesConnectedComponent obsComponent : obs
				.getConnectedComponentList()) {
			sb.append("%obs: ");
			if (obsComponent.getName() != null)
				sb.append("'" + obsComponent.getName() + "' ");

			for (int i = 0; i < obsComponent.getAgents().size() - 1; i++) {
				sb.append(Converter.toString(obsComponent.getAgents().get(i)) + ", ");
			}
			sb.append(Converter.toString(obsComponent.getAgents().get(
					obsComponent.getAgents().size() - 1))
					+ "\n");
		}

		List<IObservablesComponent> list = obs.getComponentList();
		ObservablesRuleComponent obsRule = null;
		for (IObservablesComponent obsComponent : list) {
			if (obsComponent instanceof ObservablesRuleComponent) {
				obsRule = (ObservablesRuleComponent) obsComponent;
				sb.append("%obs: '" + obsRule.getName() + "'\n");
			}
		}

		if (isStorify) {
			CStories stories = simulationData.getKappaSystem().getStories();
			int i = 0;
			while (stories.getRuleIdAtStories(i) != -1) {
				sb.append("%story: '"
						+ rules.get(stories.getRuleIdAtStories(i++)).getName()
						+ "'\n");
			}
		} else {
			sb.append("\n");

			for (CPerturbation perturbation : simulationData.getKappaSystem()
					.getPerturbations()) {

				PerturbationReader pr = new PerturbationReader(perturbation);
				sb.append(pr.read());
			}
		}
		return sb.toString();
	}

}
