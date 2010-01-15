package com.plectix.simulator.parser.incomplete;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.builders.MasterSolutionModel;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LibraryOfSpecies;

@RunWith(value = Parameterized.class)
public class TestIncompleteSubstance {
	private final String agent1Str;
	private final String agent2Str;
	private final boolean result;
	
	public static LibraryOfSpecies libraryOfSpecies = new LibraryOfSpecies();
	private static final byte COUNT_TESTS = 8;
	
	private static final String[] AGENT_1 = {
		"A1(x1)", 
		"A2(x2)",
		"A3(x3,y3)",
		"A4(x4,y4)",
		"A5(x5,y5)",
		"A6(x6,y6)",
		"A7(x7)",
		"B8(y8)",
		"a(x)",
		"b(x)"
	};

	private static final String[] AGENT_2 = {
		"A1(x1)", 
		"A2()",
		"A3(x3,y3)",
		"A4(x4)",
		"A5(y5)",
		"A6(x6,y6,z6)",
		"A7(z7)",
		"C8(x8)",
		"a(x?)",
		"b(x!_)"
	};
	
	
	private static final boolean[] RESULTS_AGENTS = {
		true,
		false,
		true,
		false,
		false,
		false,
		false,
		true,
		false,
		false
	};
	
	@Parameters
	public static Collection<Object[]> configs() {
		List<Object[]> outList = new LinkedList<Object[]>();
		for (int i = 0; i < COUNT_TESTS; i++) {
			String agent1 = AGENT_1[i];
			String agent2 = AGENT_2[i];
			boolean result = RESULTS_AGENTS[i];
			outList.add(new Object[]{agent1, agent2, result});
		}
		
		return outList;
	}
	
	public TestIncompleteSubstance(String agent1,String agent2, boolean result) {
		this.agent1Str = agent1;
		this.agent2Str = agent2;
		this.result = result;
	}

	@Test
	public void isCorrect() throws ParseErrorException, DocumentFormatException {
		MasterSolutionModel model = new MasterSolutionModel();
		Agent agent1;
		agent1 = libraryOfSpecies.getAgentListByString(agent1Str).get(0);
		Agent agent2 = libraryOfSpecies.getAgentListByString(agent2Str).get(0);
		assertTrue(model.isCorrect(agent1));
		assertEquals(model.isCorrect(agent2), result);
	}

}
