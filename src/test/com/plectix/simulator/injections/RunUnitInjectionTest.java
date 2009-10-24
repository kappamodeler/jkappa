package com.plectix.simulator.injections;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.builders.SubstanceBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulationclasses.solution.SolutionUtils;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;

public class RunUnitInjectionTest {
	/**
	 * ENG - 275
	 */
	private static String OBS = "A(A1!1,A2!3), B(B2!2,B1!3), C(C2!1,C1!2)";

	private static String[] CC_LIST = {
//			"B(B1!0,B2),C(C1,C2!1),A(A1!1,A2!0)",
//			"C(C1,C2)",
//			"A(A1!0,A2),C(C1,C2!0)",
//			"B(B1,B2!0),C(C1!0,C2)",
//			"C(C1,C2!0),C(C1!1,C2),A(A1!0,A2!2),B(B1!2,B2!1)",
//			"A(A1,A2!0),B(B1!1,B2),A(A1!2,A2!1),A(A1!3,A2!4),B(B1!0,B2!5),B(B1!4,B2!6),C(C1!6,C2!2),C(C1!5,C2!3)",
//			"A(A1,A2!0),C(C1!1,C2),B(B1!0,B2!1)",
//			"A(A1!0,A2),B(B1,B2!1),C(C1!1,C2!0)",
			"B(B1,B2!0),C(C1!1,C2),A(A1!2,A2!3),B(B1!3,B2!1),C(C1!0,C2!2)"
//			"B(B1,B2!0),B(B1!1,B2),A(A1!2,A2!1),C(C1!0,C2!2)",
//			"A(A1,A2!0),B(B1!1,B2),A(A1!2,A2!1),B(B1!0,B2!3),C(C1!3,C2!2)",
//			"B(B1,B2!0),B(B1!1,B2),A(A1!2,A2!1),A(A1!3,A2!4),B(B1!4,B2!5),C(C1!0,C2!3),C(C1!5,C2!2)",
//			"B(B1!0,B2),C(C1,C2!1),A(A1!1,A2!2),A(A1!3,A2!0),B(B1!2,B2!4),C(C1!4,C2!3)",
//			"A(A1!0,A2),B(B1,B2!1),A(A1!2,A2!3),B(B1!3,B2!4),C(C1!1,C2!2),C(C1!4,C2!0)",
//			"A(A1!0,A2),C(C1,C2!1),A(A1!1,A2!2),B(B1!2,B2!3),C(C1!3,C2!0)",
//			"B(B1,B2!0),C(C1!1,C2),A(A1!2,A2!3),B(B1!3,B2!1),C(C1!0,C2!2)",
//			"A(A1,A2!0),B(B1!0,B2)", "B(B1,B2)", "A(A1,A2)" 
			};

	@Test
	public void runTest() {
		AgentFactory af = new AgentFactory(false);
		SimulationData simulationData = new SimulationData();
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		SubstanceBuilder substanceBuilder = new SubstanceBuilder(kappaSystem);
		try {
			List<ModelAgent> obsModelAgents = af.parseAgent(OBS);
			List<Agent> obsList = substanceBuilder.buildAgents(obsModelAgents);
			ConnectedComponentInterface obsCC = SolutionUtils.getConnectedComponent(obsList.get(0));//new ConnectedComponent(obsList);
			obsCC.initSpanningTreeMap();
			for (String cc : CC_LIST) {
				List<ModelAgent> ccModelAgents = af.parseAgent(cc);
				List<Agent> ccList = substanceBuilder.buildAgents(ccModelAgents);

				ConnectedComponentInterface ccCC = SolutionUtils.getConnectedComponent(ccList.get(0));//new ConnectedComponent(ccList);
				ccCC.initSpanningTreeMap();
				List<ConnectedComponentInterface> list = new LinkedList<ConnectedComponentInterface>();
				list.add(ccCC);
				for(Agent a : ccCC.getAgents())
					ccCC.addAgentFromSolutionForRHS(a);
				
				obsCC.doPositiveUpdate(list);
				assertEquals(obsCC.getInjectionsList().size(), 0);
			}

		} catch (IncompletesDisabledException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (DocumentFormatException e) {
			e.printStackTrace();
		}
	}

}
