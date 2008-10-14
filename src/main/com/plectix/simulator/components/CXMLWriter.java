package com.plectix.simulator.components;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.simulator.SimulatorManager;

public class CXMLWriter {

	public CXMLWriter() {

	}

	public void writeToXML(String fileName)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element simplxSession = doc.createElement("SimplxSession");
		simplxSession.setAttribute("CommandLine", "cmd");
		simplxSession.setAttribute("InputFile", "file");
		simplxSession.setAttribute("TimeStamp", "stmp");
		doc.appendChild(simplxSession);

		Element influenceMap = doc.createElement("InfluenceMap");

		CObservables obs = SimulationMain.getSimulationManager()
				.getSimulationData().getObservables();
		List<CRule> rules = SimulationMain.getSimulationManager()
				.getSimulationData().getRules();
		int rulesAndObsNumber = obs.getConnectedComponentList().size()
				+ rules.size();

		for (int i = obs.getConnectedComponentList().size() - 1; i >= 0; i--) {
			Element node = doc.createElement("Node");
			node.setAttribute("ID", Integer.toString(rulesAndObsNumber--));
			node.setAttribute("Type", "OBSERVABLE");
			String obsName = obs.getConnectedComponentList().get(i).getName();

			if (obsName == null)
				obsName = obs.getConnectedComponentList().get(i).getLine();

			node.setAttribute("Text", '[' + obsName + ']');
			node.setAttribute("Data", obs.getConnectedComponentList().get(i)
					.getLine());
			node.setAttribute("Name", '[' + obsName + ']');
			influenceMap.appendChild(node);
		}

		for (int i = rules.size() - 1; i >= 0; i--) {
			Element node = doc.createElement("Node");
			node.setAttribute("ID", Integer.toString(rulesAndObsNumber--));
			node.setAttribute("Type", "RULE");
			node.setAttribute("Text", rules.get(i).getName());

			String line = SimulatorManager.printPartRule(rules.get(i)
					.getLeftHandSide());
			line = line + "->";
			line = line
					+ SimulatorManager.printPartRule(rules.get(i)
							.getRightHandSide());

			node.setAttribute("Data", line);
			node.setAttribute("Name", rules.get(i).getName());
			influenceMap.appendChild(node);
		}

		simplxSession.appendChild(influenceMap);

		Element simulation = doc.createElement("Simulation");
		simulation.setAttribute("TotalEvents", Integer.toString(obs
				.getCountTimeList().size()));
		simulation.setAttribute("TotalTime", Double.toString(SimulationMain
				.getSimulationManager().getSimulationData().getTimeLength()));
		simulation.setAttribute("InitTime", "0");
		simulation.setAttribute("TimeSample", "0.01");
		simplxSession.appendChild(simulation);

		for (int i = obs.getConnectedComponentList().size() - 1; i >= 0; i--) {
			Element node = doc.createElement("Plot");
			node.setAttribute("Type", "OBSERVABLE");
			String obsName = obs.getConnectedComponentList().get(i).getName();

			if (obsName == null)
				obsName = obs.getConnectedComponentList().get(i).getLine();

			node.setAttribute("Text", '[' + obsName + ']');
			simulation.appendChild(node);
		}
		Element csv = doc.createElement("CSV");
		CDATASection cdata = doc.createCDATASection("\n");
		double timeSampleMin = 0.01;

		appendData(obs, cdata, 0);
		double timeLast = obs.getCountTimeList().get(0);

		for (int i = 0; i < obs.getCountTimeList().size(); i++) {
			if ((obs.getCountTimeList().get(i) - timeLast) > timeSampleMin) {
				timeLast = obs.getCountTimeList().get(i);
				appendData(obs, cdata, i);
			}

		}
		csv.appendChild(cdata);
		simulation.appendChild(csv);

		TransformerFactory trFactory = TransformerFactory.newInstance();
		Transformer transformer = trFactory.newTransformer();
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamesult = new StreamResult(fileName);
		transformer.transform(domSource, streamesult);
	}

	private void appendData(CObservables obs, CDATASection cdata, int index) {
		String enter = "\n";
		cdata.appendData(obs.getCountTimeList().get(index).toString());
		for (int j = obs.getConnectedComponentList().size() - 1; j >= 0; j--) {
			cdata.appendData(",");
			ObservablesConnectedComponent oCC = obs.getConnectedComponentList()
					.get(j);
			if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX)
				cdata.appendData(oCC.getCountList().get(index).toString());
			else
				cdata.appendData(obs.getConnectedComponentList().get(
						oCC.getMainAutomorphismNumber()).getCountList().get(
						index).toString());
		}
		cdata.appendData(enter);
	}
}
