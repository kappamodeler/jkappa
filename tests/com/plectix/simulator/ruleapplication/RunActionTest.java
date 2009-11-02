package com.plectix.simulator.ruleapplication;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.util.CComponentComparator;
import com.plectix.simulator.util.Converter;

@RunWith(value = Parameterized.class)
public class RunActionTest extends InitTestAction {
	private static final String separator = File.separator;
	private static String FilePath = "test.data" + separator + "actions"
			+ separator;
	private Rule activeRule;
	private Integer opMode;

	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator.generate(getAllTestFileNames(FilePath));
	}

	public RunActionTest(String fileName, Integer opMode) {
		super(FilePath + fileName, opMode);
		this.opMode = opMode;
	}

	@Test
	public void test() throws StoryStorageException {
		List<Injection> injectionsList = run();
		activeRule = getActiveRule();
		List<ConnectedComponentInterface> lhs = activeRule.getLeftHandSide();
		SimulationData simulationData = getSimulationData();

		apply(injectionsList);
		if (simulationData.getSimulationArguments().getOperationMode() != OperationMode.FOURTH) {
			SimulationUtils.doNegativeUpdate(injectionsList);
			simulationData.getKappaSystem().doPositiveUpdate(activeRule,
					injectionsList);
		}
		simulationData.getKappaSystem().getSolution().flushPoolContent(
				activeRule.getPool());

		List<ConnectedComponentInterface> rhs = activeRule.getRightHandSide();
		simulationData = getSimulationData();

		Collection<ConnectedComponentInterface> solution = simulationData
				.getKappaSystem().getSolution().split();

		if (rhs == null) {
			if (lhs == null)
				fail("operation mode: " + opMode + "\nuncorrect rule");
			else {
				if (solution.size() != (lhs.size()))
					fail("operation mode: " + opMode + "\nuncorrect size of solution");
			}
		} else if (solution.size() != (rhs.size() + lhs.size()))
			fail("operation mode: " + opMode + "\nuncorrect size of solution");

		compareWithSolution(rhs, solution, "rhs");
		compareWithSolution(lhs, solution, "lhs");

	}

	private void compareWithSolution(List<ConnectedComponentInterface> listCC,
			Collection<ConnectedComponentInterface> solutionCC, String ruleName) {
		ConnectedComponentInterface foundCC;
		if (listCC != null) {
			for (ConnectedComponentInterface cc : listCC) {
				foundCC = null;
				if ((foundCC = CComponentComparator.findComponent(cc, solutionCC)) == null) {
					fail("operation mode: " + opMode + "\nNot found connected component from " + ruleName + ":"
							+ Converter.toString(cc));
				} else {
					solutionCC.remove(foundCC);
				}
			}
		}
	}

}
