package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.NewsAddress;

public class CObservables  {
	private List<ObservablesConnectedComponent> connectedComponentList = new ArrayList<ObservablesConnectedComponent>();
	public static List<Double> countTimeList = new ArrayList<Double>();
	
//	public static void addCountTimeList(Double count){
//		countTimeList.add(count);
//	}
	
	public static List<Double> getCountTimeList(){
		return countTimeList;
	}
	
	public final void PrintObsCount(){
		for(ObservablesConnectedComponent cc : connectedComponentList){
			System.out.println(cc.getInjectionsList().size());
		}
	}
	
	public final void calculateObs(Double time){
		countTimeList.add(time);
		//System.out.println("---------------------------Time: "+time);
		for(ObservablesConnectedComponent cc : connectedComponentList){
			cc.calculateInjection();
		}
	//	PrintObsCount();
	}
	
	public CObservables() {
	}

	public final List<ObservablesConnectedComponent> getConnectedComponentList() {
		return connectedComponentList;
	}

	public final void addConnectedComponents(List<CConnectedComponent> list, String name) {
		for (CConnectedComponent component: list) {
			ObservablesConnectedComponent oCC = new ObservablesConnectedComponent(component.getAgents(), name);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
		}
	}

	public class ObservablesConnectedComponent extends CConnectedComponent {
		private String name;
		private List<Integer> countList = new ArrayList<Integer>(); 
		
		public List<Integer> getCountList() {
			return countList;
		}
		
		public final void calculateInjection(){
			countList.add(getInjectionsList().size());
		}

		public ObservablesConnectedComponent(List<CAgent> connectedAgents, String name) {
			super(connectedAgents);
			this.name = name;
		}
				
		public String getName() {
			return name;
		}
	}

}
