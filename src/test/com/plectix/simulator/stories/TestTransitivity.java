package com.plectix.simulator.stories;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.TreeMap;

public class TestTransitivity {
	private TreeMap<Integer,List<Integer>> trace; 
	private int[][] C;
	private int[][] B;
	
	
	
	
	public TestTransitivity(TreeMap<Integer,List<Integer>> traceIdToTraceId) {
		trace = traceIdToTraceId;
	}
	
	
	public void test(){
		buildMatrix();
		warshall();
		checkTransitivity();

	}
	
	private void warshall() {
		for (Integer k : trace.keySet()) {
			for (Integer i : trace.keySet()) {
				for (Integer j : trace.keySet()) {
					if (B[i][j] == 0)
						B[i][j] = B[i][k]*B[k][j];
				}
			}
		}
	}

	private void checkTransitivity() {
		for (Integer k : trace.keySet()) {
			for (Integer i : trace.keySet()) {
				for (Integer j : trace.keySet()) {
					if (C[i][j] == 1){
						if (B[i][k]*B[k][j] == 1)
							fail("the graph has a transitive relation");
					}
				}
			}
		}
	}

	private void buildMatrix() {
		int n = trace.lastKey();
		initMatrix(n+1);
		for (Integer key : trace.keySet()) {
			for (Integer w : trace.get(key)) {
				C[key][w] = 1;
				B[key][w] = 1;
			}
		}
	}
	
	private void initMatrix(int n) {
		C = new int[n][n];
		B = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				C[i][j] = 0;
				B[i][j] = 0;
			}
		}
	}
	
	private void printMatrix(int[][] m, String name) {
		System.out.println("matrix " + name);
		for (Integer i : trace.keySet()) {
			for (Integer j : trace.keySet()) {
				System.out.print(m[i][j] + "  ");
			}
			System.out.println();
		}
	}

}
