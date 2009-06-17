package com.plectix.simulator.XMLmaps;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {
	private ArrayList<Node> nodes;
	private ArrayList<Connection> connections;
	private ArrayList<Agent> agents;
	private ArrayList<Bond> bonds;

	private boolean isNodeTag = false;
	private boolean isInfluenceMapTag = false;
	private boolean isConnectionTag = false;
	private boolean isBondTag = false;
	private boolean isAgentTag = false;
	private boolean isSiteTag = false;
	private boolean isRuleTag = false;
	private boolean isContactMap = false;
	private boolean isAgent = false;

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public ArrayList<Agent> getAgents() {
		return agents;
	}

	public ArrayList<Bond> getBonds() {
		return bonds;
	}

	@Override
	public void startDocument() throws SAXException {
		nodes = new ArrayList<Node>();
		connections = new ArrayList<Connection>();
		agents = new ArrayList<Agent>();
		bonds = new ArrayList<Bond>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("InfluenceMap".equals(qName))
			isInfluenceMapTag = true;
		if ("ContactMap".equals(qName))
			isContactMap = true;

		if (isInfluenceMapTag) {
			isNodeTag = "Node".equals(qName);
			if (isNodeTag) {
				String data = attributes.getValue("Data");
				String id = attributes.getValue("ID");
				if (id == null)
					id = attributes.getValue("Id");
				String name = attributes.getValue("Name");
				if (name == null)
					name = "";
				String text = attributes.getValue("Text");
				if (text.equals(data))
					text = "";
				String type = attributes.getValue("Type");

				nodes.add(new Node(data, id, name, text, type));
			}

			isConnectionTag = "Connection".equals(qName);
			if (isConnectionTag) {
				String FromNode = attributes.getValue("FromNode");
				String ToNode = attributes.getValue("ToNode");
				String Relation = attributes.getValue("Relation");
				connections.add(new Connection(FromNode, ToNode, Relation));
			}
		}

		if (isContactMap) {
			if ("Agent".equals(qName)) {
				isAgentTag = true;
				agents.add(new Agent(attributes.getValue("Name")));
			}

			if (isAgentTag) {
				if ("Site".equals(qName)) {
					isSiteTag = true;
					Site site = new Site(attributes.getValue("Name"),
							Boolean.parseBoolean(attributes
									.getValue("CanChangeState")), Boolean
									.parseBoolean(attributes
											.getValue("CanBeBound")));
					agents.get(agents.size() - 1).addSite(site);
				}
				isRuleTag = "Rule".equals(qName);
				if (isRuleTag) {
					if (isSiteTag) {
						agents.get(agents.size() - 1).getLastSite().add(
								Integer.parseInt(attributes.getValue("Id")));
					} else {
						agents.get(agents.size() - 1).addRuleId(
								Integer.parseInt(attributes.getValue("Id")));
					}
				}
			}

			// ////////
			if ("Bond".equals(qName)) {
				isBondTag = true;

				bonds.add(new Bond(attributes.getValue("FromAgent"), attributes
						.getValue("FromSite"), attributes.getValue("ToAgent"),
						attributes.getValue("ToSite")));
			}
//			if (isBondTag) {
//				isRuleTag = "Rule".equals(qName);
//				if (isRuleTag) {
//					bonds.get(bonds.size() - 1).addRuleId(
//							Integer.parseInt(attributes.getValue("Id")));
//				}
//			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		isNodeTag = false;
		isConnectionTag = false;
		if ("Agent".equals(qName)) {
			isAgentTag = false;
		}
		isSiteTag = false;
		isRuleTag = false;
		if ("ContactMap".equals(qName)) {
			isContactMap = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}

}
