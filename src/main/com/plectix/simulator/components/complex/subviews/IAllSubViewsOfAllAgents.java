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
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.interfaces.ISolution;

public interface IAllSubViewsOfAllAgents {
	
	public void build(ISolution solution, List<CRule> rules);
		
	public Iterator<Integer> getAllTypesIdOfAgents();
	
	public List<String> getAllTypesOfAgents();
	
	public List<ISubViews> getAllSubViewsByTypeId(int type);
	
	public List<ISubViews> getAllSubViewsByType(String type);
	
	public ISubViews getSubViewForRule(String typeOfAgent, CRule rule);
	
	public Element createXML(Document doc);
	
	public Map<Integer, CAbstractAgent> getFullMapOfAgents();
	
	public void initDeadRules();

	public List<SubViewsRule> getRules();

	public Map<Integer, CAbstractAgent> getAgentNameIdToAgent();

	public void createXML(XMLStreamWriter writer)throws XMLStreamException;
	
	public List<CAbstractAgent> getAllCorehentViews(CAbstractAgent agent);

}