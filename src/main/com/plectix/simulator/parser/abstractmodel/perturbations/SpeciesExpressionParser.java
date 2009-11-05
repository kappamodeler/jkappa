package com.plectix.simulator.parser.abstractmodel.perturbations;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.util.ParserUtil;

public final class SpeciesExpressionParser extends SpecifiedLinearModificationParser {
	@Override
	protected final String parseName(String lineToParse, KappaFileLine perturbationLine) throws ParseErrorException {
		ParserUtil.checkString("[", lineToParse, perturbationLine);
		lineToParse = lineToParse.substring(lineToParse.indexOf("[") + 1).trim();
		ParserUtil.checkString("'", lineToParse, perturbationLine);
		lineToParse = lineToParse.substring(lineToParse.indexOf("'") + 1).trim();
		return ParserUtil.parseRuleName(lineToParse);
	}
}
