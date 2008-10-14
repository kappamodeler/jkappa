package com.plectix.simulator.components;

import java.util.Random;

import com.plectix.simulator.interfaces.IRandom;

public class CRandomJava implements IRandom {

	private Random rand;

	public CRandomJava(int seed) {
		if (seed != 0)
			rand = new Random(seed);
		else
			rand = new Random();
	}

	@Override
	public double getDouble() {
		return rand.nextDouble();
	}

	@Override
	public int getInteger(int limit) {
		return rand.nextInt(limit);
	}

}
