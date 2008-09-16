package com.plectix.simulator;

import java.io.IOException;

import com.plectix.simulator.CommandLine.FlagDefinition;
import com.plectix.simulator.parser.Parser;

public class SimulationMain {

	public static void main(String[] args) {
		
//		CommandLine cmdLine = new CommandLine(args);
//		try {
//			cmdLine.parseCommand();
//		} catch (IOException e) {
//			System.err.println(e.getMessage());
//			return;
//		}
//		
//		DataReading data = new DataReading(cmdLine.MODE_SIM);// needs connection to file
		
		
		CommandLine cmdLine = new CommandLine();
		String sim=null;

		for (int i = 0; i < args.length; i++) {
			FlagDefinition flagDef=cmdLine.checkFlag(args[i]);
			
			if (flagDef==null){
				System.err.println("Error in command line");
				return;
			}
			
			if (args[i].equals("--sim")){
				//if (cmdLine.hasValue(flagDef))
					sim = args[++i];  
			}	
		}
		
		DataReading data = new DataReading(sim);// needs connection to file
		
		try{
		    data.ReadData();
		    SimulationData simData = new SimulationData();
		   	Parser parser = new Parser(data, simData);
		   	parser.doParse();
//		   	Simulator simulator = new Simulator(new Model(simData));
//		   	simulator.run();
//		   	simulator.outputData();
		}
		catch (IOException e){
			System.err.println(e.getMessage());
			System.err.println("Wrong input file.");
		}
		
//		catch parser exeptions
//		catch simulator exeptions
//		...

	}

}
