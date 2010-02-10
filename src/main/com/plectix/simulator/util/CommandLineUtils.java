package com.plectix.simulator.util;

import java.util.ArrayList;

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
		
		return processTokens(argsNew);
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
	
	private static final String[] processTokens(String[] commandLineArguments) {
		ArrayList<String> result = new ArrayList<String>();
		
		StringBuffer currentToken = new StringBuffer();
		boolean currentTokenIsComplex = false;
		for (String token2 : commandLineArguments) {
			String token = token2.trim();
			
			if (token.startsWith("\"")
				&& !currentTokenIsComplex) {
					currentTokenIsComplex = true;
			}
			
			currentToken.append(token + " ");						
			
			if (token.endsWith("\"") 
				&& currentTokenIsComplex) {
					currentTokenIsComplex = false;
			}
			
			if (!currentTokenIsComplex) {
				result.add(currentToken.toString().trim());
				currentToken = new StringBuffer();
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
}
