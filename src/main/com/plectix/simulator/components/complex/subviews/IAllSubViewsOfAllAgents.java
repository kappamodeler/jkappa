package com.plectix.simulator.components.complex.subviews;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.interfaces.ISolution;

public interface IAllSubViewsOfAllAgents {
	
	public void build(ISolution solution, List<CRule> rules);
		
	public Iterator<Integer> getAllTypesIdOfAgents();
	
	public List<ISubViews> getAllSubViewsByTypeId(int type);
	
	public Element createXML(Document doc);
	
	public Map<Integer, CAbstractAgent> getFullMapOfAgents();
	
	public void initDeadRules();

	public List<AbstractionRule> getRules();

	public Map<Integer, CAbstractAgent> getAgentNameIdToAgent();

	public void createXML(XMLStreamWriter writer)throws XMLStreamException;
	

}