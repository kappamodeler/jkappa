package com.plectix.simulator.doAction;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.util.CComponentComparator;
import com.plectix.simulator.util.Converter;

@RunWith(value = Parameterized.class)
public class RunActionTest extends InitTestAction {
	private IRule activeRule;
	private static String FilePath = "test.data/actions/";

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(FilePath);
	}

	public RunActionTest(String fileName) {
		super(FilePath + fileName);
	}

	@Test
	public void test() {
		List<IInjection> injectionsList = run();
		activeRule = getActiveRule();
		List<IConnectedComponent> lhs = activeRule.getLeftHandSide();
		SimulationData simulationData = getSimulationData();
		List<IConnectedComponent> firstsolution = simulationData.getKappaSystem()
				.getSolution().split();

		if (lhs == null){
			if (firstsolution != null){
				fail("uncorrect init");
			}
		}else {
			if (firstsolution.size() != (2 * lhs.size()))
				fail("uncorrect init");
		}

		compareWithSolution(lhs, firstsolution, "init");
		compareWithSolution(lhs, firstsolution, "init");

		apply(injectionsList);
		activeRule = getActiveRule();
		List<IConnectedComponent> rhs = activeRule.getRightHandSide();
		simulationData = getSimulationData();
		
		
		List<IConnectedComponent> solution = simulationData.getKappaSystem()
				.getSolution().split();

		if (rhs == null) {
			if (lhs == null)
				fail("uncorrect rule");
			else {
				if (solution.size() != (lhs.size()))
					fail("uncorrect size of solution");
			}
		} else if (solution.size() != (rhs.size() + lhs.size()))
			fail("uncorrect size of solution");

		compareWithSolution(rhs, solution, "rhs");
		compareWithSolution(lhs, solution, "lhs");

	}

	private void compareWithSolution(List<IConnectedComponent> listCC,
			List<IConnectedComponent> solutionCC, String name) {
		IConnectedComponent foundCC;
		if (listCC != null) {
			for (IConnectedComponent cc : listCC) {
				foundCC = null;
				if ((foundCC = CComponentComparator.findCC(cc, solutionCC)) == null) {
					fail("Not found connected component from " + name + ":"
							+ Converter.toString(cc));
				} else{
					solutionCC.remove(foundCC);
				}
			}
		}
	}

}
