package com.plectix.simulator.parser.abstractmodel.perturbations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.parser.util.StringUtil;

public class LinearExpression {
	private final List<LinearExpressionMonome> myRightHandSidePolynome = new ArrayList<LinearExpressionMonome>();

	public void addMonome(LinearExpressionMonome linearExpressionMonome) {
		myRightHandSidePolynome.add(linearExpressionMonome);
	}
	
	public List<LinearExpressionMonome> getPolynome() {
		return Collections.unmodifiableList(myRightHandSidePolynome);
	}
	
	//--------------toString----------------
	
	public String toString() {
		return StringUtil.listToString(myRightHandSidePolynome, " + ");
	}
}
