package com.plectix.simulator.staticanalysis.rulecompression;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Site;
/**
 * agent with link to real agent
 * @author nkalinin
 *
 */
public class ShadowAgent extends Agent {
	private boolean isActionAgent = false;
	private int range;
	private Site siteToParentInTree;
	
	//agent in rule handside
	private Agent realAgent;
	
	public ShadowAgent(String name, long id) {
		super(name,id);
	}

	public void setRealAgent(Agent realAgent) {
		this.realAgent = realAgent;
	}

	public Agent getRealAgent() {
		return realAgent;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getRange() {
		return range;
	}

	public void setActionAgent() {
		isActionAgent = true;
	}

	public boolean isActionAgent() {
		return isActionAgent;
	}

	public void setSiteToParentInTree(Site siteToParentInTree) {
		this.siteToParentInTree = siteToParentInTree;
	}

	public Site getParentInTree() {
		return siteToParentInTree;
	}
}
