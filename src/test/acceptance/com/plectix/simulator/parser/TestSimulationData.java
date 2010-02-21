package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.io.SimulationDataOutputUtil;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.builders.KappaSystemBuilder;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.solution.SuperSubstance;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.staticanalysis.ObservableRuleComponent;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.Stories;
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
		new KappaSystemBuilder(simulationData).build(model);
	}

	private void initSimulationData(boolean isStorify) {
		Simulator mySimulator = new Simulator();
		simulationData = mySimulator.getSimulationData();
		simulationData.getSimulationArguments().setAllowIncompleteSubstance(true);
		if (isStorify) {
			simulationData.getSimulationArguments().setStorifyFlag(true);
			simulationData.getSimulationArguments().setSimulationType(
					SimulationType.STORIFY);
		}
	}

	public String getData() {
		StringBuffer sb = new StringBuffer();
		// rules
		List<Rule> rules = simulationData.getKappaSystem().getRules();
		for (Rule rule : rules) {
			sb.append("'" + rule.getName() + "' "
					+ SimulationDataOutputUtil.getData(rule, true) + "\n");
		}
		sb.append("\n");

		// init
		TreeMap<String, Integer> initMap = new TreeMap<String, Integer>();
		Integer count = 0;
		SolutionInterface solution = simulationData.getKappaSystem()
				.getSolution();
		for (ConnectedComponentInterface cc : solution.getStraightStorage()
				.split()) {
			count = 0;
			String c = Converter.toString(cc);
			if (initMap.containsKey(c))
				count = initMap.get(c);
			initMap.put(c, ++count);
		}
		for (SuperSubstance ss : solution.getSuperStorage().getComponents()) {
			initMap.put(Converter.toString(ss.getComponent()), (int) ss
					.getQuantity());
		}

		for (Map.Entry<String, Integer> entry : initMap.entrySet()) {
			sb.append("%init: " + entry.getValue() + " * (");
			sb.append(entry.getKey() + ")\n");
		}
		sb.append("\n");

		// observables
		Observables obs = simulationData.getKappaSystem().getObservables();
		for (ObservableConnectedComponentInterface obsComponent : obs
				.getConnectedComponentList()) {
			sb.append("%obs: ");
			if (obsComponent.getName() != null)
				sb.append("'" + obsComponent.getName() + "' ");

			for (int i = 0; i < obsComponent.getAgents().size() - 1; i++) {
				sb.append(Converter.toString(obsComponent.getAgents().get(i))
						+ ", ");
			}
			sb.append(Converter.toString(obsComponent.getAgents().get(
					obsComponent.getAgents().size() - 1))
					+ "\n");
		}

		List<ObservableInterface> list = obs.getComponentList();
		ObservableRuleComponent obsRule = null;
		for (ObservableInterface obsComponent : list) {
			if (obsComponent instanceof ObservableRuleComponent) {
				obsRule = (ObservableRuleComponent) obsComponent;
				sb.append("%obs: '" + obsRule.getName() + "'\n");
			}
		}

		if (isStorify) {
			Stories stories = simulationData.getKappaSystem().getStories();
			int i = 0;
			while (stories.getRuleIdAtStories(i) != -1) {
				sb.append("%story: '"
						+ rules.get(stories.getRuleIdAtStories(i++)).getName()
						+ "'\n");
			}
		} else {
			sb.append("\n");

			for (ComplexPerturbation<?, ?> perturbation : simulationData.getKappaSystem()
					.getPerturbations()) {

				sb.append("%mod: " + perturbation);
			}
		}
		return sb.toString();
	}

}
