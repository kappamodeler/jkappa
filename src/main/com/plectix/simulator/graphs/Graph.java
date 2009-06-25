package com.plectix.simulator.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Graph {
	private LinkedList<Edge> edges = null;
	private ArrayList<Vertex> vertices = null;
	public int numberOfVertices;

	public Graph(){
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
	public List<Edge> getAllEdgesInCycles() {
		clearTags();
		LinkedList<Edge> list = new LinkedList<Edge>();

		Stack<Vertex> stack = new Stack<Vertex>();
		vertices.get(0).parent = null;
		vertices.get(0).setTag(true);
		stack.push(vertices.get(0));

		boolean breaked = false;
		while (!stack.empty()) {
			breaked = false;
			Vertex temp = stack.pop();
			for (Edge e : temp.edges) {
				if (breaked)
					continue;
				if (e.getTarget() == temp) {
					if (e.getSource() == temp.parent) {
						continue;
					} else {
						e.setTarget(e.getSource());
						e.setSource(temp);
					}
				}
				if (e.getTarget().isTag()) {
					list.addAll(extractCycle(e.getTarget(), temp));
					breaked = true;
					continue;
				} else {
					e.getTarget().setTag(true);
					stack.push(e.getTarget());
				}
			}
		}
		return null;
	}

	private LinkedList<Edge> extractCycle(Vertex v, Vertex secondParent) {
		LinkedList<Vertex> list1 = new LinkedList<Vertex>();
		LinkedList<Vertex> list2 = new LinkedList<Vertex>();
		LinkedList<Edge> answer = new LinkedList<Edge>();
		list1.add(v);
		list1.add(secondParent);
		list2.add(v.parent);
		Vertex runner = secondParent;
		while (runner.parent != null) {
			runner = runner.parent;
			list1.add(runner);
		}
		runner = v.parent;
		while (runner.parent != null) {
			runner = runner.parent;
			list2.add(runner);
		}
		int size1 = list1.size() - 1;
		int size2 = list2.size() - 1;

		while (list1.get(size1) == list2.get(size2)) {
			list1.remove(size1);
			size1--;
			list2.remove(size2);
			size2--;
		}

		// megre and glue these vertices to one
		Vertex merged = list1.get(size1).parent;
		Edge e = getEdge(v, secondParent);
		answer.add(e);
		v.edges.remove(e);
		v.neighbors.remove(secondParent);
		secondParent.edges.remove(e);
		secondParent.neighbors.remove(v);
		Vertex vertexTwo = new Vertex();
		for (Vertex vertex : list1) {
			vertexTwo = vertex.parent;
			addEdgeToAnswer(answer, vertex, vertexTwo);
		}

		for (Vertex vertex : list2) {
			vertexTwo = vertex.parent;
			addEdgeToAnswer(answer, vertex, vertexTwo);
		}
		// redirect edges
		for (Vertex vertex : list1) {
			mergeVertices(merged, vertex);
		}

		for (Vertex vertex : list2) {
			mergeVertices(merged, vertex);
		}

		boolean needCompress = true;

		while (needCompress) {
			needCompress = false;
			for (int i = 0; i < merged.neighbors.size(); i++) {
				if (merged.neighbors.lastIndexOf(merged.neighbors.get(i)) != i) {
					vertexTwo = merged.neighbors.get(i);
					while (getEdge(merged, vertexTwo) != null) {
						addEdgeToAnswer(answer, merged, vertexTwo);
					}
					mergeVertices(merged, vertexTwo);
					needCompress = true;
				}
			}
		}

		return answer;
	}

	private void addEdgeToAnswer(LinkedList<Edge> answer, Vertex vertex,
			Vertex vertexTwo) {
		Edge e;
		e = getEdge(vertex, vertexTwo);
		answer.add(e);
		vertex.edges.remove(e);
		vertex.neighbors.remove(vertexTwo);
		vertexTwo.edges.remove(e);
		vertexTwo.neighbors.remove(vertex);
	}

	private void mergeVertices(Vertex merged, Vertex vertex) {
		for (Edge eTemp : vertex.edges) {
			eTemp.setSource(merged);
			merged.edges.add(eTemp);
			merged.neighbors.add(eTemp.getTarget());
			eTemp.getTarget().neighbors.remove(vertex);
			eTemp.getTarget().neighbors.add(merged);
		}
		vertex.edges.clear();
		vertex.neighbors.clear();
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
				for(Edge e : vertices.get(i).edges){
					if(e.getSource() == vertices.get(i)){
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
		
		for(int i = 0;i<numberOfVertices;i++){
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
	//change neighbors
	private void outDegree(Vertex v){
		for(Edge e : v.edges){
			if (e.getTarget()==v)
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


		Set<Vertex> newEdgeTargets = new HashSet<Vertex>();

		// At every iteration of the outer loop, we add a path of length 1
		// between nodes that originally had a path of length 2. In the worst
		// case, we need to make floor(log |V|) + 1 iterations. We stop earlier
		// if there is no change to the output graph.

		int bound = computeBinaryLog(numberOfVertices);
		boolean done = false;
		for (int i = 0; !done && (i < bound); ++i) {
			done = true;
			for (Vertex v1 :  vertices) {
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
			if (edge.getTarget() == v2 || edge.getSource() == v2)
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

	public void addEdge(Edge e){
		edges.add(e);
		e.getSource().edges.add(e);
		e.getSource().neighbors.add(e.getTarget());
		e.getTarget().edges.add(e);
		e.getTarget().neighbors.add(e.getSource());
	}
	
}
