package com.plectix.simulator.io;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.simulationclasses.action.Action;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.solution.SolutionLine;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.OutputUtils;
import com.plectix.simulator.util.Info.InfoType;

public class ConsoleOutputManager {
	public final String PROGRESS_BAR_SYMBOL = "#";
	
	private PrintStream printStream = null;
	private final SimulationData simulationData;
	private final List<Info> infoList = new ArrayList<Info>();
	
	public ConsoleOutputManager(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	public boolean initialized() {
		return printStream != null;
	}
	
	public final void print(String text) {
		if (this.initialized()) {
			printStream.print(text);
		}
	}

	public final void println() {
		if (this.initialized()) {
			printStream.println();
		}
	}

	public final void println(String text) {
		this.print(text);
		this.println();
	}
	
	public final void outputBar() {
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		if (simulationArguments.getOutputTypeForAdditionalInfo() != InfoType.DO_NOT_OUTPUT
				|| !simulationArguments.storiesModeIsOn())
			print(PROGRESS_BAR_SYMBOL);
	}

	public void setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
	}
	
	public final void outputData() {
		outputRules();
		outputPertubation();
		outputSolution();
	}

	private final void outputSolution() {
		println("INITIAL SOLUTION:");
		for (SolutionLine sl : (simulationData.getKappaSystem().getSolution()).getSolutionLines()) {
			print("-");
			print("" + sl.getNumber());
			print("*[");
			print(sl.getLine());
			println("]");
		}
	}

	private final void outputRules() {
		for (Rule rule : simulationData.getKappaSystem().getRules()) {
			// int countAgentsInLHS = rule.getCountAgentsLHS();
			// int indexNewAgent = countAgentsInLHS;

			for (Action action : rule.getActionList()) {
				switch (action.getType()) {
				case BREAK: {
					Site siteTo = ((Site) action.getSourceSite().getLinkState()
							.getConnectedSite());
					if (action.getSourceSite().getParentAgent()
							.getIdInRuleHandside() < siteTo.getParentAgent()
							.getIdInRuleHandside()) {
						// BRK (#0,a) (#1,x)
						print("BRK (#");
						print(""
								+ (action.getSourceSite().getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(action.getSourceSite().getName());
						print(") ");
						print("(#");
						print(""
								+ (siteTo.getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(siteTo.getName());
						println(") ");
					}
					break;
				}
				case DELETE: {
					// DEL #0
					print("DEL #");
					println(""
							+ (action.getSourceAgent().getIdInRuleHandside() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					print("ADD " + action.getTargetAgent().getName() + "#");

					print("" + (action.getTargetAgent().getIdInRuleHandside() - 1));
					print("(");
					int i = 1;
					for (Site site : action.getTargetAgent().getSites()) {
						print(site.getName());
						if ((site.getInternalState() != null)
								&& (!site.getInternalState().hasDefaultName()))
							print("~" + site.getInternalState().getName());
						if (action.getTargetAgent().getSites().size() > i++)
							print(",");
					}
					println(") ");

					break;
				}
				case BOUND: {
					// BND (#1,x) (#0,a)
					Site siteTo = ((Site) action.getSourceSite().getLinkState()
							.getConnectedSite());
					if (action.getSourceSite().getParentAgent()
							.getIdInRuleHandside() > siteTo.getParentAgent()
							.getIdInRuleHandside()) {
						print("BND (#");
						print(""
								+ (action.getSourceSite().getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(action.getSourceSite().getName());
						print(") ");
						print("(#");
						print(""
								+ (action.getTargetSite().getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(siteTo.getName());
						println(") ");
					}
					break;
				}
				case MODIFY: {
					// MOD (#1,x) with p
					print("MOD (#");
					print(""
							+ (action.getSourceSite().getParentAgent()
									.getIdInRuleHandside() - 1));
					print(",");
					print(action.getSourceSite().getName());
					print(") with ");
					println(action.getTargetSite().getInternalState().getName());
					break;
				}
				}

			}

			StringBuffer sb = new StringBuffer();
			boolean ocamlStyle = simulationData.getSimulationArguments().isOcamlStyleNameingInUse();
			sb.append(OutputUtils.printPartRule(rule.getLeftHandSide(), ocamlStyle));
			sb.append("->");
			sb.append(OutputUtils.printPartRule(rule.getRightHandSide(), ocamlStyle));
			StringBuffer ch = new StringBuffer();
			for (int j = 0; j < sb.length(); j++)
				ch.append("-");

			println(ch.toString());
			if (rule.getName() != null) {
				print(rule.getName());
				print(": ");
			}
			print(sb.toString());
			println();
			println(ch.toString());
			println();
			println();
		}
	}

	private final void outputPertubation() {
		println("PERTURBATIONS:");
		KappaSystem kappaSystem = simulationData.getKappaSystem();

		for (ComplexPerturbation<?, ?> perturbation : kappaSystem.getPerturbations()) {
			println(OutputUtils.perturbationToString(perturbation, kappaSystem));
		}
	}

	public PrintStream getPrintStream() {
		return printStream;
	}
	public final void addAdditionalInfo(InfoType type, String message) {
		InfoType outputType = simulationData.getSimulationArguments().getOutputTypeForAdditionalInfo();
		this.addInfo(outputType, type, message);
	}

	public List<Info> getInfo() {
		return infoList;
	}

	private void addInfo(InfoType outputType, InfoType type, String message) {
		simulationData.addInfo(new Info(outputType, type, message, printStream));
	}
}
