package com.plectix.simulator.simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.detectionOfCycles.Detector;
import com.plectix.simulator.components.complex.enumerationOfSpecies.GeneratorSpecies;
import com.plectix.simulator.components.complex.influenceMap.AInfluenceMap;
import com.plectix.simulator.components.complex.influenceMap.withFuture.CInfluenceMapWithFuture;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
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
import com.plectix.simulator.rulecompression.CompressionResults;
import com.plectix.simulator.rulecompression.RuleCompressionType;
import com.plectix.simulator.rulecompression.RuleCompressor;
import com.plectix.simulator.rulecompression.writer.RuleCompressionXMLWriter;
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
	private IAllSubViewsOfAllAgents subViews;
	private AInfluenceMap influenceMap;
	private CLocalViewsMain localViews;
	private GeneratorSpecies enumerationOfSpecies;
	private RuleCompressionXMLWriter ruleCompressionWriter; 

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

		observables.checkAutomorphisms();

		// !!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			(new InjectionsBuilder(this)).build();
		}

		if (solution.getSuperStorage() != null) {
			solution.getSuperStorage().setAgentsLimit(args.getAgentsLimit());
		}

		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP
				|| args.isSubViews() || args.isDeadRules() || args.isActivationMap() 
				|| args.isInhibitionMap()) {
			// contactMap.initAbstractSolution();
			// contactMap.constructAbstractRules(rules);
			// contactMap.constructAbstractContactMap();
			subViews = new CMainSubViews();
			subViews.build(solution, rules);
			if(args.isDeadRules())
				subViews.initDeadRules();
			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
				contactMap.fillingContactMap(rules,subViews,simulationData);
			}
			
			if(args.isActivationMap() || args.isInhibitionMap()){
				PlxTimer timer = new PlxTimer();
				simulationData.addInfo(outputType, InfoType.INFO,
						"--Abstracting influence map...");
				influenceMap = new CInfluenceMapWithFuture();
				if(!contactMap.isInit())
					contactMap.fillingContactMap(rules,subViews,simulationData);
				influenceMap.initInfluenceMap(subViews.getRules(),observables, contactMap, subViews.getAgentNameIdToAgent());
				influenceMap.fillingActivatedInhibitedRules(rules,this, observables);
				simulationData.stopTimer(outputType, timer, "--Abstraction:");
				simulationData.addInfo(outputType, InfoType.INFO,
						"--influence map computed");
			}
			
			if(args.isLocalViews() || args.isEnumerationOfSpecies()){
				localViews = new CLocalViewsMain(subViews);
				localViews.buildLocalViews();
				if(args.isEnumerationOfSpecies()){
					enumerationOfSpecies = new GeneratorSpecies(localViews.getLocalViews());
					List<CAbstractAgent> list = new LinkedList<CAbstractAgent>();
					list.addAll(contactMap.getAbstractSolution().getAgentNameIdToAgent().values());
					Detector detector = new Detector(subViews,list);
					if(detector.extractCycles().isEmpty())
						enumerationOfSpecies.enumerate();
					else
						enumerationOfSpecies.setUnbounded();
				}
			}
		}
		
		if (args.isQualitativeCompression()){
			compressRules(RuleCompressionType.QUALITATIVE, rules);
		}
		
		if (args.isQuantitativeCompression()){
			compressRules(RuleCompressionType.QUANTITATIVE, rules);
		}
	}

	private void compressRules(RuleCompressionType type, Collection<CRule> rules) {
		RuleCompressor compressor = new RuleCompressor(type, this);
		CompressionResults results = compressor.compress(rules);
		System.out.println(results);
		CMainSubViews qualitativeSubViews = new CMainSubViews();
		qualitativeSubViews.build(solution, new ArrayList<CRule>(results.getCompressedRules()));
		qualitativeSubViews.initDeadRules();
		ruleCompressionWriter = new RuleCompressionXMLWriter(this, results, qualitativeSubViews);
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
		// if (rules != null) {
		// return rules.asSet();
		// } else {
		// return null;
		// }
		return Collections.unmodifiableList(orderedRulesList);
	}

	public final CRule getRuleByID(int ruleID) {
		// TODO: We are scanning a list linearly, can't we use a LinkedHashMap here?
		for (CRule rule : orderedRulesList) {
			if (rule.getRuleID() == ruleID) {
				return rule;
			}
		}
		return null;
	}

	// public final CRule getRuleByNumber(int i) {
	// return orderedRulesList.get(i);
	// }

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

	public final IAllSubViewsOfAllAgents getSubViews() {
		return subViews;
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

	// --------------------METHODS FROM PROBABILITY
	// CALCULATION--------------------

	public final CRule getRandomRule() {
		for (CRule rule : orderedRulesList) {
			double oldActivity = rule.getActivity();
			rule.calculateActivity();
			if (rule.getActivity() != oldActivity) {
				rules.updatedItem(rule);
			}
		}
		return rules.select();
	}

	public final double getTimeValue() {
		double randomValue = 0;

		while (randomValue == 0.0)
			randomValue = ThreadLocalData.getRandom().getDouble();

		return -1. / rules.getTotalWeight() * java.lang.Math.log(randomValue);
	}
	
	public AInfluenceMap getInfluenceMap(){
		return influenceMap;
	}
	
	public CLocalViewsMain getLocalViews(){
		return localViews;
	}

	public GeneratorSpecies getEnumerationOfSpecies() {
		return enumerationOfSpecies;
	}

	public RuleCompressionXMLWriter getRuleCompressionBuilder(){
		return ruleCompressionWriter;
	}
}
