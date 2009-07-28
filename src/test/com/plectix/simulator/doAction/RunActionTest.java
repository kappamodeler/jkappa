package com.plectix.simulator.doAction;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.OperationMode;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.util.CComponentComparator;
import com.plectix.simulator.util.Converter;

@RunWith(value = Parameterized.class)
public class RunActionTest extends InitTestAction {
	private static final String separator = File.separator;
	private static String FilePath = "test.data" + separator + "actions" + separator;
	private CRule activeRule;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(FilePath);
	}

	public RunActionTest(String fileName) {
		super(FilePath + fileName);
	}

	@Test
	public void test() {
		List<CInjection> injectionsList = run();
		activeRule = getActiveRule();
		List<IConnectedComponent> lhs = activeRule.getLeftHandSide();
		SimulationData simulationData = getSimulationData();
//		List<IConnectedComponent> firstsolution = simulationData.getKappaSystem()
//				.getSolution().split();
//
//		if (lhs == null){
//			if (firstsolution != null){
//				fail("uncorrect init");
//			}
//		} else {
//			if (firstsolution.size() != (2 * lhs.size()))
//				fail("uncorrect init");
//		}
//
//		compareWithSolution(lhs, firstsolution, "init");
//		compareWithSolution(lhs, firstsolution, "init");

		apply(injectionsList);
		if (simulationData.getSimulationArguments().getOperationMode() != OperationMode.FOURTH) {
			SimulationUtils.doNegativeUpdate(injectionsList);
			simulationData.getKappaSystem().doPositiveUpdate(activeRule, injectionsList);
		}
		simulationData.getKappaSystem().getSolution().applyChanges(activeRule.getPool());
		
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
			List<IConnectedComponent> solutionCC, String ruleName) {
		IConnectedComponent foundCC;
		if (listCC != null) {
			for (IConnectedComponent cc : listCC) {
				foundCC = null;
				if ((foundCC = CComponentComparator.findCC(cc, solutionCC)) == null) {
					fail("Not found connected component from " + ruleName + ":"
							+ Converter.toString(cc));
				} else{
					solutionCC.remove(foundCC);
				}
			}
		}
	}

}
