package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.io.xml.RuleCompressionXMLWriter;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.ConditionType;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.InjectionsUtil;
import com.plectix.simulator.simulationclasses.perturbations.AbstractModification;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.ConditionInterface;
import com.plectix.simulator.simulationclasses.probability.SkipListSelector;
import com.plectix.simulator.simulationclasses.probability.WeightedItemSelector;
import com.plectix.simulator.simulator.api.steps.ContactMapComputationOperation;
import com.plectix.simulator.simulator.api.steps.DeadRuleDetectionOperation;
import com.plectix.simulator.simulator.api.steps.InfluenceMapComputationOperation;
import com.plectix.simulator.simulator.api.steps.InjectionBuildingOperation;
import com.plectix.simulator.simulator.api.steps.LocalViewsComputationOperation;
import com.plectix.simulator.simulator.api.steps.RuleCompressionOperation;
import com.plectix.simulator.simulator.api.steps.SpeciesEnumerationOperation;
import com.plectix.simulator.simulator.api.steps.SubviewsComputationOperation;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapMode;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.future.InfluenceMapWithFuture;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.rulecompression.CompressionResults;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressor;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;
import com.plectix.simulator.util.IdGenerator;
import com.plectix.simulator.util.Info.InfoType;

public final class KappaSystem implements KappaSystemInterface {
	private WeightedItemSelector<Rule> rules = new SkipListSelector<Rule>();
	private List<Rule> orderedRulesList = new ArrayList<Rule>();
	private Stories stories = null;
	private List<ComplexPerturbation<?, ?>> perturbations = null;
	private Observables observables = new Observables();
	private SolutionInterface solution;// = new CSolution(); // soup of initial
	// components
	private final ContactMap contactMap = new ContactMap();
	private AllSubViewsOfAllAgentsInterface subViews;
	private InfluenceMap influenceMap = new InfluenceMapWithFuture();
	private LocalViewsMain localViews;
	private SpeciesEnumeration enumerationOfSpecies;
	private RuleCompressionXMLWriter ruleCompressionWriter;

	private final IdGenerator agentsIdGenerator = new IdGenerator();
	private final IdGenerator ruleIdGenerator = new IdGenerator();
	private final SimulationData simulationData;

	public KappaSystem(SimulationData data) {
		simulationData = data;
	}

	public SimulationData getSimulationData() {
		return simulationData;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#initialize(com.plectix
	 * .simulator.util.Info.InfoType)
	 */
	public final void initialize(InfoType outputType) throws Exception {
		SimulationArguments args = simulationData.getSimulationArguments();

		observables.init(args.getTimeLimit(), args.getInitialTime(), args
				.getMaxNumberOfEvents(), args.getPoints(), args.isTime());
		observables.checkAutomorphisms();

		// !!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			new InjectionBuildingOperation(this).perform();
		}

		if (solution.getSuperStorage() != null) {
			solution.getSuperStorage().setAgentsLimit(args.getAgentsLimit());
		}

		if ((args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP)
				|| args.createSubViews()
				|| args.isDeadRulesShow()
				|| args.isActivationMap()
				|| args.isInhibitionMap()
				|| args.runQualitativeCompression()
				|| args.runQuantitativeCompression()) {
			// contactMap.initAbstractSolution();
			// contactMap.constructAbstractRules(rules);
			// contactMap.constructAbstractContactMap();

			if ((args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP && contactMap
					.getMode() == ContactMapMode.MODEL)
					|| args.createSubViews()
					|| args.isDeadRulesShow()
					|| args.isActivationMap()
					|| args.isInhibitionMap()
					|| args.runQualitativeCompression()
					|| args.runQuantitativeCompression()) {

				
				subViews = new SubviewsComputationOperation(this).perform();
			}
			if (args.isDeadRulesShow())
				new DeadRuleDetectionOperation(this).perform();
			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
				new ContactMapComputationOperation(simulationData).perform();
			}

			if (args.isActivationMap() || args.isInhibitionMap()) {
				influenceMap = new InfluenceMapComputationOperation(simulationData).perform();
			}

			if (args.createLocalViews() || args.useEnumerationOfSpecies()) {
				localViews = new LocalViewsComputationOperation(simulationData).perform();
				if (args.useEnumerationOfSpecies()) {
					enumerationOfSpecies = new SpeciesEnumerationOperation(this).perform();
				}
			}
		}

		if (args.runQualitativeCompression()) {
			new RuleCompressionOperation(this, RuleCompressionType.QUALITATIVE).perform();
		}

		if (args.runQuantitativeCompression()) {
			new RuleCompressionOperation(this, RuleCompressionType.QUANTITATIVE).perform();
		}
	}

	public final List<Rule> compressRules(RuleCompressionType type,
			Collection<Rule> rules) {
		RuleCompressor compressor = new RuleCompressor(type, this
				.getLocalViews());
		CompressionResults results = compressor.compress(rules);
		MainSubViews newSubViews = new MainSubViews();

		newSubViews.build(solution, results.getCompressedRules());
		newSubViews.initDeadRules();
		
		//TODO separate output stuff
		ruleCompressionWriter = new RuleCompressionXMLWriter(this, results,
				newSubViews);
		return results.getCompressedRules();
	}

	// ---------------------POSITIVE UPDATE-----------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#doPositiveUpdate
	 * (com.plectix.simulator.component.Rule, java.util.List)
	 */
	public final void doPositiveUpdate(Rule rule,
			List<Injection> currentInjectionsList) {
		if (simulationData.getSimulationArguments().isActivationMap()) {
			rule.positiveUpdate(rule.getActivatedRules(), rule
					.getActivatedObservable());
		} else {
			rule.positiveUpdate(getRules(), observables
					.getConnectedComponentList());
		}

		List<Agent> freeAgents = UpdatesPerformer
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgents(freeAgents);
	}

	private final void doPositiveUpdateForDeletedAgents(List<Agent> agents) {
		for (Agent agent : agents) {
			for (Rule rule : orderedRulesList) {
				for (ConnectedComponentInterface cc : rule.getLeftHandSide()) {
					Injection inj = cc.createInjection(agent);
					if (inj != null) {
						if (!agent.hasSimilarInjection(inj))
							cc.setInjection(inj);
					}
				}
			}
			for (ObservableConnectedComponentInterface obsCC : observables
					.getConnectedComponentList()) {
				Injection inj = obsCC.createInjection(agent);
				if (inj != null) {
					if (!agent.hasSimilarInjection(inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	// ------------------MISC--------------------------------

	public final List<Injection> chooseInjectionsForRuleApplication(Rule rule) {
		List<Injection> list = new ArrayList<Injection>();
		rule.preparePool(simulationData);
		for (ConnectedComponentInterface cc : rule.getLeftHandSide()) {
			Injection inj = cc.getRandomInjection();
			list.add(inj);
			solution.addInjectionToPool(rule.getPool(), inj);
		}
		if (!InjectionsUtil.isClash(list)) {
			return list;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#setRules(java.util
	 * .List)
	 */
	public final void setRules(List<Rule> rules) {
		orderedRulesList = rules;
		this.rules.updatedItems(rules);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#checkPerturbation
	 * (double)
	 */
	public final void checkPerturbation(double currentTime) {
		if (perturbations.size() != 0) {
			for (ComplexPerturbation<?, ?> pb : perturbations) {
				AbstractModification modification = pb.getModification();
				ConditionInterface condition = pb.getCondition();
				if (condition.getType() != ConditionType.SPECIES) {
					if (modification.wasPerformed()) {
						continue;
					}
				}
				if (condition.check(currentTime)) {
					modification.perform();
				}
			}
		}
	}

	// ----------------------GETTERS-------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getRules()
	 */
	public final List<Rule> getRules() {
		return orderedRulesList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#getRuleById(int)
	 */
	public final Rule getRuleById(int ruleId) {
		// TODO: We are scanning a list linearly, can't we use a LinkedHashMap
		// here?
		for (Rule rule : orderedRulesList) {
			if (rule.getRuleId() == ruleId) {
				return rule;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getSolution()
	 */
	public final SolutionInterface getSolution() {
		return solution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#getObservables()
	 */
	public final Observables getObservables() {
		return observables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getStories()
	 */
	public final Stories getStories() {
		return stories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getContactMap()
	 */
	public final ContactMap getContactMap() {
		return contactMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getSubViews()
	 */
	public final AllSubViewsOfAllAgentsInterface getSubViews() {
		return subViews;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#generateNextRuleId()
	 */
	public final long generateNextRuleId() {
		return ruleIdGenerator.generateNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#generateNextAgentId
	 * ()
	 */
	public final long generateNextAgentId() {
		return agentsIdGenerator.generateNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#getPerturbations()
	 */
	public final List<ComplexPerturbation<?, ?>> getPerturbations() {
		return perturbations;
	}

	// ----------------------SETTERS / ADDERS-------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#addRule(com.plectix
	 * .simulator.component.Rule)
	 */
	public final void addRule(Rule rule) {
		orderedRulesList.add(rule);
		rules.updatedItem(rule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#setSolution(com.
	 * plectix.simulator.interfaces.SolutionInterface)
	 */
	public void setSolution(SolutionInterface solution) {
		this.solution = solution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#setObservables(com
	 * .plectix.simulator.component.Observables)
	 */
	public void setObservables(Observables observables) {
		this.observables = observables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#setStories(com.plectix
	 * .simulator.component.stories.Stories)
	 */
	public final void setStories(Stories stories) {
		this.stories = stories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#addStories(java.
	 * lang.String)
	 */
	public final void addStories(String name) {
		byte index = 0;
		List<Integer> ruleIDs = new ArrayList<Integer>();
		for (Rule rule : orderedRulesList) {
			if ((rule.getName() != null)
					&& (rule.getName().startsWith(name) && ((name.length() == rule
							.getName().length()) || ((rule.getName()
							.startsWith(name + "_op")) && ((name.length() + 3) == rule
							.getName().length()))))) {
				ruleIDs.add(rule.getRuleId());
				index++;
			}
			if (index == 2) {
				this.stories.addToStories(ruleIDs);
				return;
			}
		}
		this.stories.addToStories(ruleIDs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#setPerturbations
	 * (java.util.List)
	 */
	public final void setPerturbations(
			List<ComplexPerturbation<?, ?>> perturbations) {
		this.perturbations = perturbations;
	}

	// --------------------CLEANUP---------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#resetIdGenerators()
	 */
	public final void resetIdGenerators() {
		agentsIdGenerator.reset();
		ruleIdGenerator.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#clearRules()
	 */
	public final void clearRules() {
		rules = new SkipListSelector<Rule>();
		orderedRulesList.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#clearPerturbations()
	 */
	public final void clearPerturbations() {
		if (perturbations != null) {
			perturbations.clear();
		}
	}

	// ---------------------MISC------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getRandomRule()
	 */
	public final Rule getRandomRule() {
		List<Rule> infiniteRules = getInfiniteRatedRules();
		if (!infiniteRules.isEmpty() && !this.anyRuleCannotBeApplied(infiniteRules)) {
			return infiniteRules.get(ThreadLocalData.getRandom().getInteger(
					infiniteRules.size()));
		}
		return rules.select();
	}

	/**
	 * We check whether any rule with infinite rate cannot be applied because of clashes
	 * @return
	 */
	private final boolean anyRuleCannotBeApplied(List<Rule> rules) {
		for (Rule rule : rules) {
			if (!ruleIsBlockedWithClashes(rule)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * We assume that left hand side of the rule can contain only 2 connected components
	 * @param rule
	 * @return
	 */
	private final boolean ruleIsBlockedWithClashes(Rule rule) {
		List<Injection> pair = new ArrayList<Injection>();
		if (rule.getLeftHandSide().size() < 2) {
			return false;
		}
		for (Injection injection0 : rule.getLeftHandSide().get(0).getInjectionsList()) {
			pair.add(injection0);
			for (Injection injection1 : rule.getLeftHandSide().get(1).getInjectionsList()) {
				pair.add(injection1);
				if (!InjectionsUtil.isClash(pair)) {
					return false;
				}
				pair.remove(injection1);
			}
		}
		return true;
	}
	
	private List<Rule> getInfiniteRatedRules() {
		List<Rule> infinitRules = new ArrayList<Rule>();
		for (Rule rule : orderedRulesList) {
			if (rule.hasInfiniteRate() && rule.getActivity() > 0) {
				infinitRules.add(rule);
				rule.setActivity(0.);
				rules.updatedItem(rule);
			}
		}
		return infinitRules;
	}

	public void updateRuleActivities() {
		for (Rule rule : orderedRulesList) {
			double oldActivity = rule.getActivity();
			rule.calculateActivity();
			if (rule.getActivity() != oldActivity) {
				rules.updatedItem(rule);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getTimeValue()
	 */
	public final double getTimeValue() {
		double randomValue = 0;

		while (randomValue == 0.0)
			randomValue = ThreadLocalData.getRandom().getDouble();

		return -1. / rules.getTotalWeight() * java.lang.Math.log(randomValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#getInfluenceMap()
	 */
	public final InfluenceMap getInfluenceMap() {
		return influenceMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getLocalViews()
	 */
	public final LocalViewsMain getLocalViews() {
		if (localViews == null) {
			MainSubViews sViews = new MainSubViews();
			sViews.build(getSolution(), getRules());
			localViews = new LocalViewsMain(sViews);
			localViews.buildLocalViews();
		}
		return localViews;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#getEnumerationOfSpecies
	 * ()
	 */
	public final SpeciesEnumeration getEnumerationOfSpecies() {
		return enumerationOfSpecies;
	}

	public final RuleCompressionXMLWriter getRuleCompressionBuilder() {
		return ruleCompressionWriter;
	}
}
