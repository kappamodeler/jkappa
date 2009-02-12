package com.plectix.simulator.doAction;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;

@RunWith(value = Parameterized.class)
public class TestAction {
	private static Simulator mySimulator;
	private final Logger LOGGER = Logger.getLogger(TestAction.class);
	private double currentTime = 0.;
	private IRule myActiveRule;
	private static int myRunQuant = 0;
	private static int myTestQuant = 70;
	private static boolean myFirstRun = true;
	private static SimulatorCommandLine commandLine;
	private static String FilePath = "";
	private final static String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	private ISolution csolution;

	private static Map<Long, IAgent> mySolutionAgentsStructure;

	@Parameters
	public static Collection<Object[]> regExValues() {
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		String prefixFileName = "test.data/actions/test";
		StringBuffer suffixFileName;
		for (int i = 0; i < myTestQuant; i++) {
			suffixFileName = new StringBuffer();
			if (i < 9) {
				suffixFileName.append("0");
			}
			suffixFileName.append(i + 1);
			parameters.add(new Object[] { (prefixFileName + suffixFileName) });
		}
		return Collections.unmodifiableList(parameters);
	}

	public TestAction(String testFilePath) {
		FilePath = testFilePath;
	}

	private static String[] prepareTestArgs(String filePath) {
		String arg1 = new String("--debug");
		String arg2 = new String("--sim");
		String arg3 = new String(filePath);
		String arg4 = new String("--no_save_all");

		String[] args = new String[4];
		args[0] = arg1;
		args[1] = arg2;
		args[2] = arg3;
		args[3] = arg4;
		return args;
	}

	public void reset(String filePath) {
		String[] testArgs = prepareTestArgs(filePath);
		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		mySimulator.getSimulationData().setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
		mySimulator.resetSimulation(InfoType.OUTPUT);
	}
	
	@BeforeClass
	public static void init() {
		if (myFirstRun)
			FilePath = "test.data/actions/test00";
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		mySimulator = new Simulator();
		
		String[] testArgs = prepareTestArgs(FilePath);

		SimulationData simulationData = mySimulator.getSimulationData();

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(testArgs);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		simulationData.setSimulationArguments(InfoType.OUTPUT,commandLine.getSimulationArguments());
		simulationData.readSimulatonFile(InfoType.OUTPUT);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);
		System.out.println(FilePath);
	}

	@Before
	public void setup() {
		myRunQuant++;
		mySolutionAgentsStructure = mySimulator.getSimulationData().getKappaSystem()
				.getSolution().getAgents();
		System.out.println();
		System.out.println("test" + (myRunQuant - 1));
		printSolution("before action", mySolutionAgentsStructure);
		run();
	}

	@After
	public void teardown() {
		if (myRunQuant != myTestQuant) {
			reset(FilePath);
		}
	}

	private void run() {
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				InfoType.OUTPUT,mySimulator.getSimulationData());
		myActiveRule = ruleProbabilityCalculation.getRandomRule();

		if (myActiveRule == null) {
			mySimulator.getSimulationData().setTimeLength(currentTime);
			System.out.println("there's no active rules");
			System.exit(0);
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Rule: " + myActiveRule.getName());

		List<IInjection> injectionsList = ruleProbabilityCalculation
				.getSomeInjectionList(myActiveRule);

		currentTime += ruleProbabilityCalculation.getTimeValue();

		if (!isClash(injectionsList)) {
			// negative update
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("negative update");

			myActiveRule.applyRule(injectionsList, mySimulator.getSimulationData());

		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Clash");
		}
	}

	private boolean isClash(List<IInjection> injections) {
		if (injections.size() == 2) {
			for (ISite siteCC1 : injections.get(0).getSiteList())
				for (ISite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}

	@Test
	public void test() {
		List<IConnectedComponent> rightCCList = myActiveRule.getRightHandSide();
		List<IConnectedComponent> leftCCList = myActiveRule.getLeftHandSide();

		csolution = mySimulator.getSimulationData().getKappaSystem().getSolution();

		List<IConnectedComponent> listSolutionCC = new ArrayList<IConnectedComponent>();
		listSolutionCC = csolution.split();
		List<IConnectedComponent> listToRemove = new ArrayList<IConnectedComponent>();

		IConnectedComponent foundCC;

		printSolution("after action", mySolutionAgentsStructure);

		if (rightCCList != null) {
			for (IConnectedComponent ccRight : rightCCList) {
				foundCC = null;
				foundCC = findCC(ccRight, listSolutionCC);
				if (foundCC == null) {
					System.out.println(" - not found connected component:");
					printCC(ccRight);
					fail();
				} else {
					listToRemove.add(foundCC);
					continue;
				}
			}
		}

		for (IConnectedComponent connectedComponent : listToRemove) {
			listSolutionCC.remove(connectedComponent);
		}
		listToRemove = null;
		listToRemove = new ArrayList<IConnectedComponent>();
		if (leftCCList != null) {
			for (IConnectedComponent ccLeft : leftCCList) {
				foundCC = null;
				foundCC = findCC(ccLeft, listSolutionCC);
				if (foundCC == null) {
					fail();
				} else {
					listToRemove.add(foundCC);
					continue;
				}
			}
		} else {
			if (listSolutionCC != null) {
				if (!listSolutionCC.isEmpty())
					fail();
			}
		}
		for (IConnectedComponent connectedComponent : listToRemove) {
			listSolutionCC.remove(connectedComponent);
		}
		if (!listSolutionCC.isEmpty())
			fail();

		mySolutionAgentsStructure = null;
	}

	private void printCC(IConnectedComponent connectedComponent) {
		List<IAgent> list = new ArrayList<IAgent>();
		list = connectedComponent.getAgents();
		for (IAgent agent : list) {
			System.out.print(agent.getName() + "(");
			Collection<ISite> sitelist = new ArrayList<ISite>();
			sitelist = agent.getSites();
			for (ISite site : sitelist) {
				System.out.print(site.getName());
				System.out.print("~");
				System.out.print(site.getInternalState().getNameId());
				if (site.getLinkIndex() != -1) {
					System.out.print("!");
					System.out.print(site.getLinkIndex());
				}
				System.out.print(" ");
			}
			System.out.print(")   ");
		}
		System.out.println();

	}

	private void printListAgents(List<IConnectedComponent> listCC, String string) {
		System.out.println(string);
		List<IAgent> listAgents = new ArrayList<IAgent>();
		for (IConnectedComponent connectedComponent : listCC) {
			listAgents = connectedComponent.getAgents();
			for (IAgent agent : listAgents) {
				System.out.print(agent.getName());
				Collection<ISite> sitelist = new ArrayList<ISite>();
				sitelist = agent.getSites();
				System.out.print("(");
				for (ISite site : sitelist) {
					System.out.print(site.getName());
					System.out.print("~");
					System.out.print(site.getInternalState().getNameId());
					System.out.print("!");
					System.out.print(site.getLinkIndex());
					System.out.print(" ");
				}
				System.out.println(") ");
			}
		}
	}

	private void printSolution(String name,
			Map<Long, IAgent> mySolutionAgentsStructure2) {
		System.out.print("solution   ");
		System.out.println(name);
		for (IAgent agent : mySolutionAgentsStructure2.values()) {
			System.out.print(agent.getName());
			Collection<ISite> sitelist = new ArrayList<ISite>();
			sitelist = agent.getSites();
			System.out.print("(");
			for (ISite site : sitelist) {
				System.out.print(site.getName());
				System.out.print("~");
				System.out.print(site.getInternalState().getNameId());
				if (site.getLinkIndex() != -1) {
					System.out.print("!");
					System.out.print(site.getLinkIndex());
				}
				System.out.print(" ");
			}
			System.out.println(") ");
		}
	}

	private IConnectedComponent findCC(IConnectedComponent cCRight,
			List<IConnectedComponent> listCC) {
		int size = cCRight.getAgents().size();
		for (IConnectedComponent cC : listCC) {
			if (cC.getAgents().size() == size) {
				if (compareCC(cCRight, cC))
					return cC;
			}

		}
		return null;
	}

	private boolean compareCC(IConnectedComponent cCRight,
			IConnectedComponent cc) {
		List<IAgent> listCC = new ArrayList<IAgent>();
		listCC = cCRight.getAgents();
		for (IAgent agent : listCC) {
			if (!findAgentInCC(agent, cc)) {
				return false;
			}
		}
		listCC = cc.getAgents();
		for (IAgent agent : listCC) {
			if (!findAgentInCC(agent, cCRight)) {

				return false;
			}
		}
		return true;
	}

	private boolean findAgentInCC(IAgent agentToFind, IConnectedComponent cc) {
		String name = agentToFind.getName();
		for (IAgent agent2 : cc.getAgents()) {
			if (name.equals(agent2.getName())) {
				if (checkSites(agentToFind, agent2)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkSites(IAgent agent1, IAgent agent2) {
		boolean flag = false;
		Collection<ISite> agent1Sites = new ArrayList<ISite>();
		Collection<ISite> agent2Sites = new ArrayList<ISite>();
		agent1Sites = agent1.getSites();
		agent2Sites = agent2.getSites();
		String sName;
		int sIntState;
		for (ISite site1 : agent1Sites) {
			flag = false;
			sName = site1.getName();
			sIntState = site1.getInternalState().getNameId();
			for (ISite site2 : agent2Sites) {
				if (sName.equals(site2.getName())
						&& (site2.getInternalState().getNameId() == sIntState)
						&& (site2.getLinkIndex() == site1.getLinkIndex())) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return flag;
	}

}
