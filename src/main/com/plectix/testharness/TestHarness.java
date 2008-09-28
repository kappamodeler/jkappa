package com.plectix.testharness;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class TestHarness {

	private static class Test {
		public Test(String command, String output) {
			this.command = command;
			this.output = output;
		}
	
		private String command;
		private String output;
		
		public String getCommand() {
			return command;
		}
		
		public String getOutput() {
			return output;
		}
		
	}
	
	
	public static void main(String[] args) {
		List<Test> tests = getTests(args[0]);
		
		List<List<String>> outputs = new ArrayList<List<String>>(); 
		
		for (Test test : tests) {
			try {
				Process process = Runtime.getRuntime().exec(test.getCommand());
				process.waitFor();
				BufferedReader bufferedReader;
				if (test.getOutput().equals("console")) {
					bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				} else {
					bufferedReader = new BufferedReader(new FileReader(test.getOutput()));
				}
				outputs.add(getFileLines(bufferedReader));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int size = tests.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < i; j++) {
				List<String> differences = Diff.diff(outputs.get(i),  outputs.get(j));
				if (!differences.isEmpty()) {
					System.out.println("Differences between " + i + " and " + j + " tests");
					for (String string : differences) {
						System.out.println(string);
					}
				}
			}
		}
	}

	private static List<Test> getTests(String xmlFileName) {
		List<Test> result = new ArrayList<Test>();
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "/tests/test";
			InputSource inputSource = new InputSource(
					xmlFileName);
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource,
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				String command = nodes.item(i).getAttributes().getNamedItem("command").getNodeValue();
				String output = nodes.item(i).getAttributes().getNamedItem("output").getNodeValue();
				result.add(new Test(command, output));
			}
			return result;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<String> getFileLines(BufferedReader bufferedReader) {
		try {
			List<String> result = new ArrayList<String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
