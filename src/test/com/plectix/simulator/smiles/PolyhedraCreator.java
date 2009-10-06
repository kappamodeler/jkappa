package com.plectix.simulator.smiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates polyhedra from the information downloaded from
 * http://www.rwgrayprojects.com/Lynn/Coordinates/coord01.html
 * 
 * @author ecemis
 */
public class PolyhedraCreator {

	private static final String TEST_DATA_DIRECTORY = "test.data"
			+ File.separator + "smiles" + File.separator;
	private static final String POLYHERDRA_COORDINATES_FILENAME = "polyhedra_coordinates.txt";

	private static final String VERTICES_LABEL = "Vertices";
	private static final String EDGE_MAP_LABEL = "Edge Map";

	private static final String[] AGENT_NAMES = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J" };
	private static final String[] SITE_NAMES = { "x", "y", "z", "t", "q", "r",
			"p", "m", "h", "g", "f", "s", "w", "k", "j", "n", "b", "c", "a",
			"d" };

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				TEST_DATA_DIRECTORY + POLYHERDRA_COORDINATES_FILENAME));

		String vertices = null;
		String edgeMap = null;
		int counter = 0;
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			if (line.startsWith(VERTICES_LABEL)) {
				vertices = line.replaceAll(VERTICES_LABEL, "").replaceAll(
						"\\{", "").replaceAll("\\}", "").replaceAll(" ", "");
			} else if (line.startsWith(EDGE_MAP_LABEL)) {
				if (vertices == null) {
					throw new RuntimeException(
							"Found an Edge Map without Vertices!");
				}
				edgeMap = line.replaceAll(EDGE_MAP_LABEL, "").replaceAll("\\{",
						"").replaceAll("\\}", "").replaceAll(" ", "");

				String[] verticesArray = vertices.split(",");
				String[] edgeMapArray = edgeMap.split(",");
				for (int i = 1; i < Math.min(1 + verticesArray.length,
						AGENT_NAMES.length); i++) {
					createPolydedron(verticesArray, edgeMapArray, i);
					counter++;
				}

				vertices = null;
				edgeMap = null;
			}
		}

		System.err.println("Created " + counter + " polyhedra");
	}

	private static void createPolydedron(String[] vertices, String[] edgeMap,
			int numberOfAgentNames) {

		Map<String, PolyhedraAgent> agentMap = new LinkedHashMap<String, PolyhedraAgent>(
				vertices.length);
		for (int i = 0; i < vertices.length; i++) {
			agentMap.put(vertices[i], new PolyhedraAgent(AGENT_NAMES[i
					% numberOfAgentNames]));
		}

		if (edgeMap.length % 2 != 0) {
			throw new RuntimeException("Odd number of edges in the Edge Map: "
					+ edgeMap.length);
		}

		for (int i = 0; i < edgeMap.length / 2; i++) {
			PolyhedraAgent agent1 = agentMap.get(edgeMap[2 * i]);
			PolyhedraAgent agent2 = agentMap.get(edgeMap[2 * i + 1]);

			agent1.addNeighbor(agent2);
			agent2.addNeighbor(agent1);
		}

		int numberOfSites = 0;
		for (PolyhedraAgent agent : agentMap.values()) {
			numberOfSites = Math.max(numberOfSites, agent
					.getNumberOfNeighbors());
		}

		StringBuffer kappaStringBuffer = new StringBuffer();
		int lastLinkIndex = 1;
		for (PolyhedraAgent agent : agentMap.values()) {
			lastLinkIndex = agent.computeKappaString(lastLinkIndex,
					numberOfSites);
			kappaStringBuffer.append(agent.getKappaString() + ", ");
		}
		kappaStringBuffer.deleteCharAt(kappaStringBuffer.length() - 2);

		System.err.println(kappaStringBuffer.toString());
	}

	private static final class PolyhedraAgent {
		private List<PolyhedraAgent> neighbors = new ArrayList<PolyhedraAgent>();
		private int[] agentLinks = null;
		private String kappaString = null;
		private String agentName = null;

		public PolyhedraAgent(String agentName) {
			super();
			this.agentName = agentName;
		}

		public void addNeighbor(PolyhedraAgent agent) {
			neighbors.add(agent);
		}

		public int computeKappaString(int lastLinkIndex, int numberOfSites) {
			createAgentLinks();

			StringBuffer kappaStringBuffer = new StringBuffer(agentName + "(");
			for (int i = 0; i < agentLinks.length; i++) {
				int linkIndex = agentLinks[i];
				if (linkIndex == 0) {
					linkIndex = lastLinkIndex++;
					neighbors.get(i).setLinkIndex(this, linkIndex);
				}
				kappaStringBuffer.append(SITE_NAMES[i] + "!" + linkIndex + ",");
			}

			for (int i = agentLinks.length; i < numberOfSites; i++) {
				kappaStringBuffer.append(SITE_NAMES[i] + ",");
			}

			kappaStringBuffer.replace(kappaStringBuffer.length() - 1,
					kappaStringBuffer.length(), ")");
			kappaString = kappaStringBuffer.toString();
			return lastLinkIndex;
		}

		private void setLinkIndex(PolyhedraAgent polyhedraAgent, int linkIndex) {
			createAgentLinks();
			agentLinks[neighbors.indexOf(polyhedraAgent)] = linkIndex;
		}

		private void createAgentLinks() {
			if (agentLinks == null) {
				agentLinks = new int[neighbors.size()];
				Arrays.fill(agentLinks, 0);
			}
		}

		public final String getKappaString() {
			return kappaString;
		}

		public final int getNumberOfNeighbors() {
			return neighbors.size();
		}
	}
}
