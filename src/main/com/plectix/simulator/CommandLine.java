package com.plectix.simulator;

import java.util.ArrayList;
import java.util.List;

public class CommandLine {

	public CommandLine() {
		flagDefinitions = new ArrayList<FlagDefinition>();
		// Mode
		flagDefinitions.add(new FlagDefinition("--sim",
				FlagType.FLAGTYPE_STRING));
		flagDefinitions.add(new FlagDefinition("--compile",
				FlagType.FLAGTYPE_STRING));
		flagDefinitions.add(new FlagDefinition("--storify",
				FlagType.FLAGTYPE_STRING));
		// Options for simulation and story mode
		flagDefinitions.add(new FlagDefinition("--time",
				FlagType.FLAGTYPE_DOUBLE));
	}

	private List<FlagDefinition> flagDefinitions;

	enum FlagType {
		FLAGTYPE_NOVALUE, FLAGTYPE_STRING, FLAGTYPE_DOUBLE, FLAGTYPE_LONG
	};

	class FlagDefinition {
		String inFlagName = null;

		FlagType inFlagType = FlagType.FLAGTYPE_NOVALUE;

		public FlagDefinition(String flagName, FlagType flagType) {
			inFlagName = flagName;
			inFlagType = flagType;
		}
	}

	public FlagDefinition checkFlag(String flagName) {
		for (FlagDefinition fDef : flagDefinitions) {
			if (flagName.equals(fDef.inFlagName)) {
				return fDef;
			}
		}
		return null;
	}
	
	public boolean hasValue(FlagDefinition flagDef) {
		if (flagDef.inFlagType.equals(FlagType.FLAGTYPE_NOVALUE)) {
				return false;
		}
		return true;
	}
	

}
