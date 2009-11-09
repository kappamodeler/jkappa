package com.plectix.simulator.simulationclasses.perturbations.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinearExpression<T extends Vector> {
	private final Map<T, Monome<T>> monomes = new LinkedHashMap<T, Monome<T>>();
	
	public void addMonome(T something, double coefficient) {
		if (something == null) {
			this.addMonome(coefficient);
			return;
		}
		Monome<T> previousMonome = monomes.get(something); 
		if (previousMonome == null) {
			monomes.put(something, new Monome<T>(something, coefficient));
		} else {
			previousMonome.setCoefficient(previousMonome.getCoefficient() + coefficient);
		}
	}
	
	public void addMonome(double coefficient) {
		Monome<T> previousMonome = monomes.get(null); 
		if (previousMonome == null) {
			monomes.put(null, new Monome<T>(null, coefficient));
		} else {
			previousMonome.setCoefficient(previousMonome.getCoefficient() + coefficient);
		}
	}
	
	public double calculate() {
		double sum = 0;
		for (Monome<T> monome : monomes.values()) {
			sum += monome.getMultiplication();
		}
		return sum;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String plus = "";
		for (Monome<T> monome : monomes.values()) {
			sb.append(plus + monome);
			plus = " + ";
		}
		return sb.toString();
	}

}
