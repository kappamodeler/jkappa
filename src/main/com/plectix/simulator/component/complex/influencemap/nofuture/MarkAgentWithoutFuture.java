package com.plectix.simulator.component.complex.influencemap.nofuture;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*package*/ final class MarkAgentWithoutFuture {
	private final Map<String,List<MarkSiteWithoutFuture>> sitesMap 
		= new LinkedHashMap<String, List<MarkSiteWithoutFuture>>();

	public final void addMarkSite(MarkSiteWithoutFuture mSite){
		List<MarkSiteWithoutFuture> mSites = sitesMap.get(mSite.getSite().getName());
		if(mSites == null){
			mSites = new LinkedList<MarkSiteWithoutFuture>();
			sitesMap.put(mSite.getSite().getName(), mSites);
		}
		mSites.add(mSite);
	}
	
	public final List<MarkSiteWithoutFuture> getMarkSites(String key){
		return sitesMap.get(key);
	}
}
