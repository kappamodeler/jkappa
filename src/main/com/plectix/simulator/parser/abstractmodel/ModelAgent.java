package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.component.NamedEntity;
import com.plectix.simulator.parser.util.StringUtil;

public final class ModelAgent extends NamedEntity implements Comparable<ModelAgent> {
	private static final String DEFAULT_NAME = null;
	private final List<ModelSite> sites = new ArrayList<ModelSite>();
	private final String name;
	
	public ModelAgent(String name) {
		this.name = name;
	}
	
	public final void addSite(ModelSite site) {
		this.sites.add(site);
	}

	public final List<ModelSite> getSites() {
		return sites;
	}

	public final int compareTo(ModelAgent agent) {
		return name.compareTo(agent.name);
	}
	
	//-------------------------------toString-----------------
	
	
	//TODO remove this code and implement AbstractSite 
	private static final class ComparableSite implements Comparable<ComparableSite>{
		private final ModelSite site;
		
		public ComparableSite(ModelSite site) {
			this.site = site;
		}
		
		public final ModelSite getSite() {
			return site;
		}
		
		@Override
		public final int compareTo(ComparableSite siteCompareTo) {
			return site.getName().compareTo(siteCompareTo.site.getName());
		}
		
		@Override
		public final String toString() {
			return site.toString();
		}
	}
	
	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(name + "(");
		List<ComparableSite> sortedSites = new ArrayList<ComparableSite>();
		
		for (ModelSite site : this.sites) {
			sortedSites.add(new ComparableSite(site));
		}
		Collections.sort(sortedSites);

		sb.append(StringUtil.listToString(sortedSites));
		
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}

	@Override
	public String getName() {
		return name;
	}
}
