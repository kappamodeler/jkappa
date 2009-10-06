package com.plectix.simulator.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.Action;
import com.plectix.simulator.action.ActionObserverInteface;
import com.plectix.simulator.action.ActionType;
import com.plectix.simulator.action.AddAction;
import com.plectix.simulator.action.DefaultAction;
import com.plectix.simulator.action.DeleteAction;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.component.stories.storage.EventBuilder;
import com.plectix.simulator.component.stories.storage.NullEvent;
import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.probability.WeightedItem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;

/**
 * This class is an implementation of 'rule' entity.
 * 
 * <br>
 * <br>
 * Rule has two handsides with list of connected components each, name and rate.
 * 
 * <br>
 * In general we have kappa file line like <br>
 * <br>
 * <code>'ruleName' leftHandSide -> rightHandSide @ ruleRate</code>, where <br>
 * <li><code>ruleName</code> - name of this rule</li> <li>
 * <code>leftHandside</code> - list of substances (maybe empty)</li> <li>
 * <code>rightHandside</code> - list of substances (maybe empty), which will
 * replace leftHandside after applying this rule</li> <li><code>ruleRate</code>
 * - rate of this rule, i.e. number, which affects on rule application frequency
 * in a whole process</li> <br>
 * <br>
 * For example, we have kappa file line such as : <code>'name' A(x) -> B(), C() @ 0.0</code> <br>
 * This one means rule, which transform agent A with site x to agents B and C.
 * Notice that this rule will never be applied, because it has zero
 * <code>ruleRate</code>
 * 
 * @see ConnectedComponent
 * @author evlasov
 */
@SuppressWarnings("serial")
public class Rule implements Serializable, WeightedItem {
	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(Rule.class);

	private final List<ConnectedComponentInterface> leftHandside;
	private final List<ConnectedComponentInterface> rightHandside;
	private final String ruleName;
	private List<Site> sitesConnectedWithDeleted;
	private List<Site> sitesConnectedWithBroken;
	private boolean doesNothing;

	private int automorphismNumber = 1;
	private boolean hasInfiniteRate = false;
	private List<Rule> activatedRules = new LinkedList<Rule>();
	private List<Rule> inhibitedRule = new LinkedList<Rule>();

	private List<ObservableConnectedComponentInterface> activatedObservable = new LinkedList<ObservableConnectedComponentInterface>();
	private List<ObservableConnectedComponentInterface> inhibitedObservable = new LinkedList<ObservableConnectedComponentInterface>();

	private int ruleId;
	private List<Action> actionList;
	private Map<Agent, Agent> agentAddList;
	private List<Injection> injections;
	private List<Site> changedActivatedSites;
	private List<ChangedSite> changedInhibitedSites;
	private double activity = 0.;
	private double rate;
	
	private static NullEvent nullEvent = new NullEvent();

	private RuleApplicationPoolInterface pool;

	/**
	 * This one is and additional rate, that we should consider when applying a
	 * binary rule
	 */
	private double additionalRate = -1;
	private final boolean isBinary;

	/**
	 * The only CRule constructor.
	 * 
	 * @param leftHandsideComponents
	 *            left handside of the rule, list of connected components
	 * @param rightHandsideComponents
	 *            right handside of the rule, list of connected components
	 * @param ruleName
	 *            name of the rule
	 * @param ruleRate
	 *            rate of the rule
	 * @param ruleId
	 *            unique rule identificator
	 * @param isStorify
	 *            <tt>true</tt> if simulator run in storify mode, <tt>false</tt>
	 *            otherwise
	 */
	public Rule(List<ConnectedComponentInterface> leftHandsideComponents,
			List<ConnectedComponentInterface> rightHandsideComponents, String ruleName,
			double ruleRate, int ruleId, boolean isStorify) {
		if (leftHandsideComponents == null) {
			leftHandside = new ArrayList<ConnectedComponentInterface>();
			leftHandside.add(ThreadLocalData.getEmptyConnectedComponent());
		} else {
			this.leftHandside = leftHandsideComponents;
		}
		this.rightHandside = rightHandsideComponents;
		this.rate = ruleRate;
		associateWithComponents(leftHandsideComponents);
		associateWithComponents(rightHandsideComponents);
		for (ConnectedComponentInterface cc : this.leftHandside) {
			cc.initSpanningTreeMap();
		}
		if (ruleRate == Double.POSITIVE_INFINITY) {
			this.hasInfiniteRate = true;
			this.rate = 1;
		} else {
			this.rate = ruleRate;
		}

		this.ruleName = ruleName;
		this.ruleId = ruleId;
		calculateAutomorphismsNumber();
		markRightHandSideAgents();
		createActionList();
		sortActionList();
		if (isStorify) {
			doesNothing = true;
			for (Action action : actionList) {
				if (action.getType() != ActionType.NONE) {
					doesNothing = false;
					break;
				}
			}
		}

		isBinary = (leftHandside.size() == 2)
				&& (leftHandside.get(0).getAgents().size() == 1)
				&& (leftHandside.get(1).getAgents().size() == 1)
				&& onlyOneBoundAction();
	}

	private final boolean onlyOneBoundAction() {
		int counter = 0;
		for (Action action : actionList) {
			if (action.getType() == ActionType.NONE) {
				continue;
			}
			if (action.getType() == ActionType.BOUND) {
				counter++;
			} else {
				return false;
			}
			if (counter > 2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method links every connected component in given list with this rule
	 * 
	 * @param connectedComponents
	 *            give list of connected component
	 */
	private final void associateWithComponents(
			List<ConnectedComponentInterface> connectedComponents) {
		if (connectedComponents == null)
			return;
		for (ConnectedComponentInterface cc : connectedComponents)
			cc.setRule(this);
	}

	/**
	 * 
	 * @return <tt>true</tt> if left handside of this rule contains no
	 *         substances, otherwise <tt>false</tt>
	 */
	public final boolean leftHandSideIsEmpty() {
		return leftHandside.contains(ThreadLocalData.getEmptyConnectedComponent());
	}

	/**
	 * 
	 * @return <tt>true</tt> if left handside of this rule contains the same
	 *         substances as right handside, otherwise <tt>false</tt>
	 */
	public final boolean doesNothing() {
		return doesNothing;
	}

	/**
	 * Indicates if rate of this rule is infinite
	 * 
	 * @return <tt>true</tt> if rate of this rule is infinite, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean hasInfiniteRate() {
		return hasInfiniteRate;
	}

	/**
	 * Sets rate of this rule to infinity
	 * @param infiniteRate
	 */
	public final void setInfinityRateFlag(boolean infiniteRate) {
		this.hasInfiniteRate = infiniteRate;
	}

	/**
	 * This method is used by simulator in "storify" mode to apply current rule
	 * 
	 * @param injections
	 *            list of injections, which point to substances this rule will
	 *            be applied to
	 * @param netNotation
	 *            INetworkNotation object which keep information about rule
	 *            application
	 * @param simulationData
	 *            simulation data
	 * @param lastApplication
	 *            is <tt>true</tt> if and only if this application is the latest
	 *            in current simulation, otherwise false
	 * @throws StoryStorageException 
	 */
	public void applyRuleForStories(List<Injection> injections,
			EventBuilder eventBuilder, SimulationData simulationData, boolean lastApplication) throws StoryStorageException {
		apply(injections, eventBuilder, simulationData,
				lastApplication);
	}

	/**
	 * This method is used by simulator in "simulation" mode to apply current
	 * rule
	 * 
	 * @param injectionList
	 *            list of injections, which point to substances this rule will
	 *            be applied to
	 * @param simulationData
	 *            simulation data
	 * @throws StoryStorageException 
	 */
	public void applyRule(List<Injection> injectionList,
			SimulationData simulationData) throws StoryStorageException {
		apply(injectionList, nullEvent, simulationData, false);
	}

	/**
	 * This method searches agent in solution, which was added with the latest
	 * application of this rule
	 * 
	 * @param agent
	 *            agent from the right handside of the rule, which was "parent"
	 *            of the unknown agent
	 * @return agent in solution, which was added with the latest application of
	 *         this rule
	 */
	public final Agent getAgentAdd(Agent agent) {
		return agentAddList.get(agent);
	}


	/**
	 * This method puts agent in solution, which was added with the latest
	 * application of this rule, with it's "parent" - agent from the right
	 * handside of the rule
	 * 
	 * @param agent
	 *            agent from the right handside of the rule
	 * @param agentFromSolution
	 *            agent from solution
	 */
	public final void registerAddedAgent(Agent agent, Agent agentFromSolution) {
		agentAddList.put(agent, agentFromSolution);
	}

	/**
	 * The main method for the rule application
	 * 
	 * @param injections
	 *            list of injections, which point to substances this rule will
	 *            be applied to
	 * @param netNotation
	 *            INetworkNotation object which keep information about rule
	 *            application
	 * @param simulationData
	 *            simulation data
	 * @param lastApplication
	 *            is <tt>true</tt> if and only if this application is the latest
	 *            in current simulation, otherwise false
	 * @throws StoryStorageException 
	 */
	protected final void apply(List<Injection> injections,
			ActionObserverInteface event, SimulationData simulationData, boolean lastApplication) throws StoryStorageException {
		agentAddList = new LinkedHashMap<Agent, Agent>();
		sitesConnectedWithDeleted = new ArrayList<Site>();
		sitesConnectedWithBroken = new ArrayList<Site>();
		this.injections = injections;

		if (rightHandside != null) {
			for (ConnectedComponentInterface cc : rightHandside) {
				cc.clearAgentsFromSolutionForRHS();
			}
		}
		event.setTypeById(simulationData.getStoriesAgentTypesStorage());
		for (Action action : actionList) {
			if (action.getLeftCComponent() == null) {
				action.doAction(pool, null,event,
						simulationData);
			} else {
				action.doAction(pool, injections.get(leftHandside
						.indexOf(action.getLeftCComponent())), 
						event, simulationData);
			}
		}

	}

	public final RuleApplicationPoolInterface getPool() {
		return pool;
	}

	public final void preparePool(SimulationData simulationData) {
		SolutionInterface solution = simulationData.getKappaSystem().getSolution();
		pool = solution.prepareRuleApplicationPool();
	}

	/**
	 * This method sets idInRuleSide parameters to the agents from the rule's
	 * right handside and needed for initialization
	 */
	private final void markRightHandSideAgents() {
		List<Agent> rhsAgents = new ArrayList<Agent>();
		List<Agent> lhsAgents = new ArrayList<Agent>();

		if (leftHandside.get(0).isEmpty()) {
			int indexAgentRHS = 0;
			for (ConnectedComponentInterface cc : rightHandside)
				for (Agent agent : cc.getAgents())
					if (agent.getIdInRuleHandside() == Agent.UNMARKED)
						agent.setIdInRuleSide(indexAgentRHS++);
			return;
		} else {
			for (ConnectedComponentInterface cc : leftHandside) {
				lhsAgents.addAll(cc.getAgents());
			}
		}

		if (rightHandside == null)
			return;
		for (ConnectedComponentInterface cc : rightHandside) {
			rhsAgents.addAll(cc.getAgents());
		}
		sortAgentsByIdInRuleHandside(rhsAgents);
		sortAgentsByIdInRuleHandside(lhsAgents);

		int index = 0;
		for (Agent lhsAgent : lhsAgents) {
			if ((index < rhsAgents.size())
					&& !(rhsAgents.get(index).equalz(lhsAgent) && rhsAgents
							.get(index).siteMapsAreEqual(lhsAgent))) {
				break;
			}
			// filling of fixed agents
//			if (index < rhsAgents.size() && isStorify)
//				fillFixedSites(lhsAgent, rhsAgents.get(index));
			index++;
		}

		for (int i = index; i < rhsAgents.size(); i++) {
			if (index == 0)
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i + 1);
			else
				rhsAgents.get(i).setIdInRuleSide(lhsAgents.size() + i);
		}
	}


	/**
	 * This method sorts agents by id in rule's handside
	 * 
	 * @param agents
	 *            list of agents to be sorted
	 */
	private final void sortAgentsByIdInRuleHandside(List<Agent> agents) {
		Agent left;
		Agent right;
		for (int i = 0; i < agents.size() - 1; i++) {
			for (int j = i + 1; j < agents.size(); j++) {
				left = agents.get(i);
				right = agents.get(j);
				if (left.getIdInRuleHandside() > right.getIdInRuleHandside()) {
					agents.set(i, right);
					agents.set(j, left);
				}
			}
		}
	}

	/**
	 * This method sorts action of this rule by priority
	 */
	private final void sortActionList() {
		for (int i = 0; i < actionList.size(); i++) {
			for (int j = 0; j < actionList.size(); j++) {
				if (actionList.get(i).getType().compareTo(actionList.get(j).getType()) < 0) {
					Action actionMin = actionList.get(j);
					Action actionR = actionList.get(i);
					actionList.set(j, actionR);
					actionList.set(i, actionMin);
				}
			}
		}
	}

	/**
	 * Util method using when creating actions list
	 * 
	 * @param sourceSite
	 * @param internalState
	 * @param linkState
	 */
	public final void addInhibitedChangedSite(Site sourceSite,
			boolean internalState, boolean linkState) {
		for (ChangedSite inSite : changedInhibitedSites)
			if (inSite.getSite() == sourceSite) {
				if (!inSite.hasInternalState())
					inSite.setInternalState(internalState);
				if (!inSite.hasLinkState())
					inSite.setLinkState(linkState);
				return;
			}
		changedInhibitedSites.add(new ChangedSite(sourceSite, internalState,
				linkState));
	}


	/**
	 * This methods creates atomic-actions list for this rule
	 */
	private final void createActionList() {
		changedActivatedSites = new ArrayList<Site>();
		changedInhibitedSites = new ArrayList<ChangedSite>();
		actionList = new ArrayList<Action>();

		if (rightHandside != null)
			for (ConnectedComponentInterface cc : rightHandside)
				for (Agent agentRight : cc.getAgents())
					for (Site site : agentRight.getSites()) {
						changedActivatedSites.add(site);
					}

		if (rightHandside == null) {
			for (ConnectedComponentInterface ccL : leftHandside)
				for (Agent lAgent : ccL.getAgents()) {
					actionList.add(new DeleteAction(this, lAgent, ccL));
					for (Site site : lAgent.getSites()) {
						changedInhibitedSites.add(new ChangedSite(site, true,
								true));
					}
				}
			return;
		}

		int lhsAgentsQuantity = 0;
		if (!this.leftHandSideIsEmpty()) {
			for (ConnectedComponentInterface cc : leftHandside) {
				lhsAgentsQuantity += cc.getAgents().size();
			}
		}

		for (ConnectedComponentInterface ccR : rightHandside) {
			for (Agent rAgent : ccR.getAgents()) {
				if ((lhsAgentsQuantity == 0)
						|| (rAgent.getIdInRuleHandside() > lhsAgentsQuantity)) {
					actionList.add(new AddAction(this, rAgent, ccR));
					// fillChangedSites(rAgent);
				}
			}
		}

		if (leftHandside.get(0).isEmpty())
			return;
		for (ConnectedComponentInterface ccL : leftHandside)
			for (Agent lAgent : ccL.getAgents()) {
				this.addAllActions(lAgent, ccL);
			}
	}

	/**
	 * This method adds all actions among to a given agent
	 * 
	 * @param lAgent
	 *            given agent
	 * @param ccL
	 *            connected component which contains lAgent
	 */
	private final void addAllActions(Agent lAgent,
			ConnectedComponentInterface ccL) {
		for (ConnectedComponentInterface ccR : rightHandside) {
			for (Agent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleHandside() == rAgent
						.getIdInRuleHandside()) {
					Action newAction = new DefaultAction(this, lAgent,
							rAgent, ccL, ccR);
					actionList.add(newAction);
					actionList.addAll(newAction.createAtomicActions());
					return;
				}
			}
		}
		actionList.add(new DeleteAction(this, lAgent, ccL));
	}

	/**
	 * Calculates automorphism number for this rule
	 */
	private final void calculateAutomorphismsNumber() {
		if (leftHandside != null)
			if (this.leftHandside.size() == 2) {
				if (this.leftHandside.get(0).getAgents().size() == this.leftHandside
						.get(1).getAgents().size())
					if (this.leftHandside.get(0).isAutomorphicTo(
							this.leftHandside.get(1).getAgents().get(0)))
						automorphismNumber = 2;
			}
	}

	/**
	 * This method calculates activity of this rule according to it's current
	 * parameters
	 */
	public final void calculateActivity() {
		activity = 1.;
		for (ConnectedComponentInterface cc : this.leftHandside) {
			activity *= cc.getInjectionsWeight();
		}
		if (!this.isUnusualBinary()) {
			activity *= rate;
		} else {
			double k1 = rate;
			// additional rate
			double k2 = additionalRate;
			
			long commonInjectionsWeight = 0;
			for (ConnectedComponentInterface cc : this.leftHandside) {
				commonInjectionsWeight += cc.getInjectionsWeight();
			}
			double temp = k1 / commonInjectionsWeight;
			double k2prime = Math.max(temp, k2);
			activity *= k2prime;
		}
		activity /= automorphismNumber;
	}

	/**
	 * This method returns the name of this rule
	 * 
	 * @return the name of this rule
	 */
	public final String getName() {
		return ruleName;
	}

	/**
	 * This method returns activity of this rule
	 * 
	 * @return activity of this rule
	 */
	public final double getActivity() {
		return activity;
	}

	/**
	 * This method sets activity of this rule to a given value
	 * 
	 * @param activity
	 *            new value
	 */
	public final void setActivity(double activity) {
		this.activity = activity;
	}

	/**
	 * This method returns list of components from the left handside of this
	 * rule
	 * 
	 * @return list of components from the left handside of this rule
	 */
	public final List<ConnectedComponentInterface> getLeftHandSide() {
		if (leftHandside == null) {
			return null;
		} else {
			return leftHandside;
		}
	}

	/**
	 * This method returns list of components from the right handside of this
	 * rule
	 * 
	 * @return list of components from the right handside of this rule
	 */
	public final List<ConnectedComponentInterface> getRightHandSide() {
		if (rightHandside == null) {
			return null;
		} else {
			return rightHandside;
		}
	}

	/**
	 * This method returns injection from the injection-list from the latest
	 * application of this rule, which points to connected component, including
	 * siteTo
	 * 
	 * @param siteTo
	 *            given site
	 * @return injection from the injection-list from the latest application of
	 *         this rule, which points to connected component, including siteTo
	 */
	public final Injection getInjectionBySiteToFromLHS(Site siteTo) {
		int sideId = siteTo.getParentAgent().getIdInRuleHandside();
		int i = 0;
		for (ConnectedComponentInterface cc : leftHandside) {
			for (Agent agent : cc.getAgents())
				if (agent.getIdInRuleHandside() == sideId)
					return injections.get(i);
			i++;
		}
		return null;
	}

	public final List<Site> getSitesConnectedWithBroken() {
		return sitesConnectedWithBroken;
	}

	/**
	 * This method adds given site util list.
	 * 
	 * @param site
	 *            given site
	 */
	public final void addSiteConnectedWithBroken(Site site) {
		sitesConnectedWithBroken.add(site);
	}

	public final List<Site> getSitesConnectedWithDeleted() {
		return sitesConnectedWithDeleted;
	}

	public final void addSiteConnectedWithDeleted(Site site) {
		sitesConnectedWithDeleted.add(site);
	}

	public final Site getSiteConnectedWithDeleted(int index) {
		return sitesConnectedWithDeleted.get(index);
	}

	public final void removeSiteConnectedWithDeleted(int index) {
		sitesConnectedWithDeleted.remove(index);
	}

	public final void addAction(Action action) {
		actionList.add(action);
	}

	/**
	 * We use this method to compare rules
	 * 
	 * @param obj
	 *            another rule
	 * @return <tt>true</tt> if rules have similar id's, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean equalz(Rule obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		Rule rule = (Rule) obj;
		return rule.toString().equals(this.toString());
	}

	/**
	 * Returns id number of current rule
	 * 
	 * @return rule id
	 */
	public final int getRuleId() {
		return ruleId;
	}

	/**
	 * Sets id of current rule to a given value
	 * 
	 * @param id
	 *            new value of rule id
	 */
	public final void setRuleID(int id) {
		this.ruleId = id;
	}

	/**
	 * Returns current rule rate
	 * 
	 * @return rule rate
	 */
	public final double getRate() {
		return rate;
	}

	/**
	 * Sets current rule rate to a given value
	 * 
	 * @param ruleRate
	 *            new value of rule rate
	 */
	public final void setRuleRate(double ruleRate) {
		if (ruleRate >= 0) {
			this.rate = ruleRate;
		} else {
			LOGGER.info("warning : rate of the rule '" + ruleName
					+ "' was attempted to be set as negative");
			this.rate = 0;
		}
	}

	/**
	 * Returns list of observable components which activate by this rule
	 * 
	 * @return list of observable components which activate by this rule
	 */
	public final List<ObservableConnectedComponentInterface> getActivatedObservable() {
		return activatedObservable;
	}

	/**
	 * Returns list of rules which activate by this rule
	 * 
	 * @return list of rules which activate by this rule
	 */
	public final List<Rule> getActivatedRules() {
		return activatedRules;
	}
	
	public final void addActivatedRule(Rule rule){
		activatedRules.add(rule);
	}
	
	public final void addinhibitedRule(Rule rule){
		inhibitedRule.add(rule);
	}
	
	public final void addActivatedObs(ObservableConnectedComponentInterface obs){
		activatedObservable.add(obs);
	}
	public final void addinhibitedObs(ObservableConnectedComponentInterface obs){
		inhibitedObservable.add(obs);
	}
	
	/**
	 * Returns list of actions, which this rule performs
	 * 
	 * @return list of actions, which this rule performs
	 */
	public final List<Action> getActionList() {
		return actionList;
	}

	@Override
	public final double getWeight() {
		return activity;
	}

	public final void setAdditionalRate(double binaryRate) {
		this.additionalRate = binaryRate;
	}

	public final double getAdditionalRate() {
		return this.additionalRate;
	}

	/**
	 * This method tells us when we should perform Bologna method on application
	 * of this rule
	 * 
	 * @return <tt>true</tt> if and only if this rule is binary and we should
	 *         consider it's additional rate when applying
	 */
	public final boolean isUnusualBinary() {
		return this.isBinary && additionalRate != -1;
	}

	@Override
	public final String toString() {
		StringBuffer st = new StringBuffer(leftHandside.toString());
		st.append("->");
		st.append(rightHandside);
		return st.toString();
	}
}