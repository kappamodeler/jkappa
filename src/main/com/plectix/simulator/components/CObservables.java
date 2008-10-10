package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

public class CObservables {
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

	public final void addConnectedComponents(List<CConnectedComponent> list, String name,String line) {
		for (CConnectedComponent component: list) {
			ObservablesConnectedComponent oCC = new ObservablesConnectedComponent(component.getAgents(), name,line);
			oCC.initSpanningTreeMap();
			connectedComponentList.add(oCC);
		}
	}

	public class ObservablesConnectedComponent extends CConnectedComponent {
		private String name;
		private String line;
		public String getLine() {
			return line;
		}

		private List<Integer> countList = new ArrayList<Integer>(); 
		
		public List<Integer> getCountList() {
			return countList;
		}
		
		public final void calculateInjection(){
			countList.add(getInjectionsList().size());
		}

		public ObservablesConnectedComponent(List<CAgent> connectedAgents, String name,String line) {
			super(connectedAgents);
			this.name = name;
			this.line=line;
		}
				
		public String getName() {
			return name;
		}
	}

}
