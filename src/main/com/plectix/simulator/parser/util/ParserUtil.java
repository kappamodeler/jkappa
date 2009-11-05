package com.plectix.simulator.parser.util;

import java.util.List;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;

public final class ParserUtil {
	public static final void checkString(String stringToFind, String line, KappaFileLine kappaFileLine)
			throws ParseErrorException {
		int index = line.indexOf(stringToFind);
		if (index == -1) {
			String quote = "'";
			if ("'".equals(stringToFind)) {
				quote = "\"";
			}
			throw new ParseErrorException(kappaFileLine, quote + stringToFind + quote
					+ " expected : " + line + "");
		}
	}
	
	public static final String parseRuleName(String line) throws ParseErrorException {
		// Example: "abc']..."
		String name = null;
		int index = line.indexOf("'");
		if (index != -1) {
			name = line.substring(0, line.indexOf("'"));
		} else {
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_RULE_NAME);
		}
		return name;
	}
	
	public static final <E> String listToString(List<E> list) {
		return listToString(list, ", ");
	}
	
	public static final <E> String listToString(List<E> list, String separator) {
		StringBuffer sb = new StringBuffer();
		
		boolean first = true;
		for (E element : list) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(element);
		}
		
		return sb.toString();
	}
}
