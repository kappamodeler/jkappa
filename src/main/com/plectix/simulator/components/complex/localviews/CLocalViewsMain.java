package com.plectix.simulator.components.complex.localviews;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CLocalViewsMain {
	private IAllSubViewsOfAllAgents subviews;
	private Map<Integer, List<CAbstractAgent>> localViews;

	public CLocalViewsMain(IAllSubViewsOfAllAgents subViews) {
		this.subviews = subViews;
		localViews = new LinkedHashMap<Integer, List<CAbstractAgent>>();
	}

	public void buildLocalViews() {

		  Map<Integer, CAbstractAgent> agentsMap = subviews.getFullMapOfAgents();
		  
		  Set<Integer> keys = agentsMap.keySet();
		  
		  for (Integer agentId : keys){
			  
			  List<CAbstractAgent> setOfLocalViews = new LinkedList<CAbstractAgent>();
		   
			  List<CAbstractAgent> temp = new LinkedList<CAbstractAgent>();
		   
			  for(ISubViews subView : subviews.getAllSubViewsByTypeId(agentId)){
				  
				  if(setOfLocalViews.isEmpty()){
					  temp.addAll(subView.getAllSubViewsCoherent(null));
				  }
				  else{
		     
					  for(CAbstractAgent agent : setOfLocalViews){
						  temp.addAll(subView.getAllSubViewsCoherent(agent));
					  }
				  }
				  setOfLocalViews.clear();
				  for(int i=0; i<temp.size();i++){
					  setOfLocalViews.add(temp.get(i).clone());
		    	  }
				  temp.clear();
			  }
			  
			  localViews.put(agentId, setOfLocalViews);
		  
		  }
	}
	
	
	public int getCountOfCoherentAgent(CAbstractAgent mask){
		int answer =0;
		for(CAbstractAgent agent : localViews.get(mask.getNameId())){
			if(mask.isFit(agent))
				answer++;
		}
		return answer;
	}

	public Map<Integer, List<CAbstractAgent>> getLocalViews() {
		return localViews;
	}
	
	public void writeToXML(XMLStreamWriter xtw) throws XMLStreamException{
		xtw.writeStartElement("Reachables");
		xtw.writeAttribute("Name", "Views");
		for(Map.Entry<Integer, List<CAbstractAgent>> entry : localViews.entrySet()){
			int key = entry.getKey();
			if(key == -1)
				continue;
			List<CAbstractAgent> list = entry.getValue();
			xtw.writeStartElement("Set");
			xtw.writeAttribute("Agent", ThreadLocalData.getNameDictionary().getName(key));
			for(CAbstractAgent agent : list){
				xtw.writeStartElement("Entry");
				xtw.writeAttribute("Data", agent.toStringForXML());
				xtw.writeEndElement();
			}
			xtw.writeEndElement();
		}
		xtw.writeEndElement();
	}
}
