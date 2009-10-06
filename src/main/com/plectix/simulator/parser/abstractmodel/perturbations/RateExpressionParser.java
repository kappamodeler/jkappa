package com.plectix.simulator.parser.abstractmodel.perturbations;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.util.StringUtil;

public final class RateExpressionParser extends SpecifiedLinearModificationParser {
	@Override
	protected final String parseName(String lineToParse, KappaFileLine perturbationLine) throws ParseErrorException {
		StringUtil.checkString("'", lineToParse, perturbationLine);
		lineToParse = lineToParse.substring(lineToParse.indexOf("'") + 1).trim();
		return StringUtil.parseRuleName(lineToParse);
	}
}
