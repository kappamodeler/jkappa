package com.plectix.simulator.commandline;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.options.SimulatorFlagOption;
import com.plectix.simulator.simulator.options.SimulatorOption;
import com.plectix.simulator.simulator.options.SimulatorParameterizedOption;

public class TestCommandLineOptionsToBeSet {
	private final int setAllNumberParametersTo = 10;
	private final String setAllStringParametersTo = "parameter";
	
	@Test
	public void test() throws Exception {
		List<String> options = this.prepareListOfOptions();
		// if you want to check more cases, just reduce this variable
		long step = 1136416724988959L;
		long prev = 0;
		BitSet bitSet;
		for (long t = 1; t < Long.MAX_VALUE && prev < t; t += step) {
			bitSet = this.longToBitSet(t);
			Set<String> set = this.getSubset(options, bitSet);
			this.testCommandLine(normalize(set), set);
			prev = t;
		}

	}

	private void testCommandLine(String[] commandLine, Set<String> helpSet) throws Exception {
//		this.getResult(commandLine);
		try {
			SimulatorCommandLine simCommandLine = new SimulatorCommandLine(commandLine);
			SimulationArguments arguments = simCommandLine.getSimulationArguments();
			
			boolean fileNameWasSet = false;
			int parametersCounter = 0;
			for(String commandLineArgument : commandLine) {
				if (commandLineArgument.equals("filename") && !fileNameWasSet) {
					fileNameWasSet = true;
				} else if (commandLineArgument.equals(setAllStringParametersTo)
							|| commandLineArgument.equals(setAllNumberParametersTo + "")) {
					parametersCounter++;
				}
			}
			
			Assert.assertTrue("failed on " + helpSet, 
					arguments.allParametersAreDefaultOrEqualTo(
							setAllNumberParametersTo, setAllStringParametersTo, 
							parametersCounter,
							fileNameWasSet));
		} catch (ParseException e) {
			if (!e.getMessage().equals("two input files!")) {
				Assert.fail(e.getMessage());
			}
		}
	}

	private String[] normalize(Set<String> options) {
		ArrayList<String> list = new ArrayList<String>();
		int i = 0;
		for (String option : options) {
			String[] words = option.split(" ");
			list.add(words[0]);
			if (words.length > 1) {
				list.add(words[1]);
			}

			i++;
		}
		return list.toArray(new String[list.size()]);
	}

	private Set<SimulatorParameterizedOption> exceptions() {
		Set<SimulatorParameterizedOption> options = new HashSet<SimulatorParameterizedOption>();
		options.add(SimulatorParameterizedOption.COMPILE);
		options.add(SimulatorParameterizedOption.STORIFY);
		options.add(SimulatorParameterizedOption.SIMULATIONFILE);
		options.add(SimulatorParameterizedOption.QUALITATIVE_COMPRESSION);
		options.add(SimulatorParameterizedOption.QUANTITATIVE_COMPRESSION);
		options.add(SimulatorParameterizedOption.GENERATE_INFLUENCE_MAP);
		options.add(SimulatorParameterizedOption.CONTACT_MAP);
		options.add(SimulatorParameterizedOption.INPUT);
		return options;
	}
	
	private List<String> prepareListOfOptions() {
		List<String> list = new ArrayList<String>();
		for (SimulatorFlagOption flag : SimulatorFlagOption.values()) {
			list.add(optionToCommandLineArgument(flag));
		}
		for (SimulatorParameterizedOption parameter : SimulatorParameterizedOption
				.values()) {
			if (parameter.getParameterType().equals(String.class)) {
				if (this.exceptions().contains(parameter)) {
					list.add(optionToCommandLineArgument(parameter) + " filename");
				} else {
					list.add(optionToCommandLineArgument(parameter) + " " + setAllStringParametersTo);
				}
			} else {
				list.add(optionToCommandLineArgument(parameter) + " " + setAllNumberParametersTo);
			}
		}
		return list;
	}

	private String optionToCommandLineArgument(SimulatorOption option) {
		return "--" + option.getLongName().replace("_", "-");
	}

	private <E> Set<E> getSubset(List<E> source, BitSet bitSet) {
		Set<E> set = new HashSet<E>();
		for (int i = 0; i < source.size(); i++) {
			if (bitSet.get(i)) {
				set.add(source.get(i));
			}
		}
		return set;
	}

	private BitSet longToBitSet(long argument) {
		BitSet bitSet = new BitSet();
		char[] chars = Long.toString(argument, 2).toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '1') {
				bitSet.set(i);
			}
		}
		return bitSet;
	}
}
