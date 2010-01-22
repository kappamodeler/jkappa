package com.plectix.simulator.consoleOutput;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class BackingUpPrintStream extends PrintStream {
	public List<String> content = new LinkedList<String>();
	private StringBuffer currentLine = new StringBuffer();
	
	public BackingUpPrintStream() {
		super(System.out);
	}
	
	@Override
	public void println() {
		flushBuffer();
	}
	
	@Override
	public void println(String s) {
		print(s);
		println();
	}
	
	private void trimBuffer() {
		String[] words = currentLine.toString().split("\n");
		for (int i = 0; i < words.length - 1; i++) {
			this.println(words[i]);
		}
		currentLine = new StringBuffer(words[words.length - 1]);
	}
	
	private void flushBuffer() {
		addWord(currentLine.toString());
		currentLine = new StringBuffer();
	}
	
	public void addWord(String s) {
		if (!s.trim().isEmpty()) {
			content.add(s.trim());
		}
	}
	
	@Override
	public void print(String s) {
		currentLine.append(s);
		trimBuffer();
	}
	
	public String getContentItem(int index) {
		if (index < content.size()) {
			return content.get(index);
		} else {
			return null;
		}
	}
}
