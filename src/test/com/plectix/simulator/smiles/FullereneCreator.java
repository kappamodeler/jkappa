package com.plectix.simulator.smiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FullereneCreator {

	private static final String TEST_DATA_DIRECTORY = "test.data"
			+ File.separator + "smiles" + File.separator + "fullerenes-ccd1";
	private static final String OUTPUT_FILENAME = "Fullerenes.txt";

	private static final String[] AGENT_NAMES = { "A", "B", "C", "D", "E", "F" };
	private static final String[] SITE_NAMES = { "x", "y", "z", "t", "q", "r",
			"p", "m", "h", "g", "f", "s", "w", "k", "j", "n", "b", "c", "a",
			"d" };

	public static void main(String[] args) throws IOException {
		List<String> ccd1Files = crawlFolder(TEST_DATA_DIRECTORY,
				new ArrayList<String>());
		PrintStream outputStream = new PrintStream(new FileOutputStream(
				OUTPUT_FILENAME));

		int counter = 0;
		for (String file : ccd1Files) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			outputStream.println(createFullerene(reader));
			reader.close();
			counter++;
		}

		outputStream.close();
		System.err.println("Created " + counter + " fullerenes");
	}

	private static final String createFullerene(BufferedReader reader)
			throws IOException {
		String line = reader.readLine();
		int numberOfVertices = Integer.parseInt(line.trim());
		List<FullereneVertex> vertices = new ArrayList<FullereneVertex>(
				numberOfVertices);

		line = reader.readLine();
		while (line != null) {
			vertices.add(new FullereneVertex(AGENT_NAMES[0], line));
			line = reader.readLine();
		}

		if (vertices.size() != numberOfVertices) {
			throw new RuntimeException("Unexpected number of vertices: "
					+ vertices.size() + " != " + numberOfVertices);
		}

		StringBuffer kappaStringBuffer = new StringBuffer();
		int lastLinkIndex = 1;
		for (FullereneVertex vertix : vertices) {
			vertix.sanityCheckNeighbors(vertices);
			lastLinkIndex = vertix.computeKappaString(vertices, lastLinkIndex);
			kappaStringBuffer.append(vertix.getKappaString() + ", ");
		}
		kappaStringBuffer.deleteCharAt(kappaStringBuffer.length() - 2);

		return kappaStringBuffer.toString();
	}

	private static final List<String> crawlFolder(String testDataDirectory,
			List<String> ccd1Files) {
		File file = new File(testDataDirectory);

		if (!file.exists()) {
			throw new RuntimeException("File " + file.getName()
					+ " does not exist!");
		}

		if (file.isFile()) {
			ccd1Files.add(file.getAbsolutePath());
		} else if (file.isDirectory()) {
			for (String f : file.list()) {
				crawlFolder(testDataDirectory + File.separator + f, ccd1Files);
			}
		} else {
			throw new RuntimeException("Don't know how to deal with file "
					+ file.getName());
		}

		return ccd1Files;
	}

	private static final class FullereneVertex {
		private static final int NUMBER_OF_FIELDS = 10;

		private String kappaString = null;
		private String agentName = null;

		private int[] neighbors = new int[3];
		private int[] agentLinks = new int[3];

		private int id = -1;
		private double x = Double.NaN;
		private double y = Double.NaN;
		private double z = Double.NaN;

		public FullereneVertex(String agentName, String line) {
			super();
			this.agentName = agentName;
			Arrays.fill(agentLinks, 0);
			Arrays.fill(neighbors, -1);

			String[] fields = line.split(" ");
			String[] data = new String[NUMBER_OF_FIELDS];
			int i = 0;
			for (String field : fields) {
				if (field.length() > 0) {
					// System.err.println("|" + field.trim() + "|" + " where " +
					// line);
					data[i++] = field.trim();
				}
			}

			i = 1;
			id = Integer.parseInt(data[i++]);
			x = Double.parseDouble(data[i++]);
			y = Double.parseDouble(data[i++]);
			z = Double.parseDouble(data[i++]);
			i++;
			neighbors[0] = Integer.parseInt(data[i++]);
			neighbors[1] = Integer.parseInt(data[i++]);
			neighbors[2] = Integer.parseInt(data[i++]);
		}

		public final String getKappaString() {
			return kappaString;
		}

		public final int computeKappaString(List<FullereneVertex> vertices,
				int lastLinkIndex) {
			StringBuffer kappaStringBuffer = new StringBuffer(agentName + "(");
			for (int i = 0; i < agentLinks.length; i++) {
				int linkIndex = agentLinks[i];
				if (linkIndex == 0) {
					linkIndex = lastLinkIndex++;
					vertices.get(neighbors[i] - 1)
							.setLinkIndex(this, linkIndex);
				}
				kappaStringBuffer.append(SITE_NAMES[i] + "!" + linkIndex + ",");
			}

			kappaStringBuffer.replace(kappaStringBuffer.length() - 1,
					kappaStringBuffer.length(), ")");
			kappaString = kappaStringBuffer.toString();
			return lastLinkIndex;
		}

		private final void setLinkIndex(FullereneVertex fullereneVertex,
				int linkIndex) {
			for (int i = 0; i < neighbors.length; i++) {
				if (fullereneVertex.id == neighbors[i]) {
					agentLinks[i] = linkIndex;
					return;
				}
			}

			throw new RuntimeException("Could not find the neighbor!");
		}

		public final void sanityCheckNeighbors(List<FullereneVertex> vertices) {
			List<FullereneVertex> verticesCopy = new ArrayList<FullereneVertex>(
					vertices);
			final FullereneVertex thisVertex = this;

			Collections.sort(verticesCopy, new Comparator<FullereneVertex>() {
				public int compare(final FullereneVertex o1,
						final FullereneVertex o2) {
					return Double.compare(thisVertex
							.computeDistanceSquareTo(o1), thisVertex
							.computeDistanceSquareTo(o2));
				}
			});

			for (int i = 0; i < neighbors.length; i++) {
				boolean found = false;
				// we are number 0 on the list!
				for (int j = 1; j < 1 + neighbors.length; j++) {
					if (neighbors[i] == verticesCopy.get(j).id) {
						found = true;
						break;
					}
				}
				if (!found) {
					StringBuffer sb = new StringBuffer();
					for (int j = 0; j < verticesCopy.size(); j++) {
						FullereneVertex o1 = verticesCopy.get(j);
						sb.append(o1.id + " -> " + computeDistanceSquareTo(o1)
								+ "\n");
					}
					throw new RuntimeException(
							"sanityCheckNeighbors failed for vertix: " + id
									+ " (" + x + ", " + y + ", " + z + ") ["
									+ neighbors[0] + " " + neighbors[1] + " "
									+ neighbors[2] + "] ["
									+ verticesCopy.get(0).id + " "
									+ verticesCopy.get(1).id + " "
									+ verticesCopy.get(2).id + "]\n"
									+ sb.toString());
				}
			}
		}

		private final double computeDistanceSquareTo(final FullereneVertex o) {
			return (o.x - x) * (o.x - x) + (o.y - y) * (o.y - y) + (o.z - z)
					* (o.z - z);
		}

	}
}
