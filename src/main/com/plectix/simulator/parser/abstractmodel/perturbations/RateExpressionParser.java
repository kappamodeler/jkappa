package com.plectix.simulator.parser.abstractmodel.perturbations;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.StringUtil;

public class RateExpressionParser extends SpecifiedLinearModificationParser {

	@Override
	protected String parseName(String almostName, KappaFileLine perturbationStr) throws ParseErrorException {
		StringUtil.checkString("'", almostName, perturbationStr);
		almostName = almostName.substring(almostName.indexOf("'") + 1).trim();
		return StringUtil.getName(almostName);
	}
}
