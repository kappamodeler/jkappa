package com.plectix.simulator.stories.weakCompression;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.RunAllTests;

@RunWith(value = Parameterized.class)
public class RunTestWeakWithSeed {
	

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (int i = 0; i < 100; i++) {
			Object[] obj = new Object[1];
			obj[0] = i+1;
			data.add(obj);
		}
		return data;
	}

	public RunTestWeakWithSeed() {
	}


}
