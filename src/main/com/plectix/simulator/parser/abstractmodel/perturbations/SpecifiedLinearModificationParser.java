package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.StringTokenizer;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.exceptions.ParseErrorMessage;

public abstract class SpecifiedLinearModificationParser {
	
	public LinearExpression parse(String line, KappaFileLine perturbationStr) 
						throws ParseErrorException {
		
		LinearExpression expressionRHS = new LinearExpression();
		
		if (line.indexOf("+") == -1) {
			LinearExpressionMonome theOnlyMonome = parseMonome(line, perturbationStr);
			expressionRHS.addMonome(theOnlyMonome);
		} else {
			StringTokenizer sTok = new StringTokenizer(line, "+");
			while (sTok.hasMoreTokens()) {
				String item = sTok.nextToken().trim();
				LinearExpressionMonome monome = parseMonome(item, perturbationStr);
				expressionRHS.addMonome(monome);
			}
		}

		return expressionRHS;
	}
	
	protected LinearExpressionMonome parseMonome(String arg,
			KappaFileLine perturbationStr) throws ParseErrorException {
		String item = arg;
		double coef = 0;
		item.replace("-", "+ -");
		if (item.indexOf("*") == -1) {
			try {
				// free term as double
				if(item.equals("$INF"))
					coef = Double.POSITIVE_INFINITY;
				else
					coef = Double.valueOf(item);
				return new LinearExpressionMonome(null, coef);
			} catch (NumberFormatException e) {
				// free term as rule name
				String name = parseName(item, perturbationStr);

				if (name != null) {
					return new LinearExpressionMonome(name, 1);
				} else {
					throw new ParseErrorException(perturbationStr,
						ParseErrorMessage.BAD_LINEAR_EXPRESSION);
				}
			}
		} else {
			double curValue = 0;
			try {
				curValue = Double.valueOf(item.substring(0, item.indexOf("*"))
						.trim());
			} catch (NumberFormatException e) {
				throw new ParseErrorException(perturbationStr,
						ParseErrorMessage.BAD_LINEAR_EXPRESSION);
			}
			item = item.substring(item.indexOf("*") + 1).trim();

			String curRuleName = parseName(item, perturbationStr);

			return new LinearExpressionMonome(curRuleName, curValue);
		}
	}
	
	protected abstract String parseName(String almostName, KappaFileLine line) throws ParseErrorException;
}
