package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.StringTokenizer;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;

public abstract class SpecifiedLinearModificationParser {
	public final LinearExpression parse(String line, KappaFileLine perturbationLine) 
						throws ParseErrorException {
		LinearExpression expressionRHS = new LinearExpression();
		if (line.indexOf("+") == -1) {
			LinearExpressionMonome theOnlyMonome = parseMonome(line, perturbationLine);
			expressionRHS.addMonome(theOnlyMonome);
		} else {
			StringTokenizer sTok = new StringTokenizer(line, "+");
			while (sTok.hasMoreTokens()) {
				String item = sTok.nextToken().trim();
				LinearExpressionMonome monome = parseMonome(item, perturbationLine);
				expressionRHS.addMonome(monome);
			}
		}
		return expressionRHS;
	}
	
	private final LinearExpressionMonome parseMonome(String line,
			KappaFileLine perturbationLine) throws ParseErrorException {
		String item = line;
		double coefficient = 0;
		item.replace("-", "+ -");
		if (item.indexOf("*") == -1) {
			try {
				// free term as double
				if(item.equals("$INF"))
					coefficient = Double.POSITIVE_INFINITY;
				else
					coefficient = Double.valueOf(item);
				return new LinearExpressionMonome(null, coefficient);
			} catch (NumberFormatException e) {
				// free term as rule name
				String name = parseName(item, perturbationLine);

				if (name != null) {
					return new LinearExpressionMonome(name, 1);
				} else {
					throw new ParseErrorException(perturbationLine,
						ParseErrorMessage.BAD_LINEAR_EXPRESSION);
				}
			}
		} else {
			double curValue = 0;
			try {
				curValue = Double.valueOf(item.substring(0, item.indexOf("*"))
						.trim());
			} catch (NumberFormatException e) {
				throw new ParseErrorException(perturbationLine,
						ParseErrorMessage.BAD_LINEAR_EXPRESSION);
			}
			item = item.substring(item.indexOf("*") + 1).trim();

			String curRuleName = parseName(item, perturbationLine);

			return new LinearExpressionMonome(curRuleName, curValue);
		}
	}
	
	protected abstract String parseName(String almostName, KappaFileLine line) throws ParseErrorException;
}
