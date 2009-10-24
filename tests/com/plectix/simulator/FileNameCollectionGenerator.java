package com.plectix.simulator;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public class FileNameCollectionGenerator {

	private static final String FILENAME_EXTENSION = ".test";

	private static LinkedList<Object[]> collectionFiles;
	private static String pathForFiles;

	public static Collection<Object[]> getAllFileNames(String path) {
		collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, null, false, false);
	}

	public static Collection<Object[]> getAllFileNamesWithPath(String path) {
		collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, null, false, true);
	}
	
	public static Collection<Object[]> getAllFileNamesWithOutPath(String path, String startWithFile) {
		collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, startWithFile, false, false);
	}

	//Modify the file name 
	// file name input - startWithFile[xxx]FILENAME_EXTENSION
	// file name output - [xxx]
	public static Collection<Object[]> getAllFileNamesWithPathWithModifyName(
			String path, String startWithFile) {
		collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, startWithFile, true, true);
	}
	
	//Does not modify the file name
	public static Collection<Object[]> getAllFileNamesWithPath(
			String path, String startWithFile) {
		collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, startWithFile, false, true);
	}

	public static Collection<Object[]> addAllFileNamesWithPathWithModifyName(
			String path, String startWithFile) {
		if (collectionFiles == null)
			collectionFiles = new LinkedList<Object[]>();
		return generateFileName(path, startWithFile, true, true);
	}	
	



	private static Collection<Object[]> generateFileName(String path,
			String startWith, boolean isModifyFileName, boolean addPath) {
		pathForFiles = path;
		try {
			File testFolder = new File(path);
			if (!testFolder.isDirectory())
				return collectionFiles;
			for (String fileName : testFolder.list()) {
				if (!isCorrectFileName(fileName, startWith))
					continue;
				if (!isCorrectFileNameExtension(fileName))
					continue;
				if (isModifyFileName)
					fileName = modifyFileNameDeliteStartAndEndWith(fileName,
							startWith);
				addFileName(fileName, addPath);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot generate a file name !!!");
		}
		return collectionFiles;
	}

	private static boolean isCorrectFileName(String fileName, String startWith) {
		if (startWith == null)
			return true;
		return fileName.startsWith(startWith);
	}

	private static boolean isCorrectFileNameExtension(String fileName) {
		return fileName.endsWith(FILENAME_EXTENSION);
	}

	private static String modifyFileNameDeliteStartAndEndWith(String fileName,
			String fileNameStartWith) {

		return fileName.split(fileNameStartWith)[1].split(FILENAME_EXTENSION)[0];

	}

	private static void addFileName(String fileName, boolean isAddPath) {
		if (isAddPath)
			collectionFiles.add(new Object[] { fileName, pathForFiles });
		else
			collectionFiles.add(new Object[] { fileName });
	}
}
