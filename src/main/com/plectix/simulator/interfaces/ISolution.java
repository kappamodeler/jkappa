package com.plectix.simulator.interfaces;

import java.util.*;

import com.plectix.simulator.components.solution.SolutionLines;
import com.plectix.simulator.simulator.KappaSystem;

public interface ISolution {

	public Collection<IAgent> getAgents();
	
	public List<IConnectedComponent> split();
	
	public void addConnectedComponent(IConnectedComponent component);

	public void removeAgent(IAgent agent);

	public void addAgent(IAgent agent);

	public void clearAgents();

	public void clearSolutionLines();

	public void addConnectedComponents(List<IConnectedComponent> list);
	
	public void checkSolutionLinesAndAdd(String line, long count);

	public List<SolutionLines> getSolutionLines();
}
