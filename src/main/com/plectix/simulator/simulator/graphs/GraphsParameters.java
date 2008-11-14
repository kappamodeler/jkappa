package com.plectix.simulator.simulator.graphs;

import java.awt.Font;

public class GraphsParameters {
	public int minX;
	public int width;
	public int minY;
	public int height;
	
	public int axisFromLeftBound;
	public int axisWidth;

	public int axisFromUpperBound;
	public int axisHeight;

	public int axisStripesLength;

	public int firstStripeGap;
	public int indicesToLineGap;

	public int minGapBetweenIndices;
	
	public int fontSize;		
	public Font baseFont;
	public Font boldFont;

	public GraphsParameters(int minX, int width, int minY, int height) {
		this.minX = minX;
		this.minY = minY;
		this.width = width;
		this.height  = height;
		
		axisFromLeftBound = (int) (width * 0.25);
		axisWidth = (int) (width * 0.66);

		axisFromUpperBound = (int) (height * 0.2);
		axisHeight = (int) (height * 0.57);

		axisStripesLength = (int) (width * 0.03);

		firstStripeGap = (int) (axisWidth * 0.042);
		indicesToLineGap = (int) (axisWidth * 0.126);

		minGapBetweenIndices = 4;
		
		fontSize = (int) (width * 0.05);		
		baseFont = new Font("Arial", 0, fontSize);
		boldFont = new Font("Arial", Font.BOLD, fontSize);
	}
}
