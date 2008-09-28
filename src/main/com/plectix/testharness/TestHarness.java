package com.plectix.testharness;
import java.io.BufferedReader;
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
		public String getName() {
			return name;
		}

		private String name;
		private String command;
		private String output;

		public Test(String command, String output, String name) {
			this.command = command;
			this.output = output;
			this.name = name;
		}
	
		
		public String getCommand() {
			return command;
		}
		
		public String getOutput() {
			return output;
		}
		
	}
	
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please give the configuration file name as first argument");
			return;
		}
			
		List<Test> tests = getTests(args[0]);
		if (tests == null)
			return;
		
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
				outputs.add(getLines(bufferedReader));
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
					System.out.println("Differences between " + tests.get(j).getName() + " and " + tests.get(i).getName() + " tests");
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
				String name = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				result.add(new Test(command, output, name));
			}
			return result;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static List<String> getLines(BufferedReader bufferedReader) {
		try {
			List<String> result = new ArrayList<String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
