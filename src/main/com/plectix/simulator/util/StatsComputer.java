package com.plectix.simulator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsComputer {

	// public static final String FILE = "results/degradation-deg-all/simplx-{0}-curves";
	// public static final String FILE = "results/KPT_study/simplx-{0}-curves";
	private static final String FILE = "results/degradation-deg-free/simplx-{0}-curves";

	private static final int NUMBER_OF_FILES = 50;

	private static int number_of_observables = -1;
	private static List<Double> timeStamps = null;
	private static List<ArrayList<RunningMetric>> runningMetrics = null;

	private final static void initIterations(int observable_num) {
		if (number_of_observables > 0) {
			return;
		}
		number_of_observables = observable_num;
		timeStamps = new ArrayList<Double>();
		runningMetrics = new ArrayList<ArrayList<RunningMetric>>();
		for (int i = 0; i < number_of_observables; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}
	}



	private final static void createTMPReport() {
		try {
			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				BufferedWriter writer = new BufferedWriter(new FileWriter("simplx-stats-" + observable_num));
				for (int timeStepCounter = 0; timeStepCounter < timeStamps
						.size(); timeStepCounter++) {
					String st = timeStamps.get(timeStepCounter)
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMin()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMax()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getMean()
							+ " "
							+ runningMetrics.get(observable_num).get(
									timeStepCounter).getStd();

					writer.write(st);
					writer.newLine();
				}

				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) throws IOException {
		for (int fileCounter= 0; fileCounter < NUMBER_OF_FILES;  fileCounter++) {
			String filename = MessageFormat.format(FILE, String.format("%03d", fileCounter));
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			int lineCounter = 0;
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				line = line.trim();
				String[] columns = line.split(" ");
				
				initIterations(columns.length - 1);

				if (fileCounter == 0) {
					timeStamps.add(Double.parseDouble(columns[0]));

					for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
						runningMetrics.get(observable_num).add(new RunningMetric());
					}
				}

				for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
					if (lineCounter >= runningMetrics.get(observable_num).size()) {
						runningMetrics.get(observable_num).add(new RunningMetric());
					}
					runningMetrics.get(observable_num).get(lineCounter).add(Integer.parseInt(columns[observable_num+1]));
				}

				lineCounter++;
			}
			
			reader.close();
			System.err.println("Read " + lineCounter + " from file " + filename);
		}
		
		createTMPReport();
	}

}
