package com.plectix.simulator.XML;
import java.io.*;

public class SimplxBatFileCreator {
	private static final double[] myTime = new double[] { 0.01, 1000, 
        0.5, 100, 
        50, 50, 
        25, 25, 
        10, 10, 
        10,     40, 
        10, 10, 
        15, 10 //, 1000
        , 15, 20 };
	
	//TODO add bat-file execution
	public static void main(String[] args) {
		String prefix = "simplx --sim ";
		String time = " --time ";
		String xml = " --xml-session-name \"out_simplx/";
		File dir = new File(PathFinder.MAIN_DIR + "source_ka/");
		File out = new File(PathFinder.MAIN_DIR + "out.bat");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(out);
			if (!dir.exists() || !dir.isDirectory()) {
				//System.out.println("ONO");
			}
			int i = 0;
			for (String file : dir.list()) {
				if (file.endsWith(".ka")) {
					pw.println(prefix + "\"source_ka/" + file + "\"" 
							+ time + myTime[i] + xml + file + "_simplx.xml\"");
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			pw.close();
		}
	}
}
