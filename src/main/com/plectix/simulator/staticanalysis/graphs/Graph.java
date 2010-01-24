package com.plectix.simulator.staticanalysis.graphs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public final class Graph {
	private final LinkedList<Edge> edges = new LinkedList<Edge>();
	private final ArrayList<Vertex> vertices = new ArrayList<Vertex>();

	public final ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Notice! Source and target of edge will be changed
	 * 
	 * @return
	 */
	public final List<Edge> getAllEdgesInDirectedCycles() {
		this.clearTags();
		LinkedList<Edge> list = new LinkedList<Edge>();

		Stack<Vertex> stack = new Stack<Vertex>();
		vertices.get(0).setParentVertex(null);
		vertices.get(0).setTag(true);
		vertices.get(0).setMark2(true);
		stack.push(vertices.get(0));

		int temp = 0;

		while (temp != -1) {
			if (!stack.empty()) {
				doStep(stack, list);
			} else {
				temp = needExploreMore();
				if (temp != -1) {
					Vertex v = vertices.get(temp);
					stack.push(v);
					v.setTag(true);
					v.setMark2(true);
				}
			}
		}
		return list;
	}

	/**
	 * we move the path. isTag = true when path include this vertex doesn't use
	 * neighbor
	 * 
	 * @param stack
	 * @param list
	 */
	private final void doStep(Stack<Vertex> stack, LinkedList<Edge> list) {
		Vertex from = stack.peek();
		Vertex to = from.next();
		if (to == from) {
			addEdgeToAnswer(list, from, to);
			return;
		}
		if (to != null) {
			if (!to.isTag()) {
				to.setParentVertex(from);
				to.setMark2(true);
				to.setTag(true);
				stack.push(to);
			} else {
				extractCycle(from, to, list, stack);
			}
		} else {
			stack.pop().setTag(false);
		}

	}

	private final int needExploreMore() {
		for (int i = 0; i < vertices.size(); i++) {
			if (!vertices.get(i).isMark2()) {
				return i;
			}
		}
		return -1;
	}

	// redraw all egdes from vertices of cycle form one Big vertex
	private final void extractCycle(Vertex startVertex, Vertex lastVertex, LinkedList<Edge> edges,
			Stack<Vertex> verticesStack) {
		addEdgeToAnswer(edges, startVertex, lastVertex);
		while (startVertex != lastVertex) {
			addEdgeToAnswer(edges, startVertex.getParentVertex(), startVertex);
			mergeVertices(lastVertex, startVertex);
			startVertex = startVertex.getParentVertex();
			verticesStack.pop();
		}
	}

	private final void addEdgeToAnswer(LinkedList<Edge> answer, Vertex startVertex, Vertex endVertex) {
		Edge edge = getEdge(startVertex, endVertex);
		answer.add(edge);
		startVertex.removeEdge(edge);
		endVertex.removeEdge(edge);
		endVertex.setExplored(0);
		startVertex.setExplored(0);
		edges.remove(edge);
	}

	private final void mergeVertices(Vertex mergedVertex, Vertex vertex) {
		for (Edge eTemp : vertex.getEdges()) {
			if (eTemp.getTarget() == vertex) {
				eTemp.setTarget(mergedVertex);
				mergedVertex.addEdge(eTemp);
			}
			if (eTemp.getSource() == vertex) {
				eTemp.setSource(mergedVertex);
				mergedVertex.addEdge(eTemp);
			}
		}
		vertex.clearEdges();
	}

	public final ArrayList<ArrayList<Vertex>> getAllWeakClosureComponent() {
		this.closeGraph();
		this.sortVerticesByOutDegree();
		this.clearTags();

		ArrayList<ArrayList<Vertex>> result = new ArrayList<ArrayList<Vertex>>();
		int vertexIndex = 0;

		while (vertexIndex < vertices.size()) {
			if (vertices.get(vertexIndex).isTag()) {
				vertexIndex++;
			} else {
				ArrayList<Vertex> anotherSet = new ArrayList<Vertex>();
				vertices.get(vertexIndex).setTag(true);
				anotherSet.add(vertices.get(vertexIndex));
				for (Edge e : vertices.get(vertexIndex).getEdges()) {
					if (e.getSource() == vertices.get(vertexIndex)) {
						anotherSet.add(e.getTarget());
						e.getTarget().setTag(true);
					}
				}
				result.add(anotherSet);
			}
		}

		return result;
	}

	private final void sortVerticesByOutDegree() {
		for (int i = 0; i < vertices.size(); i++) {
			outDegree(vertices.get(i));
		}
		// may be optimize
		Vertex temp = new Vertex();
		for (int i = 0; i < vertices.size() - 1; i++) {
			for (int j = i + 1; j < vertices.size(); j++) {
				if (vertices.get(i).getNeighbourVertices().size() < 
						vertices.get(j).getNeighbourVertices().size()) {
					temp = vertices.get(i);
					vertices.set(i, vertices.get(j));
					vertices.set(j, temp);
				}
			}
		}

	}

	// change neighbors
	private final void outDegree(Vertex v) {
		for (Edge e : v.getEdges()) {
			if (e.getTarget() == v)
				v.removeNeighbour(e.getSource());
		}

	}

	private final void clearTags() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).setTag(false);
		}
	}

	/**
	 * Computes the transitive closure of the given graph.
	 * 
	 * @param graph
	 *            - Graph to compute transitive closure for.
	 */
	final void closeGraph() {
		Set<Vertex> newEdgeTargets = new LinkedHashSet<Vertex>();

		// At every iteration of the outer loop, we add a path of length 1
		// between nodes that originally had a path of length 2. In the worst
		// case, we need to make floor(log |V|) + 1 iterations. We stop earlier
		// if there is no change to the output graph.

		int bound = computeBinaryLog(vertices.size());
		boolean done = false;
		for (int i = 0; !done && (i < bound); ++i) {
			done = true;
			for (Vertex v1 : vertices) {
				newEdgeTargets.clear();

				for (Edge v1OutEdge : v1.getEdges()) {
					Vertex v2 = v1OutEdge.getTarget();
					if (v2 != v1) {
						for (Edge v2OutEdge : v2.getEdges()) {
							Vertex v3 = v2OutEdge.getTarget();
							if (v3 != v2) {
								if (v1.equals(v3)) {
									// Its a simple graph, so no self loops.
									continue;
								}

								if (getEdge(v1, v3) != null) {
									// There is already an edge from v1 ---> v3,
									// skip;
									continue;
								}

								newEdgeTargets.add(v3);
								done = false;
							}
						}
					}
				}

				for (Vertex v3 : newEdgeTargets) {
					addEdge(v1, v3);
				}
			}
		}
	}

	public final void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}

	private final Edge getEdge(Vertex startVertex, Vertex endVertex) {
		for (Edge edge : startVertex.getEdges()) {
			if (edge.getTarget() == endVertex)
				return edge;
		}
		return null;

	}

	private final void addEdge(Vertex startVertex, Vertex endVertex) {
		Edge newEdge = new Edge(startVertex, endVertex);
		edges.add(newEdge);
		startVertex.addEdge(newEdge);
		endVertex.addEdge(newEdge);
		startVertex.addNeighbourVertex(endVertex);
		endVertex.addNeighbourVertex(startVertex);
	}

	/**
	 * Computes floor(log_2(n)) + 1
	 */
	private final int computeBinaryLog(int n) {
		assert n >= 0;
		int result = 0;
		while (n > 0) {
			n >>= 1;
			++result;
		}

		return result;
	}

	public final void addEdge(Edge edge) {
		if (getEdge(edge.getSource(), edge.getTarget()) == null) {
			edges.add(edge);
			edge.getSource().addEdge(edge);
			edge.getSource().addNeighbourVertex(edge.getTarget());
			edge.getTarget().addEdge(edge);
			edge.getTarget().addNeighbourVertex(edge.getSource());
		}
	}

}
