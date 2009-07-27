package com.plectix.simulator.rulecompression;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/*package*/ class NewRuleBuilder {
	private final KappaSystem ks;
	private final SubstanceMaster master = new SubstanceMaster();
	
	public NewRuleBuilder(KappaSystem ks) {
		this.ks = ks;
	}
	
	/**
	 * This method takes rooted rule and creates the new CRule,
	 * using only ActionsInfo
	 * @param rule
	 * @return
	 */
	public CRule getShorterVersionForQuantitative(RootedRule rule, String name, double rate) {
		ActionsMapper mapper = new ActionsMapper(ks, master);
		// gather info about agents from left hand side
		RuleComponentsMapping rootsFinders = mapper.getActionsMapping(rule, true);
		
		List<CAgent> lhsAgents = new ArrayList<CAgent>(rootsFinders.gatherAgents());
		List<IConnectedComponent> lhs = SimulationUtils.splitAndCopy(ks, lhsAgents);
		
		// add info about added agents
		rootsFinders.join(mapper.getActionsMapping(rule, false));
		
		for (ActionInfo ai : rule.getActionsInfo()) {
			applyActionInfo(ai, rootsFinders);
		}
		
		List<CAgent> rhsAgents = new ArrayList<CAgent>(rootsFinders.gatherAgents());
		List<IConnectedComponent> rhs = (SimulationUtils.splitAndCopy(ks, rhsAgents));
		return new CRule(lhs, rhs, name, rate, (int)ks.generateNextRuleId(), false);
	}
	
	private void applyActionInfo(ActionInfo act, RuleComponentsMapping rootsFinders) {
		CAgent root1 = act.getRoots().get(0);
		CommonPartFinder cpf1 = rootsFinders.getStorage(root1);
		SitePath path1 = act.getPaths(root1).get(0);
		switch(act.getType()) {
		case MODIFY:{
			int modifiedSiteInfo = path1.getAndRemoveLast().getNameId();
			CAgent modifiedAgent = cpf1.getAgent(path1.hash());
			CSite modifiedSite = modifiedAgent.getSiteByNameId(modifiedSiteInfo);
			modifiedSite.setInternalState(new CInternalState(act.getNewInternalStateName()));
			break;
		}
		case BOUND:{
			SitePath path2 = act.getPaths().get(1);
			CAgent root2 = act.getRoots().get(1);
			CommonPartFinder cpf2 = rootsFinders.getStorage(root2);
			int modifiedSiteName1 = path1.getAndRemoveLast().getNameId();
			CAgent modifiedAgent1 = cpf1.getAgent(path1.hash());
			int modifiedSiteName2 = path2.getAndRemoveLast().getNameId();
			CAgent modifiedAgent2 = cpf2.getAgent(path2.hash());
			CSite modifiedSite1 = modifiedAgent1.getSiteByNameId(modifiedSiteName1);
			CSite modifiedSite2 = modifiedAgent2.getSiteByNameId(modifiedSiteName2);
			master.connect(modifiedSite1, modifiedSite2);
			break;
		}
		case BREAK:{
			int modifiedSiteName = path1.getAndRemoveLast().getNameId();
			CAgent modifiedAgent = cpf1.getAgent(path1.hash());
			CSite modifiedSite = modifiedAgent.getSiteByNameId(modifiedSiteName);
			master.breakConnection(modifiedSite);
			break;
		}
		case DELETE:{
			path1.getAndRemoveLast();
			cpf1.removeAgent(path1.hash().intern());
			break;
		}
		case ADD:{
			path1.getAndRemoveLast();
			cpf1.addOrCompleteAgent(path1.hash().intern(), act.getRoots().get(0));
			break;
		}
		}
	}
	
	public SubstanceMaster getSubstanceMaster() {
		return master;
	}
}
