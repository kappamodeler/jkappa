package com.plectix.simulator.rulecompression;

import java.util.*;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ class ActionsMapper {
	private final KappaSystem ks;
	private final SubstanceMaster substanceMaster;
	
	public ActionsMapper(KappaSystem ks, SubstanceMaster master) {
		this.ks = ks;
		this.substanceMaster = master;
	}
	
	private Collection<CAgent> getAddedRoots(RootedRule rr) {
		Set<CAgent> set = new LinkedHashSet<CAgent>();
		for (ActionInfo ai : rr.getActionsInfo()) {
			if (ai.getType() == CActionType.ADD) {
				// the only one root
				set.addAll(ai.getRoots());
			}
		}
		return set;
	}
	/**
	 * 
	 * @return common part finders for non-add actions
	 */
	public RuleComponentsMapping getActionsMapping(RootedRule rr, boolean onlyNonAddedRoots) {
		RuleComponentsMapping rootsFinders = new RuleComponentsMapping();
		Collection<CAgent> addedRoots = getAddedRoots(rr);
		for (ActionInfo ai : rr.getActionsInfo()) {
			
			/*
			 * when we complete the cycle with the bound action, 
			 * we certainly have one agent as both roots in this actions,
			 * so we can handle each unique root of the fixed action only one time
			 */
			Set<CAgent> usedRoots = new LinkedHashSet<CAgent>();
			for (CAgent root : ai.getRoots()) {
				if (!onlyNonAddedRoots ^ addedRoots.contains(root)) {
					continue;
				}
				
				if (usedRoots.contains(root)) {
					continue;
				}
				
				CommonPartFinder cpf = rootsFinders.getStorage(root);
				if (cpf == null) {
					cpf = new CommonPartFinder(ks, root, substanceMaster);
					rootsFinders.addEntry(root, cpf);						
				}
				for (SitePath sp : ai.getPaths()) {
					// this one adds to cpf only those paths, which start with root
					cpf.addPath(sp);
				}
				usedRoots.add(root);
			}
		}
		return rootsFinders;
	}
}
