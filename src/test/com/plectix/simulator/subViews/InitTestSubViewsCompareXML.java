package com.plectix.simulator.subViews;

import com.plectix.simulator.utilsForTest.GenerateXMLByModel;


public class InitTestSubViewsCompareXML extends GenerateXMLByModel {

	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	
	private static final String FILENAME_EXTENSION_TEMP = ".tmp";
	
	public InitTestSubViewsCompareXML(String log4JPropertiesFilename) {
		super(log4JPropertiesFilename);
	}

	@Override
	public String getComparePath() {
		return getDirectoryInside() + "~session" + getMyCountInside() + FILENAME_EXTENSION;
	}

	@Override
	public String getSessionPath() {
		return getSessionPathInside() + "~session" + getMyCountInside() + FILENAME_EXTENSION_TEMP;
	}

	@Override
	public String[] prepareTestModelArgs(String count) {

		String[] args = new String[5];
		
		args[0] = "--short-console-output";
		args[1] = "--compute-sub-views";
		args[2] = "--contact-map";
		args[3] = getDirectoryInside() + "~kappa" + count + FILENAME_EXTENSION;
		args[4] = "--no-build-influence-map";
		return args;
		
	}

	@Override
	public String[] prepareTestArgs(String count) {
			junit.framework.Assert.fail("[Error] Incorrect path. Path has not folder 'model'.");
			return new String[] {};
	}
	

}
