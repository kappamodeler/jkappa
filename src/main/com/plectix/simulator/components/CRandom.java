package com.plectix.simulator.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CRandom {

	public static void main(String[] args) {
		
		try {
			Process process = Runtime.getRuntime().exec("C:\\workspace\\simulator\\data\\fib.exe 5");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			
//			bufferedWriter.write("5");
//			bufferedWriter.flush();
			
			System.out.println(bufferedReader.readLine());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
}
