package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class CContactMapAbstractAction {
	private Map<Integer, List<IContactMapAbstractSite>> siteMap;
	private CContactMapAbstractRule rule;
	private List<IContactMapAbstractSite> sitesToAdd;
	private List<UCorrelationAbstractAgent> correlationSites;
	
//	NONE(-1),
//	BREAK(0),
//	DELETE(1),
//	ADD(2),
//	BOUND(3),
//	MODIFY(4);
	
	public CContactMapAbstractAction(CContactMapAbstractRule rule){
		this.rule = rule;
		this.sitesToAdd = new ArrayList<IContactMapAbstractSite>();
		correlationSites = new ArrayList<UCorrelationAbstractAgent>();
		createAtomicActions();
	}

	private void createAtomicActions(){
		// TODO createAtomicActions
		boolean[] checkList = new boolean[rule.getRhsSites().size()];
		for(IContactMapAbstractAgent a : rule.getLhsAgent()){
			UCorrelationAbstractAgent uAgent = new UCorrelationAbstractAgent(this,a,null,ECorrelationType.CORRELATION_LHS_AND_RHS);
			correlationSites.add(uAgent);
			IContactMapAbstractAgent toAgent = findCorrelation(a,uAgent,checkList);
			if(toAgent == null)
				uAgent.setType(CActionType.DELETE);
			else
				uAgent.setToAgent(toAgent);
			uAgent.initAtomicActionList();
		}
		findAddAction(checkList);
	}
	
	private void findAddAction(boolean[] checkList) {
		int i=0;
		for(boolean b : checkList){
			if(!b){
				IContactMapAbstractSite nSite = new CContactMapAbstractSite(rule.getRhsSites().get(i));
				sitesToAdd.add(nSite);
			}
			i++;
		}
	}

	private IContactMapAbstractAgent findCorrelation(IContactMapAbstractAgent agent,UCorrelationAbstractAgent uAgent,boolean[] checkList){
		int i=0;
		// TODO
		
//		for(IContactMapAbstractSite s : rule.getRhsSites()){
//			if(site.equalz(s) && !checkList[i]){
//				checkList[i] = true;
//				return site;
//			}
//			i++;
//		}
//		
//		i=0;
//		for(IContactMapAbstractSite s : rule.getRhsSites()){
//			i++;
//			if(!isPartEqualSite(s, site) || checkList[i-1])
//				continue;
//			if(s.equalsLinkState(site)){
//				if(!s.equalsInternalState(site)){
//					IContactMapAbstractSite nSite = new CContactMapAbstractSite(s);
//					uSite.setType(CActionType.MODIFY);
//					checkList[i-1]=true;
//					return nSite;
//				}
//			}
//		}
//		
//		i=0;
//		for(IContactMapAbstractSite s : rule.getRhsSites()){
//			i++;
//			if(!isPartEqualSite(s, site) || checkList[i-1])
//				continue;
//			if(s.equalsInternalState(site)){
//				if(!s.equalsLinkState(site)){
//					IContactMapAbstractSite nSite = new CContactMapAbstractSite(s);
//					uSite.setType(CActionType.ABSTRACT_BREAK_OR_BOUND);
//					checkList[i-1]=true;
//					return nSite;
//				}
//			}
//		}
//		
//		i=0;
//		for(IContactMapAbstractSite s : rule.getRhsSites()){
//			i++;
//			if(!isPartEqualSite(s, site) || checkList[i-1])
//				continue;
//			IContactMapAbstractSite nSite = new CContactMapAbstractSite(s);
//			uSite.setType(CActionType.ABSTRACT_BREAK_OR_BOUND_AND_MODIFY);
//			return nSite;
//		}
		
		return null;
	}
	
	private boolean isPartEqualSite(IContactMapAbstractSite site1,IContactMapAbstractSite site2){
		if(!site1.equalsNameId(site2))
			return false;
		if(!site1.equalsLinkAgent(site2))
			return false;
		return true;
	}
	
	public List<IContactMapAbstractSite> apply(List<UCorrelationAbstractAgent> injList, CContactMapAbstractSolution solution){
		// TODO apply
		List<IContactMapAbstractSite> listOut = CContactMapAbstractSite.cloneAll(sitesToAdd);
//		int i=0;
//		for(UCorrelationAbstractAgent corLHSandRHS : correlationSites){
//			UCorrelationAbstractAgent corLHSandSolution = injList.get(i);
//			IContactMapAbstractSite newSite = corLHSandSolution.getToSite().clone();
//			listOut.addAll(corLHSandRHS.modifySiteFromSolution(newSite,solution));
//			listOut.add(newSite);
//			i++;
//		}
		return listOut;
	}
}
