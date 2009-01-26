package com.plectix.simulator.parser.util;

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
}
