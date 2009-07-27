package com.plectix.simulator.graphs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Graph {
	private LinkedList<Edge> edges = null;
	private ArrayList<Vertex> vertices = null;
	public int numberOfVertices;

	public Graph() {
		edges = new LinkedList<Edge>();
		vertices = new ArrayList<Vertex>();
		numberOfVertices = 0;

	}

	public void setEdges(LinkedList<Edge> edges) {
		this.edges = edges;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
		numberOfVertices = vertices.size();
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Notice! Source and target of edge will be changed
	 * 
	 * @return
	 */
	public List<Edge> getAllEdgesInDirectedCycles() {
		clearTags();
		LinkedList<Edge> list = new LinkedList<Edge>();

		Stack<Vertex> stack = new Stack<Vertex>();
		vertices.get(0).parent = null;
		vertices.get(0).setTag(true);
		vertices.get(0).mark2 = true;
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
					v.mark2 = true;
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
	private void doStep(Stack<Vertex> stack, LinkedList<Edge> list) {
		Vertex from = stack.peek();
		Vertex to = from.next();
		if (to == from) {
			addEdgeToAnswer(list, from, to);
			return;
		}
		if (to != null) {
			if (!to.isTag()) {
				to.parent = from;
				to.mark2 = true;
				to.setTag(true);
				stack.push(to);
			} else {
				extractCycle(from, to, list, stack);
			}
		} else {
			stack.pop().setTag(false);
		}

	}

	private int needExploreMore() {
		for (int i = 0; i < numberOfVertices; i++) {
			if (!vertices.get(i).mark2) {
				return i;
			}
		}
		return -1;
	}

	// redraw all egdes from vertices of cycle form one Big vertex
	private void extractCycle(Vertex from, Vertex to, LinkedList<Edge> list,
			Stack<Vertex> stack) {
		addEdgeToAnswer(list, from, to);
		while (from != to) {
			addEdgeToAnswer(list, from.parent, from);
			mergeVertices(to, from);
			from = from.parent;
			stack.pop();
		}

	}

	private void addEdgeToAnswer(LinkedList<Edge> answer, Vertex from, Vertex to) {
		Edge e = getEdge(from, to);
		answer.add(e);
		from.edges.remove(e);
		to.edges.remove(e);
		to.explored=0;
		from.explored = 0;
		edges.remove(e);
	}

	private void mergeVertices(Vertex merged, Vertex vertex) {
		for (Edge eTemp : vertex.edges) {
			if (eTemp.getTarget() == vertex) {
				eTemp.setTarget(merged);
				merged.edges.add(eTemp);
			}
			if (eTemp.getSource() == vertex) {
				eTemp.setSource(merged);
				merged.edges.add(eTemp);
			}
		}
		vertex.edges.clear();
	}

	public ArrayList<ArrayList<Vertex>> getAllWeakClosureComponent() {

		closeGraph();
		sortVerticesByOutDegree();
		clearTags();

		ArrayList<ArrayList<Vertex>> answer = new ArrayList<ArrayList<Vertex>>();
		int i = 0;

		while (i < numberOfVertices) {
			if (vertices.get(i).isTag()) {
				i++;
			} else {
				ArrayList<Vertex> newSet = new ArrayList<Vertex>();
				vertices.get(i).setTag(true);
				newSet.add(vertices.get(i));
				for (Edge e : vertices.get(i).edges) {
					if (e.getSource() == vertices.get(i)) {
						newSet.add(e.getTarget());
						e.getTarget().setTag(true);
					}
				}
				answer.add(newSet);
			}

		}

		return answer;
	}

	private void sortVerticesByOutDegree() {

		for (int i = 0; i < numberOfVertices; i++) {
			outDegree(vertices.get(i));
		}
		// may be optimize
		Vertex temp = new Vertex();
		for (int i = 0; i < numberOfVertices - 1; i++) {
			for (int j = i + 1; j < numberOfVertices; j++) {
				if (vertices.get(i).neighbors.size() < vertices.get(j).neighbors
						.size()) {
					temp = vertices.get(i);
					vertices.set(i, vertices.get(j));
					vertices.set(j, temp);
				}
			}
		}

	}

	// change neighbors
	private void outDegree(Vertex v) {
		for (Edge e : v.edges) {
			if (e.getTarget() == v)
				v.neighbors.remove(e.getSource());
		}

	}

	private void clearTags() {
		for (int i = 0; i < numberOfVertices; i++) {
			vertices.get(i).setTag(false);
		}
	}

	/**
	 * Computes the transitive closure of the given graph.
	 * 
	 * @param graph
	 *            - Graph to compute transitive closure for.
	 */
	public void closeGraph() {

		Set<Vertex> newEdgeTargets = new LinkedHashSet<Vertex>();

		// At every iteration of the outer loop, we add a path of length 1
		// between nodes that originally had a path of length 2. In the worst
		// case, we need to make floor(log |V|) + 1 iterations. We stop earlier
		// if there is no change to the output graph.

		int bound = computeBinaryLog(numberOfVertices);
		boolean done = false;
		for (int i = 0; !done && (i < bound); ++i) {
			done = true;
			for (Vertex v1 : vertices) {
				newEdgeTargets.clear();

				for (Edge v1OutEdge : v1.edges) {
					Vertex v2 = v1OutEdge.getTarget();
					if (v2 != v1) {
						for (Edge v2OutEdge : v2.edges) {
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

	public void addVertex(Vertex v) {
		vertices.add(v);
		numberOfVertices++;

	}

	private Edge getEdge(Vertex v1, Vertex v2) {

		for (Edge edge : v1.edges) {
			if (edge.getTarget() == v2)
				return edge;
		}
		return null;

	}

	private void addEdge(Vertex v1, Vertex v2) {
		Edge newEdge = new Edge(v1, v2);
		edges.add(newEdge);
		v1.edges.add(newEdge);
		v2.edges.add(newEdge);
		v1.neighbors.add(v2);
		v2.neighbors.add(v1);

	}

	/**
	 * Computes floor(log_2(n)) + 1
	 */
	private int computeBinaryLog(int n) {
		assert n >= 0;

		int result = 0;
		while (n > 0) {
			n >>= 1;
			++result;
		}

		return result;
	}

	public void addEdge(Edge e) {
		if (getEdge(e.getSource(), e.getTarget()) == null) {

			edges.add(e);
			e.getSource().edges.add(e);
			e.getSource().neighbors.add(e.getTarget());
			e.getTarget().edges.add(e);
			e.getTarget().neighbors.add(e.getSource());
		}
	}

}
