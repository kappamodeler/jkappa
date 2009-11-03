package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.InjectionsUtil;
import com.plectix.simulator.simulationclasses.perturbations.Perturbation;
import com.plectix.simulator.simulationclasses.probability.SkipListSelector;
import com.plectix.simulator.simulationclasses.probability.WeightedItemSelector;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapMode;
import com.plectix.simulator.staticanalysis.cycledetection.Detector;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.future.InfluenceMapWithFuture;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.rulecompression.CompressionResults;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionXMLWriter;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressor;
import com.plectix.simulator.staticanalysis.speciesenumeration.GeneratorSpecies;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;
import com.plectix.simulator.util.IdGenerator;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public final class KappaSystem implements KappaSystemInterface {
	private WeightedItemSelector<Rule> rules = new SkipListSelector<Rule>();
	private List<Rule> orderedRulesList = new ArrayList<Rule>();
	private Stories stories = null;
	private List<Perturbation> perturbations = null;
	private Observables observables = new Observables();
	private SolutionInterface solution;// = new CSolution(); // soup of initial
										// components
	private ContactMap contactMap = new ContactMap();
	private AllSubViewsOfAllAgentsInterface subViews;
	private InfluenceMap influenceMap;
	private LocalViewsMain localViews;
	private GeneratorSpecies enumerationOfSpecies;
	private RuleCompressionXMLWriter ruleCompressionWriter;

	private final IdGenerator agentsIdGenerator = new IdGenerator();
	private final IdGenerator ruleIdGenerator = new IdGenerator();
	private final SimulationData simulationData;

	public KappaSystem(SimulationData data) {
		simulationData = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.plectix.simulator.simulator.KappaSystemInterface#initialize(com.plectix
	 * .simulator.util.Info.InfoType)
	 */
	public final void initialize(InfoType outputType) {
		SimulationArguments args = simulationData.getSimulationArguments();

		observables.init(args.getTimeLength(), args.getInitialTime(), args
				.getMaxNumberOfEvents(), args.getPoints(), args.isTime());
		List<Rule> rules = getRules();

		observables.checkAutomorphisms();

		// !!!!!!!!INJECTIONS!!!!!!!!!
		if (args.isSolutionRead()) {
			(new InjectionsBuilder(this)).build();
		}

		if (solution.getSuperStorage() != null) {
			solution.getSuperStorage().setAgentsLimit(args.getAgentsLimit());
		}

		if ((args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP)
				|| args.createSubViews()
				|| args.isDeadRulesShow()
				|| args.isActivationMap() || args.isInhibitionMap()) {
			// contactMap.initAbstractSolution();
			// contactMap.constructAbstractRules(rules);
			// contactMap.constructAbstractContactMap();

			if ((args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP && contactMap
					.getMode() == ContactMapMode.MODEL)
					|| args.createSubViews()
					|| args.isDeadRulesShow()
					|| args.isActivationMap() || args.isInhibitionMap()) {

				subViews = new MainSubViews();
				subViews.build(solution, rules);
			}
			if (args.isDeadRulesShow())
				subViews.initDeadRules();
			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
				contactMap.fillingContactMap(rules, subViews, simulationData
						.getKappaSystem());
			}

			if (args.isActivationMap() || args.isInhibitionMap()) {
				PlxTimer timer = new PlxTimer();
				simulationData.addInfo(outputType, InfoType.INFO,
						"--Abstracting influence map...");
				influenceMap = new InfluenceMapWithFuture();
				if (!contactMap.isInitialized())
					contactMap.fillingContactMap(rules, subViews,
							simulationData.getKappaSystem());
				influenceMap.initInfluenceMap(subViews.getRules(), observables,
						contactMap, subViews.getAgentNameToAgent());
				influenceMap.fillActivatedInhibitedRules(rules, this,
						observables);
				simulationData.stopTimer(outputType, timer, "--Abstraction:");
				simulationData.addInfo(outputType, InfoType.INFO,
						"--influence map computed");
			}

			if (args.createLocalViews() || args.useEnumerationOfSpecies()) {
				localViews = new LocalViewsMain(subViews);
				localViews.buildLocalViews();
				if (args.useEnumerationOfSpecies()) {
					enumerationOfSpecies = new GeneratorSpecies(localViews
							.getLocalViews());
					List<AbstractAgent> list = new LinkedList<AbstractAgent>();
					list.addAll(contactMap.getAbstractSolution()
							.getAgentNameToAgent().values());
					Detector detector = new Detector(subViews, list);
					if (detector.extractCycles().isEmpty())
						enumerationOfSpecies.enumerate();
					else
						enumerationOfSpecies.unbound();
				}
			}
		}

		if (args.runQualitativeCompression()) {
			compressRules(RuleCompressionType.QUALITATIVE, rules);
		}

		if (args.runQuantitativeCompression()) {
			compressRules(RuleCompressionType.QUANTITATIVE, rules);
		}
	}

	private final void compressRules(RuleCompressionType type,
			Collection<Rule> rules) {
		RuleCompressor compressor = new RuleCompressor(type, this
				.getLocalViews());
		CompressionResults results = compressor.compress(rules);
		MainSubViews newSubViews = new MainSubViews();

		newSubViews.build(solution, results.getCompressedRules());
		newSubViews.initDeadRules();
		ruleCompressionWriter = new RuleCompressionXMLWriter(this, results,
				newSubViews);
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
			rule.positiveUpdate(getRules(), observables.getConnectedComponentList());
		}

		List<Agent> freeAgents = UpdatesPerformer.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
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
			for (Perturbation pb : perturbations) {
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
	public final List<Perturbation> getPerturbations() {
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
	public final void setPerturbations(List<Perturbation> perturbations) {
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
		perturbations.clear();
	}

	// ---------------------MISC------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.plectix.simulator.simulator.KappaSystemInterface#getRandomRule()
	 */
	public final Rule getRandomRule() {
		List<Rule> infinitRules = new ArrayList<Rule>();
		for (Rule rule : orderedRulesList) {
			double oldActivity = rule.getActivity();
			rule.calculateActivity();
			if (rule.hasInfiniteRate() && rule.getActivity() > 0) {
				infinitRules.add(rule);
				rule.setActivity(0.);
			}

			if (rule.getActivity() != oldActivity) {
				rules.updatedItem(rule);
			}
		}
		if (!infinitRules.isEmpty())
			return infinitRules.get(ThreadLocalData.getRandom().getInteger(
					infinitRules.size()));
		return rules.select();
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
	public final GeneratorSpecies getEnumerationOfSpecies() {
		return enumerationOfSpecies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.plectix.simulator.simulator.KappaSystemInterface#
	 * getRuleCompressionBuilder()
	 */
	public final RuleCompressionXMLWriter getRuleCompressionBuilder() {
		return ruleCompressionWriter;
	}
}
