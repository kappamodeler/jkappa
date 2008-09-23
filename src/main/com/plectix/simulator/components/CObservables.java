package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CObservables  {
	private List<ObservablesConnectedComponent> connectedComponentList = new ArrayList<ObservablesConnectedComponent>();
	
	public CObservables() {
	}

	public final List<ObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<CConnectedComponent> list, String name) {
		for (CConnectedComponent component: list) {
			connectedComponentList.add(new ObservablesConnectedComponent(component.getAgents(), name));
		}
	}

	public static class ObservablesConnectedComponent extends CConnectedComponent {
		private String name;
		private List<Integer> countTimeList = new ArrayList<Integer>();
		
		public ObservablesConnectedComponent(List<CAgent> connectedAgents, String name) {
			super(connectedAgents);
			this.name = name;
		}
		
		public void addCountTimeList(Integer count){
			countTimeList.add(count);
		}
		
		public List<Integer> getCountTimeList(){
			return countTimeList;
		}
		
		public String getName() {
			return name;
		}
	}

}
