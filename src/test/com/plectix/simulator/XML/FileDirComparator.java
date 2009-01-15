package com.plectix.simulator.XML;

import java.io.*;
import com.plectix.simulator.util.*;

public class FileDirComparator {
	private final String myFirstDir;
	private final String mySecondDir;
	private final String[] myExtensions;
	
	public FileDirComparator(String first, String second, String...extension) {
		myFirstDir = first;
		mySecondDir = second;
		myExtensions = extension;
	}
	
	private boolean fileNameAccepted(String fileName) { 
		for (String extension : myExtensions) {
			if (fileName.endsWith("." + extension)) {
				return true;
			}
		}
		return false;
	}
	
	public String compare() throws FileNotFoundException, IOException {
		MessageConstructor mc = new MessageConstructor();
		
		for (String fileName : new File(myFirstDir).list()) {
			if (fileNameAccepted(fileName)) {
				int similarity = (new FileComparator(
						myFirstDir + fileName, mySecondDir + fileName)).compare(); 
				if (similarity != -1) {
					mc.addValue(fileName);
					mc.addComment("line " + similarity);
				}
			}
		}
		return mc.getMessage();
	}
}
