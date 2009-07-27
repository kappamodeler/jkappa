package com.plectix.simulator.utilsForTest;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileWrapper {
	
	
	private PrintWriter out;
	
	public FileWrapper(String path) {
		
	
		try {

			out = new PrintWriter(path);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("File for result : FileNotFoundException");
			e.printStackTrace();
		}
	}

	public void writeInFile(String str) {
			out.println(str);
	}
	
	public void closeFile() {
			out.close();
	}
	
	


}
