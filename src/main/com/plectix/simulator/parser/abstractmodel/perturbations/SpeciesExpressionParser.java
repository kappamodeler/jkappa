package com.plectix.simulator.parser.abstractmodel.perturbations;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.util.StringUtil;

public class SpeciesExpressionParser extends SpecifiedLinearModificationParser {

	@Override
	protected String parseName(String almostName, KappaFileLine line) throws ParseErrorException {
		StringUtil.checkString("[", almostName, line);
		almostName = almostName.substring(almostName.indexOf("[") + 1).trim();
		StringUtil.checkString("'", almostName, line);
		almostName = almostName.substring(almostName.indexOf("'") + 1).trim();

		return StringUtil.getName(almostName);
	}

}
