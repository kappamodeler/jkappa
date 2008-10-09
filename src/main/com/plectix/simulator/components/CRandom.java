package com.plectix.simulator.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Scanner;

public class CRandom {

	public static void main(String[] args) {

		try {
			Runtime r = Runtime.getRuntime();

			Process process = r
					.exec("C:\\Documents and Settings\\prudnikova\\workspace\\simulator\\data\\rand1.exe");

			InputStream inputStream = process.getInputStream();
			Scanner scanner = new Scanner(inputStream);
			PrintWriter writer = new PrintWriter(process.getOutputStream());
         
            writer.print("1");
			writer.println();
			writer.flush();
			
			System.out.print(scanner.nextLine());
			
            writer.print("3");
            writer.println();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
