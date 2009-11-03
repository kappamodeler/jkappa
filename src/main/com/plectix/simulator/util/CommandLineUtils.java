package com.plectix.simulator.util;

public class CommandLineUtils {
	public static final String[] normalize(String[] commandLineArguments) {
		String[] argsNew = new String[commandLineArguments.length];
		int i = 0;
		for (String st : commandLineArguments)
			if (st.startsWith("-"))
				argsNew[i++] = st.substring(0, 2)
						+ st.substring(2).replaceAll("-", "_");
			else
				argsNew[i++] = st;
		return argsNew;
	}
	
	public static final String getCommandLineString(String[] args) {
		if (args.length == 0) {
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			stringBuffer.append(args[i] + " ");
		}
		stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		return stringBuffer.toString();
	}
}
