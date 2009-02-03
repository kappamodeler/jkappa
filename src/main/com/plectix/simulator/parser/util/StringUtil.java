package com.plectix.simulator.parser.util;

import java.util.List;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;

public class StringUtil {
	/**
	 * checks string st
	 * @param ch char, which we're searching for
	 * @param st string, where we're searching for ch
	 * @param perturbationStr
	 * @throws ParseErrorException
	 */
	public static void checkString(String ch, String st, KappaFileLine perturbationStr)
			throws ParseErrorException {
		int index = st.indexOf(ch);
		if (index == -1) {
			String quote = "'";
			if ("'".equals(ch)) {
				quote = "\"";
			}
			throw new ParseErrorException(perturbationStr, quote + ch + quote
					+ " expected : " + st + "");
		}
	}
	
	public final static String getName(String line) throws ParseErrorException {
		// Example: "abc']..."
		String name = null;
		int index = line.indexOf("'");
		if (index != -1) {
			name = line.substring(0, line.indexOf("'"));
		} else {
			throw new ParseErrorException("Rule name or number expected : "
					+ line);
		}
		return name;
	}
	
	public final static <E> String listToString(List<E> list) {
		return listToString(list, ", ");
	}
	
	public final static <E> String listToString(List<E> list, String separator) {
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
