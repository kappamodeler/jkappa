package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.parser.util.ParserUtil;

public final class LinearExpression {
	private final List<LinearExpressionMonome> monomes 
			= new ArrayList<LinearExpressionMonome>();

	public final void addMonome(LinearExpressionMonome linearExpressionMonome) {
		monomes.add(linearExpressionMonome);
	}
	
	public final List<LinearExpressionMonome> getPolynome() {
		return monomes;
	}
	
	@Override
	public final String toString() {
		return ParserUtil.listToString(monomes, " + ");
	}
}
