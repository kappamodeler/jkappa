package com.plectix.simulator.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.plectix.simulator.action.CAction;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CAddAction;
import com.plectix.simulator.action.CDefaultAction;
import com.plectix.simulator.action.CDeleteAction;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.RuleApplicationPool;
import com.plectix.simulator.components.stories.storage.CEvent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
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
 * @see CConnectedComponent
 * @author evlasov
 */
public class CRule implements Serializable, WeightedItem {
	private static final long serialVersionUID = 6045806917402381525L;

	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(CRule.class);

	private final List<IConnectedComponent> leftHandside;
	private final List<IConnectedComponent> rightHandside;
	private final String ruleName;
	private List<CSite> sitesConnectedWithDeleted;
	private List<CSite> sitesConnectedWithBroken;
	private boolean rHSEqualsLHS;

	private int automorphismNumber = 1;
	private boolean infiniteRate = false;
	private List<CRule> activatedRules = new LinkedList<CRule>();
	private List<CRule> inhibitedRule = new LinkedList<CRule>();

	private List<IObservablesConnectedComponent> activatedObservable = new LinkedList<IObservablesConnectedComponent>();
	private List<IObservablesConnectedComponent> inhibitedObservable = new LinkedList<IObservablesConnectedComponent>();

	private int ruleID;
	private List<CAction> actionList;
	private Map<CAgent, CAgent> agentAddList;
	private List<CInjection> injList;
	private List<CSite> changedActivatedSites;
	private List<ChangedSite> changedInhibitedSites;
	private double activity = 0.;
	private double rate;

	private RuleApplicationPool pool;

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
	 * @param ruleID
	 *            unique rule identificator
	 * @param isStorify
	 *            <tt>true</tt> if simulator run in storify mode, <tt>false</tt>
	 *            otherwise
	 */
	public CRule(List<IConnectedComponent> leftHandsideComponents,
			List<IConnectedComponent> rightHandsideComponents, String ruleName,
			double ruleRate, int ruleID, boolean isStorify) {
		if (leftHandsideComponents == null) {
			leftHandside = new ArrayList<IConnectedComponent>();
			leftHandside.add(ThreadLocalData.getEmptyConnectedComponent());
		} else {
			this.leftHandside = leftHandsideComponents;
		}
		this.rightHandside = rightHandsideComponents;
		this.rate = ruleRate;
		setConnectedComponentLinkRule(leftHandsideComponents);
		setConnectedComponentLinkRule(rightHandsideComponents);
		for (IConnectedComponent cc : this.leftHandside) {
			cc.initSpanningTreeMap();
		}
		if (ruleRate == Double.POSITIVE_INFINITY) {
			this.infiniteRate = true;
			this.rate = 1;
		} else {
			this.rate = ruleRate;
		}

		this.ruleName = ruleName;
		this.ruleID = ruleID;
		calculateAutomorphismsNumber();
		markRHSAgents();
		createActionList();
		sortActionList();
		if (isStorify) {
			rHSEqualsLHS = true;
			for (CAction action : actionList) {
				if (action.getTypeId() != CActionType.NONE.getId()) {
					rHSEqualsLHS = false;
					break;
				}
			}
		}

		isBinary = (leftHandside.size() == 2)
				&& (leftHandside.get(0).getAgents().size() == 1)
				&& (leftHandside.get(1).getAgents().size() == 1)
				&& onlyOneBoundAction();
	}

	private boolean onlyOneBoundAction() {
		int counter = 0;
		for (CAction action : actionList) {
			if (CActionType.getById(action.getTypeId()) == CActionType.NONE) {
				continue;
			}
			if (CActionType.getById(action.getTypeId()) == CActionType.BOUND) {
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
	 * @param cList
	 *            give list of connected component
	 */
	private final void setConnectedComponentLinkRule(
			List<IConnectedComponent> cList) {
		if (cList == null)
			return;
		for (IConnectedComponent cc : cList)
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
	 * @return <tt>true</tt> if right handside of this rule contains no
	 *         substances, otherwise <tt>false</tt>
	 */
	public final boolean rightHandSideIsEmpty() {
		return rightHandside == null;
	}



	/**
	 * 
	 * @return <tt>true</tt> if left handside of this rule contains the same
	 *         substances as right handside, otherwise <tt>false</tt>
	 */
	public boolean isRHSEqualsLHS() {
		return rHSEqualsLHS;
	}

	/**
	 * Indicates if rate of this rule is infinite
	 * 
	 * @return <tt>true</tt> if rate of this rule is infinite, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean isInfiniteRated() {
		return infiniteRate;
	}

	/**
	 * Sets rate of this rule to infinity
	 * 
	 * @param infinityRate
	 */
	public final void setInfinityRate(boolean infinityRate) {
		this.infiniteRate = infinityRate;
	}

	/**
	 * This method is used by simulator in "storify" mode to apply current rule
	 * 
	 * @param injectionList
	 *            list of injections, which point to substances this rule will
	 *            be applied to
	 * @param netNotation
	 *            INetworkNotation object which keep information about rule
	 *            application
	 * @param simulationData
	 *            simulation data
	 * @param isLast
	 *            is <tt>true</tt> if and only if this application is the latest
	 *            in current simulation, otherwise false
	 */
	public void applyRuleForStories(List<CInjection> injectionList,
			CEvent eventContainer,
			SimulationData simulationData, boolean isLast) {
		apply(injectionList, eventContainer, simulationData,
				isLast);
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
	 */
	public void applyRule(List<CInjection> injectionList,
			SimulationData simulationData) {
		apply(injectionList, null, simulationData, false);
	}

	/**
	 * This method searches agent in solution, which was added with the latest
	 * application of this rule
	 * 
	 * @param rhsAgent
	 *            agent from the right handside of the rule, which was "parent"
	 *            of the unknown agent
	 * @return agent in solution, which was added with the latest application of
	 *         this rule
	 */
	public final CAgent getAgentAdd(CAgent rhsAgent) {
		return agentAddList.get(rhsAgent);
	}


	/**
	 * This method puts agent in solution, which was added with the latest
	 * application of this rule, with it's "parent" - agent from the right
	 * handside of the rule
	 * 
	 * @param rhsAgent
	 *            agent from the right handside of the rule
	 * @param agentFromSolution
	 *            agent from solution
	 */
	public final void putAgentAdd(CAgent rhsAgent, CAgent agentFromSolution) {
		agentAddList.put(rhsAgent, agentFromSolution);
	}

	/**
	 * The main method for the rule application
	 * 
	 * @param injectionList
	 *            list of injections, which point to substances this rule will
	 *            be applied to
	 * @param netNotation
	 *            INetworkNotation object which keep information about rule
	 *            application
	 * @param simulationData
	 *            simulation data
	 * @param isLast
	 *            is <tt>true</tt> if and only if this application is the latest
	 *            in current simulation, otherwise false
	 */
	protected final void apply(List<CInjection> injectionList,
			CEvent eventContainer,
			SimulationData simulationData, boolean isLast) {
		agentAddList = new LinkedHashMap<CAgent, CAgent>();
		sitesConnectedWithDeleted = new ArrayList<CSite>();
		sitesConnectedWithBroken = new ArrayList<CSite>();
		this.injList = injectionList;

		if (rightHandside != null) {
			for (IConnectedComponent cc : rightHandside) {
				cc.clearAgentsFromSolutionForRHS();
			}
		}

		for (CAction action : actionList) {
			if (action.getLeftCComponent() == null) {
				action.doAction(pool, null,eventContainer,
						simulationData);
			} else {
				action.doAction(pool, injectionList.get(leftHandside
						.indexOf(action.getLeftCComponent())), 
						eventContainer, simulationData);
			}
		}

	}

	public final RuleApplicationPool getPool() {
		return pool;
	}

	public final void preparePool(SimulationData simulationData) {
		ISolution solution = simulationData.getKappaSystem().getSolution();
		pool = solution.prepareRuleApplicationPool();
	}

	/**
	 * This method sets idInRuleSide parameters to the agents from the rule's
	 * right handside and needed for initialization
	 */
	private final void markRHSAgents() {
		List<CAgent> rhsAgents = new ArrayList<CAgent>();
		List<CAgent> lhsAgents = new ArrayList<CAgent>();

		if (leftHandside.get(0).isEmpty()) {
			int indexAgentRHS = 0;
			for (IConnectedComponent cc : rightHandside)
				for (CAgent agent : cc.getAgents())
					if (agent.getIdInRuleHandside() == CAgent.UNMARKED)
						agent.setIdInRuleSide(indexAgentRHS++);
			return;
		} else {
			for (IConnectedComponent cc : leftHandside) {
				lhsAgents.addAll(cc.getAgents());
			}
		}

		if (rightHandside == null)
			return;
		for (IConnectedComponent cc : rightHandside) {
			rhsAgents.addAll(cc.getAgents());
		}
		sortAgentsByIdInRuleHandside(rhsAgents);
		sortAgentsByIdInRuleHandside(lhsAgents);

		int index = 0;
		for (CAgent lhsAgent : lhsAgents) {
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
	 * @param list
	 *            list of agents to be sorted
	 */
	private final void sortAgentsByIdInRuleHandside(List<CAgent> list) {
		CAgent left;
		CAgent right;
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				left = list.get(i);
				right = list.get(j);
				if (left.getIdInRuleHandside() > right.getIdInRuleHandside()) {
					list.set(i, right);
					list.set(j, left);
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
				if (actionList.get(i).getTypeId() < actionList.get(j)
						.getTypeId()) {
					CAction actionMin = actionList.get(j);
					CAction actionR = actionList.get(i);
					actionList.set(j, actionR);
					actionList.set(i, actionMin);
				}
			}
		}
	}





	/**
	 * Util method using when creating actions list
	 * 
	 * @param fromSite
	 * @param internalState
	 * @param linkState
	 */
	public final void addInhibitedChangedSite(CSite fromSite,
			boolean internalState, boolean linkState) {
		for (ChangedSite inSite : changedInhibitedSites)
			if (inSite.getSite() == fromSite) {
				if (!inSite.isInternalState())
					inSite.setInternalState(internalState);
				if (!inSite.isLinkState())
					inSite.setLinkState(linkState);
				return;
			}
		changedInhibitedSites.add(new ChangedSite(fromSite, internalState,
				linkState));
	}


	/**
	 * This methods creates atomic-actions list for this rule
	 */
	private final void createActionList() {
		changedActivatedSites = new ArrayList<CSite>();
		changedInhibitedSites = new ArrayList<ChangedSite>();
		actionList = new ArrayList<CAction>();

		if (rightHandside != null)
			for (IConnectedComponent cc : rightHandside)
				for (CAgent agentRight : cc.getAgents())
					for (CSite site : agentRight.getSites()) {
						changedActivatedSites.add(site);
					}

		if (rightHandside == null) {
			for (IConnectedComponent ccL : leftHandside)
				for (CAgent lAgent : ccL.getAgents()) {
					actionList.add(new CDeleteAction(this, lAgent, ccL));
					for (CSite site : lAgent.getSites()) {
						changedInhibitedSites.add(new ChangedSite(site, true,
								true));
					}
				}
			return;
		}

		int lhsAgentsQuantity = 0;
		if (!this.leftHandSideIsEmpty()) {
			for (IConnectedComponent cc : leftHandside) {
				lhsAgentsQuantity += cc.getAgents().size();
			}
		}

		for (IConnectedComponent ccR : rightHandside) {
			for (CAgent rAgent : ccR.getAgents()) {
				if ((lhsAgentsQuantity == 0)
						|| (rAgent.getIdInRuleHandside() > lhsAgentsQuantity)) {
					actionList.add(new CAddAction(this, rAgent, ccR));
					// fillChangedSites(rAgent);
				}
			}
		}

		if (leftHandside.get(0).isEmpty())
			return;
		for (IConnectedComponent ccL : leftHandside)
			for (CAgent lAgent : ccL.getAgents()) {
				this.isAgentFromLHSHasFoundInRHS(lAgent, ccL);
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
	private final void isAgentFromLHSHasFoundInRHS(CAgent lAgent,
			IConnectedComponent ccL) {
		for (IConnectedComponent ccR : rightHandside) {
			for (CAgent rAgent : ccR.getAgents()) {
				if (lAgent.getIdInRuleHandside() == rAgent
						.getIdInRuleHandside()) {
					CAction newAction = new CDefaultAction(this, lAgent,
							rAgent, ccL, ccR);
					actionList.add(newAction);
					actionList.addAll(newAction.createAtomicActions());
					return;
				}
			}
		}
		actionList.add(new CDeleteAction(this, lAgent, ccL));
	}

	/**
	 * This methods extracts all agents from a given connected components
	 * 
	 * @param ccList
	 *            list of connected components
	 * @return list of agents, included in ccList
	 */
	public final List<CAgent> getAgentsFromConnectedComponent(
			List<IConnectedComponent> ccList) {
		List<CAgent> agentList = new ArrayList<CAgent>();
		if (ccList.get(0).getAgents().get(0).getIdInRuleHandside() == CAgent.UNMARKED)
			return agentList;
		for (IConnectedComponent cc : ccList) {
			for (CAgent agent : cc.getAgents())
				agentList.add(agent);
		}

		return agentList;
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
	 * This method checks if every connected component in left handside of this
	 * rule has injection to solution
	 * 
	 * @return <tt>true</tt> if every connected component in left handside of
	 *         this rule has injection to solution, otherwise <tt>false</tt>
	 */
	public final boolean canBeApplied() {
		int injCounter = 0;
		for (IConnectedComponent cc : this.getLeftHandSide()) {
			if (!cc.isEmpty() && cc.getInjectionsWeight() != 0) {
				injCounter++;
			} else if (cc.isEmpty()) {
				injCounter++;
			}
		}
		if (injCounter == this.getLeftHandSide().size()) {
			return true;
		}
		return false;
	}

	/**
	 * This method calculates activity of this rule according to it's current
	 * parameters
	 */
	public final void calculateActivity() {
		activity = 1.;
		for (IConnectedComponent cc : this.leftHandside) {
			activity *= cc.getInjectionsWeight();
		}
		if (!this.isUnusualBinary()) {
			activity *= rate;
		} else {
			double k1 = rate;
			// additional rate
			double k2 = additionalRate;
			
			long commonInjectionsWeight = 0;
			for (IConnectedComponent cc : this.leftHandside) {
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
	public final void setActivity(Double activity) {
		this.activity = activity;
	}

	/**
	 * This method returns list of components from the left handside of this
	 * rule
	 * 
	 * @return list of components from the left handside of this rule
	 */
	public final List<IConnectedComponent> getLeftHandSide() {
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
	public final List<IConnectedComponent> getRightHandSide() {
		if (rightHandside == null) {
			return null;
		} else {
			return rightHandside;
		}
	}

	/**
	 * This method indicates if 2 injections are in clash
	 * 
	 * @param injections
	 *            list of injections with power = 2
	 * @return <tt>true</tt> if injections are in clash, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean isClash(List<CInjection> injections) {
		Stack<CInjection> injectionStack = new Stack<CInjection>();
		injectionStack.addAll(injections);
		while (!injectionStack.isEmpty()) {
			CInjection inj1 = injectionStack.pop();
			for (CInjection inj2 : injectionStack) {
				for (CSite siteCC1 : inj1.getSiteList())
					for (CSite siteCC2 : inj2.getSiteList())
						if (siteCC1.getParentAgent().getId() == siteCC2
								.getParentAgent().getId())
							return true;
			}
		}
		return false;
	}

	/**
	 * This method indicates if injections from left handside are in clash in
	 * case this rule has infinite rate
	 * 
	 * @return <tt>true</tt> if injections are in clash, otherwise
	 *         <tt>false</tt>
	 */
	public final boolean isClashForInfiniteRule() {
		if (this.leftHandside.size() == 2) {
			if (this.leftHandside.get(0).getInjectionsWeight() == 1
					&& this.leftHandside.get(1).getInjectionsWeight() == 1) {
				List<CInjection> injList = new ArrayList<CInjection>();
				injList.add(this.leftHandside.get(0).getFirstInjection());
				injList.add(this.leftHandside.get(1).getFirstInjection());
				return isClash(injList);
			}
		}
		return false;
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
	public final CInjection getInjectionBySiteToFromLHS(CSite siteTo) {
		int sideId = siteTo.getParentAgent().getIdInRuleHandside();
		int i = 0;
		for (IConnectedComponent cc : leftHandside) {
			for (CAgent agent : cc.getAgents())
				if (agent.getIdInRuleHandside() == sideId)
					return injList.get(i);
			i++;
		}
		return null;
	}

	public final List<CSite> getSitesConnectedWithBroken() {
		return sitesConnectedWithBroken;
	}

	/**
	 * This method adds given site util list.
	 * 
	 * @param site
	 *            given site
	 */
	public final void addSiteConnectedWithBroken(CSite site) {
		sitesConnectedWithBroken.add(site);
	}

	public final List<CSite> getSitesConnectedWithDeleted() {
		return sitesConnectedWithDeleted;
	}

	public final void addSiteConnectedWithDeleted(CSite site) {
		sitesConnectedWithDeleted.add(site);
	}

	public final CSite getSiteConnectedWithDeleted(int index) {
		return sitesConnectedWithDeleted.get(index);
	}

	public final void removeSiteConnectedWithDeleted(int index) {
		sitesConnectedWithDeleted.remove(index);
	}

	public final void addAction(CAction action) {
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
	public final boolean equalz(CRule obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof CRule)) {
			return false;
		}

		CRule rule = (CRule) obj;

		return rule.getRuleID() == ruleID;
	}

	/**
	 * This method indicates whether this rule included in given collection of
	 * rules
	 * 
	 * @param collection
	 *            collection of rules
	 * @return <tt>true</tt> if this rule included in given collection of rules,
	 *         otherwise <tt>false</tt>
	 */
	public final boolean includedInCollection(Collection<CRule> collection) {
		for (CRule rule : collection) {
			if (this.equalz(rule)) {
				return true;
			}
		}
		return false;
	}

	// ------------------------GETTERS AND
	// SETTERS------------------------------------

	/**
	 * Returns id number of current rule
	 * 
	 * @return rule id
	 */
	public final int getRuleID() {
		return ruleID;
	}

	/**
	 * Sets id of current rule to a given value
	 * 
	 * @param id
	 *            new value of rule id
	 */
	public final void setRuleID(int id) {
		this.ruleID = id;
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
	public final List<IObservablesConnectedComponent> getActivatedObservable() {
		return activatedObservable;
	}

	/**
	 * Returns list of rules which activate by this rule
	 * 
	 * @return list of rules which activate by this rule
	 */
	public final List<CRule> getActivatedRules() {
		return activatedRules;
	}
	
	public final void addActivatedRule(CRule rule){
		activatedRules.add(rule);
	}
	
	public final void addinhibitedRule(CRule rule){
		inhibitedRule.add(rule);
	}
	
	public final void addActivatedObs(IObservablesConnectedComponent obs){
		activatedObservable.add(obs);
	}
	public final void addinhibitedObs(IObservablesConnectedComponent obs){
		inhibitedObservable.add(obs);
	}
	
	/**
	 * Returns list of actions, which this rule performs
	 * 
	 * @return list of actions, which this rule performs
	 */
	public final List<CAction> getActionList() {
		return actionList;
	}

	public double getWeight() {
		return activity;
	}

	public void setAdditionalRate(double binaryRate) {
		// System.out.println(binaryRate);
		this.additionalRate = binaryRate;
	}

	public double getAdditionalRate() {
		return this.additionalRate;
	}

	/**
	 * This method tells us when we should perform Bologna method on application
	 * of this rule
	 * 
	 * @return <tt>true</tt> if and only if this rule is binary and we should
	 *         consider it's additional rate when applying
	 */
	public boolean isUnusualBinary() {
		return this.isBinary && additionalRate != -1;
	}

	public String toString() {
		StringBuffer st = new StringBuffer(leftHandside.toString());
		st.append("->");
		st.append(rightHandside);
		return st.toString();
	}
}