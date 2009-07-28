package com.plectix.simulator.rulecompression;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.action.CAction;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.action.CBoundAction;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.rulecompression.util.PathFinder;
import com.plectix.simulator.simulator.ThreadLocalData;

/*package*/ class ActionInfo implements Comparable<ActionInfo> {
	private final CActionType type;
	private final List<SitePath> pathsParameter = new ArrayList<SitePath>();
	private List<CAgent> roots = new LinkedList<CAgent>();
	private int newInternalStateNameId = -1;
	private final RootedRule rule;
	private final List<CAgent> fakeRoots = new ArrayList<CAgent>();
	
	// roots should be given in a fixed order : FROM and then TO
	public ActionInfo(RootedRule rr, CAction action, CAgent ...roots) {
		this.type = CActionType.getById(action.getTypeId());
		this.rule = rr;
		for (CAgent root : roots) {
			this.roots.add(root);
		}
		
		switch (type) {
		case ADD: {
			PathFinder pn = new PathFinder(roots[0]);
			pathsParameter.add(pn.getPath(action.getAgentTo()));
			break;
		}
		case BOUND: {
			PathFinder pnFROM = new PathFinder(roots[0]);
			PathFinder pnTO = new PathFinder(roots[1]);
			CSite[] siteArray = new CSite[2];
			((CBoundAction)action).getBoundingSites().toArray(siteArray);
			SitePath pathFrom = pnFROM.getPath(siteArray[0]);
			SitePath pathTo = pnTO.getPath(siteArray[1]);
			pathsParameter.add(pathFrom);
			pathsParameter.add(pathTo);
			break;
		}
		case BREAK: {
			PathFinder pn = new PathFinder(roots[0]);
			pathsParameter.add(pn.getPath(action.getSiteFrom()));
			break;
		}
		case MODIFY: {
			PathFinder pn = new PathFinder(roots[0]);
			pathsParameter.add(pn.getPath(action.getSiteFrom()));
			newInternalStateNameId = action.getSiteTo().getInternalState().getNameId();
			break;
		}
		case DELETE: {
			PathFinder pn = new PathFinder(roots[0]);
			pathsParameter.add(pn.getPath(action.getAgentFrom()));
			break;
		}
		}
		for (CAgent root : roots) {
			CAgent fakeRoot = rule.getVirtualRoot(root);
			fakeRoots.add(fakeRoot);
		}
		for (SitePath sp : pathsParameter) {
			sp.setRoot(rule.getVirtualRoot(sp.getRoot()));
		}
	}

	public List<CAgent> getRoots() {
		return this.fakeRoots;
	}
	
	// we've got only two paths total, so this method is not critical point
	public List<SitePath> getPaths(CAgent root) {
		if (roots.size() == 1) {
			return this.pathsParameter;
		} else {
			List<SitePath> list = new ArrayList<SitePath>();
			for (SitePath sp : this.pathsParameter) {
				if (sp.getRoot() == root) {
					// there's two paths only, and two roots, so there's only one we should choose
					list.add(sp);
					break;
				}
			}
			return list;
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(type + "(");
		boolean comma = false;
		for (SitePath sp : pathsParameter) {
			if (comma) {
				sb.append(", ");
			} else {
				comma = true;
			}
			sb.append(sp);
		}
		if (newInternalStateNameId != -1) {
			sb.append(", new_state='" 
					+ ThreadLocalData.getNameDictionary().getName(newInternalStateNameId) 
					+ "'");
		}
		sb.append(")");
		return sb.toString(); 
	}
	
	public CActionType getType() {
		return type;
	}
	
	/**
	 * specific method for info about modifying action
	 * @return
	 */
	// TODO maybe this is the point to divide this entity
	public int getNewInternalStateName() {
		return this.newInternalStateNameId;
	}

	public List<SitePath> getPaths() {
		return pathsParameter;
	}

	/**
	 * That's an important point, that deletes and adds 
	 * should be contained before others in any collection
	 */
	public int compareTo(ActionInfo o) {
		if (o == null) {
			return 1;
		}
		if (this.type == CActionType.DELETE) {
			return -1;
		}
		if (o.type == CActionType.DELETE) {
			return 1;
		}
		if (this.type == CActionType.ADD) {
			return -1;
		}
		if (o.type == CActionType.ADD) {
			return 1;
		}
		return 1;
	}
}
