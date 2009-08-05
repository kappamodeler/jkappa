package com.plectix.simulator.rulecompression;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.plectix.simulator.action.CAction;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CBoundAction;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class RootedRule {
	private final CRule rule;
	private final Set<ActionInfo> actionsInfo = new TreeSet<ActionInfo>();
	// real root to the virtual one
	private final Map<CAgent, CAgent> virtualRoots 
					= new LinkedHashMap<CAgent, CAgent>();
	
	public RootedRule(KappaSystem ks, CRule rule, Collection<CAgent> roots) {
		this.rule = rule;
		for (CAgent root : roots) {
			virtualRoots.put(root, new CAgent(root.getNameId(), ks.generateNextAgentId()));
		}
		createActionsInfo(ks, roots);
	}

	private void addFullFakeRoot(CAgent realRoot, long id) {
		CAgent fakeRoot = new CAgent(realRoot.getName(), 0);
		for (CSite site : realRoot.getSites()) {
			fakeRoot.addSite(new CSite(site.getNameId()));
		}
		virtualRoots.put(realRoot, fakeRoot);
	}
	
	public CAgent getVirtualRoot(CAgent root) {
		return virtualRoots.get(root);
	}
	
	private ActionInfo createActionInfo(CAction act, CAgent ...roots) {
		return new ActionInfo(this, act, roots);
	}
	
	private void createActionsInfo(KappaSystem ks, Collection<CAgent> roots) {
		Map<CAgent, IConnectedComponent> agentComponentMap 
					= new LinkedHashMap<CAgent, IConnectedComponent>();
		
		for (IConnectedComponent cc : rule.getLeftHandSide()) {
			for (CAgent agent : cc.getAgents()) {
				agentComponentMap.put(agent, cc);
			}
		}
		if (rule.getRightHandSide() != null) {
			for (IConnectedComponent cc : rule.getRightHandSide()) {
				for (CAgent agent : cc.getAgents()) {
					agentComponentMap.put(agent, cc);
				}
			}
		}
		
		// we know that roots sorted in the same order that components are
		Iterator<IConnectedComponent> componentIterator = rule.getLeftHandSide().iterator();
		Iterator<CAgent> rootIterator = roots.iterator();
		
		// this map is given only for the left hand side
		Map<IConnectedComponent, CAgent> rootComponentMap 
				= new LinkedHashMap<IConnectedComponent, CAgent>();
		
		// we know that roots sorted in the same order that components are
		while (componentIterator.hasNext()) {
			IConnectedComponent component = componentIterator.next();
			CAgent componentsRoot = rootIterator.next();
			rootComponentMap.put(component, componentsRoot);
		}
		
		Set<CAction> boundActions = new LinkedHashSet<CAction>();
		// first of all get rid of add-actions
		for (CAction act : rule.getActionList()) {
			if (act.getTypeId() == CActionType.NONE.getId()) {
				continue;
			}
			if (act.getTypeId() == CActionType.ADD.getId()) {
				// add new ADD-action with new agent as root
				CAgent root = act.getAgentTo();
				addFullFakeRoot(root, ks.generateNextAgentId());
				actionsInfo.add(createActionInfo(act, root));
			} else if (act.getTypeId() == CActionType.DELETE.getId()) {
				// add new ADD-action with new agent as root
				CAgent root = rootComponentMap.get(act.getLeftCComponent());
				actionsInfo.add(createActionInfo(act, root));
			} else if (act.getTypeId() == CActionType.BOUND.getId()) {
				boolean symmetricalOneAlreadyWas = false;
				for (CAction boundAction : boundActions) {
					if (boundAction.getSiteFrom() == act.getSiteTo()) {
						symmetricalOneAlreadyWas = true;
						break;
					}
				}
				if (!symmetricalOneAlreadyWas) {
					boundActions.add(act);
					Set<CSite> boundingSiteImages = ((CBoundAction)act).getBoundingSites();
					CAgent[] rootAgents = new CAgent[2];
					int i = 0;
					for (CSite site : boundingSiteImages) {
						CAgent boundingAgent = site.getParentAgent();
						IConnectedComponent cc = agentComponentMap.get(boundingAgent);
						rootAgents[i] = rootComponentMap.get(cc);
						if (rootAgents[i] == null) {
							rootAgents[i] = boundingAgent;
						}
						i++;
					}
					actionsInfo.add(createActionInfo(act, rootAgents));
				}
			} else {
				IConnectedComponent cc = agentComponentMap.get(act.getSiteFrom().getParentAgent());
				CAgent root = rootComponentMap.get(cc);
				if (cc == null) {
					root = act.getSiteFrom().getParentAgent();
				}
				actionsInfo.add(createActionInfo(act, root));
			}
		}
	}

	public String toString() {
		return actionsInfo + "\n";
	}

	public Set<ActionInfo> getActionsInfo() {
		return actionsInfo;
	}

	public CRule getPrototypeRule() {
		return rule;
	}
}
