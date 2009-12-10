package com.plectix.simulator.staticanalysis;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.ObservableInterface;


public class TestObservables {
	
	@Test
	public void testOutputMode(){
		Observables obs = new Observables();
		double fullTime=10;
		double initialTime=0;
		long events=10;
		int points=10;
		boolean isTime=true;
		obs.init(fullTime, initialTime, events, points, isTime);
		List<Agent> connectedAgents=new LinkedList<Agent>();
		String name = "";
		String line = ""; 
		int id =0;
		boolean isUnique = true;
		
		ObservableMock obsMock = new ObservableMock(connectedAgents, name, line, id, isUnique);
		List<ObservableInterface> list = new LinkedList<ObservableInterface>();
		list.add(obsMock);
		
		obs.setComponentList(list);
		int count = 0;
		double time = 0;
		
		obsMock.setValue(1);
		obs.addInitialState();
		assertEquals(Double.valueOf(obs.getCountTimeList().get(0).getTime()),Double.valueOf(0.0));
		assertEquals(Double.valueOf(obs.getComponentList().get(0).getLastValue()),Double.valueOf(1));
		
		obsMock.setValue(2);
		count++;
		time+=1.5;
		obs.calculateObs(time, count, isTime);
		
		assertEquals(Double.valueOf(obs.getCountTimeList().get(1).getTime()),Double.valueOf(1.5));
		assertEquals(Double.valueOf(obs.getComponentList().get(0).getLastValue()),Double.valueOf(2));
	
		
		
	}

}
