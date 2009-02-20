package com.plectix.simulator.components.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;

/*package*/ abstract class PhysicalSolution implements ISolution {
	private List<SolutionLines> solutionLines = new ArrayList<SolutionLines>();
	
	public final void addConnectedComponents(List<IConnectedComponent> list) {
		if (list == null)
			return;
		for (IConnectedComponent component : list) {
			this.addConnectedComponent(component);
		}
	}
	
	//TODO REMOVE
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLines sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		solutionLines.add(new SolutionLines(line, count));
	}
	
	public final List<SolutionLines> getSolutionLines() {
		return Collections.unmodifiableList(solutionLines);
	}
	
	public final void clearSolutionLines() {
		solutionLines.clear();
	}
}
