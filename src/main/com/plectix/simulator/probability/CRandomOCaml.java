package com.plectix.simulator.probability;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.simulator.Simulator;

/*package*/ final class CRandomOCaml implements IRandom {

	private Process process;
	private Scanner scanner;
	private PrintWriter writer;

	public CRandomOCaml(String patch, int seed) {
		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec(patch);
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStream inputStream = process.getInputStream();
		scanner = new Scanner(inputStream);
		writer = new PrintWriter(process.getOutputStream());
		setSeed(seed);
	}

	public final double getDouble() {
		printToWriter("1");
		double randomNumber;
		try {
			randomNumber = Double.parseDouble(scanner.nextLine());
		} catch (NumberFormatException e) {
			randomNumber = 0.0;
		}
		return randomNumber;
	}

	public final int getInteger(int limit) {
		printToWriter("2");
		printToWriter(String.valueOf(limit));

		int randomNumber;
		try {
			randomNumber = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			randomNumber = 0;
		}
		return randomNumber;
	}

	private final void printToWriter(String str) {
		writer.print(str);
		writer.println();
		writer.flush();
	}

	private final void setSeed(int seed) {

		if (seed != 0) {
			printToWriter("0");
			printToWriter(String.valueOf(seed));
		} else {
			printToWriter("2");
			printToWriter("1073741823");

			int randomNumber;
			try {
				String str = scanner.next();
				randomNumber = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				randomNumber = 0;
			}
			printToWriter("0");
			printToWriter(String.valueOf(randomNumber));
		}
	}

	public final void disableRandom() {
		printToWriter("3");
	}

}
