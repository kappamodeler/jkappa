package com.plectix.simulator.parser.abstractmodel.perturbations;

public final class LinearExpressionMonome {
	// this String can be null, if we've got free coefficient
	/**
	 * This one can be rule name as well as observable name
	 */
	private final String entityName;
	private final double coefficient;
	
	public LinearExpressionMonome(String entityName, double coefficient) {
		this.coefficient = coefficient;
		this.entityName = entityName;
	}
	
	public final String getEntityName() {
		return entityName;
	}
	
	public final double getMultiplier() {
		return coefficient;
	}
	
	@Override
	public final String toString() {
		if (entityName == null) {
			return coefficient + "";
		}
		return coefficient + " * '" + entityName + "'";
	}
	                          
}
