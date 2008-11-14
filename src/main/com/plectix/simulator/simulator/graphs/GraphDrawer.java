package com.plectix.simulator.simulator.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.ObservablesConnectedComponent;

public class GraphDrawer {

	private static final String FILE_NAME_BASE = "simplx";
	private static final int IMAGE_WIDTH = 800;
	private static final int IMAGE_HEIGHT = 600;
	private static final String FILE_EXTENSION = "PNG";

	static BasicStroke dottedLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 1.0f, 1.0f, 1.0f, 1.0f,
					1.0f, 7.0f }, 0.0f);

	private CObservables observables;

	public final void createGraphs(CObservables observables, double minValueX,
			double maxValueX) {
		this.observables = observables;
		File outputFile = new File(FILE_NAME_BASE + "." + FILE_EXTENSION);
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Graphics2D graphics;
		BufferedImage bi = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
				BufferedImage.TYPE_BYTE_INDEXED);

		graphics = bi.createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());

		GraphsParameters gp = new GraphsParameters(0, IMAGE_WIDTH, 0,
				IMAGE_HEIGHT);
		graphics.setFont(gp.baseFont);

		int maxValueY = getMaxYValue();
		int minValueY = 0;

		drawAxis(graphics, gp, minValueX, maxValueX);

		drawPlot(graphics, gp, minValueX, maxValueX, minValueY, maxValueY,
				Color.blue);

		try {
			ImageIO.write(bi, FILE_EXTENSION, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private final int getMaxYValue() {
		int max = Integer.MIN_VALUE;

		for (ObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList())
			for (Integer r : obsCC.getCountList()) {
				if (max < r)
					max = r;
			}

		return max;
	}

	private final void drawAxis(Graphics2D graphics, GraphsParameters gp,
			double minValue, double maxValue) {
		graphics.setColor(Color.black);
		graphics.drawRect(gp.minX + gp.axisFromLeftBound,
				gp.axisFromUpperBound, gp.axisWidth, gp.axisHeight);

		Stroke oldStroke = graphics.getStroke();
		graphics.setStroke(dottedLine);

		graphics.drawLine(gp.minX + gp.axisFromLeftBound, gp.axisFromUpperBound
				+ gp.firstStripeGap, gp.minX + gp.axisFromLeftBound
				+ gp.axisWidth, gp.axisFromUpperBound + gp.firstStripeGap);
		graphics.drawLine(gp.minX + gp.axisFromLeftBound, gp.axisFromUpperBound
				+ gp.axisHeight - gp.firstStripeGap, gp.minX
				+ gp.axisFromLeftBound + gp.axisWidth, gp.axisFromUpperBound
				+ gp.axisHeight - gp.firstStripeGap);

		graphics.setStroke(oldStroke);
		// drawYCoordinateStripes(graphics, gp);

		// drawXCoordinateStripes(graphics, gp, maxValue, minValue);
	}

	private final void drawYCoordinateStripes(Graphics2D graphics,
			GraphsParameters gp) {
		int stripesNumber = 6;
		int dy = (gp.axisHeight - 2 * gp.firstStripeGap) / (stripesNumber - 1);
		double scaleMarkUnit = 0.2;

		AffineTransform t = new AffineTransform(); // write vertical
		t.rotate(-java.lang.Math.PI / 2., 0, 0);
		graphics.setTransform(t);

		for (int i = 0; i < stripesNumber; i++) {

			int cy = gp.axisFromUpperBound + gp.axisHeight - gp.firstStripeGap
					- dy * i;
			graphics
					.drawLine(-cy, gp.minX + gp.axisFromLeftBound
							- gp.axisStripesLength, -cy, gp.minX
							+ gp.axisFromLeftBound);

			String out = String.format("%.1f", scaleMarkUnit * i);
			int stringLength = graphics.getFontMetrics().stringWidth(out);
			graphics.drawString(out, -cy - stringLength / 2, gp.minX
					+ gp.axisFromLeftBound - gp.indicesToLineGap);
		}

		t.rotate(java.lang.Math.PI / 2., 0, 0);
		graphics.setTransform(t);
	}

	private final void drawXCoordinateStripes(Graphics2D graphics,
			GraphsParameters gp, double maxValue, double minValue) {

		XIndicesValues xIndicesValues = calculateXIndices(minValue, maxValue);

		int dx = (xIndicesValues.indicesCount - 1 == 0) ? 0
				: (int) ((double) (gp.axisWidth - 2 * gp.firstStripeGap) / (xIndicesValues.indicesCount - 1));
		double scaleMarkUnit = (xIndicesValues.indicesCount - 1 == 0) ? 0
				: (xIndicesValues.endIndexValue - xIndicesValues.startIndexValue)
						/ (double) (xIndicesValues.indicesCount - 1);

		int lastIndexRightBorder = 0;
		for (int i = 0; i < xIndicesValues.indicesCount; i++) {
			int cx = gp.minX + gp.axisFromLeftBound + gp.firstStripeGap + dx
					* i;
			graphics.drawLine(cx, gp.axisFromUpperBound + gp.axisHeight, cx,
					gp.axisFromUpperBound + gp.axisHeight
							+ gp.axisStripesLength);

			int signsAfterComma;
			if (xIndicesValues.differenceExp <= 0) {
				if (xIndicesValues.differenceUnitsCount == 1) {
					signsAfterComma = 2;
				} else {
					signsAfterComma = -xIndicesValues.differenceExp;
				}
			} else {
				signsAfterComma = 0;
			}
			String out = String.format("%." + signsAfterComma + "f",
					xIndicesValues.startIndexValue + scaleMarkUnit * i);

			int stringLength = graphics.getFontMetrics().stringWidth(out);
			if (lastIndexRightBorder < cx - stringLength / 2
					- gp.minGapBetweenIndices) {
				lastIndexRightBorder = cx + stringLength / 2;
				graphics.drawString(out, cx - stringLength / 2,
						gp.axisFromUpperBound + gp.axisHeight
								+ gp.indicesToLineGap);
			}
		}
	}

	private final XIndicesValues calculateXIndices(double minValue,
			double maxValue) {
		double difference = maxValue - minValue;

		// get difference order
		double differenceOrder = 1.;
		int differenceExp = 0;
		if (difference != 0) {
			while (true) {
				double temp = difference / differenceOrder;

				if (temp > 5) {
					differenceOrder *= 10;
					differenceExp++;
				} else if (temp < 0.5) {
					differenceOrder /= 10;
					differenceExp--;
				} else {
					break;
				}
			}
		}

		if (maxValue > 0) {
			if (Math.round(maxValue / differenceOrder) - maxValue
					/ differenceOrder != 0) {
				maxValue += differenceOrder;
			}
		}
		if (minValue < 0) {
			if (Math.round(minValue / differenceOrder) - minValue
					/ differenceOrder != 0) {
				minValue -= differenceOrder;
			}
		}

		double minIndex = ((int) (minValue / differenceOrder))
				* differenceOrder;
		double maxIndex = ((int) (maxValue / differenceOrder))
				* differenceOrder;

		if (minIndex == maxIndex) {
			minIndex -= Math.abs(minIndex * 0.1);
			maxIndex += Math.abs(maxIndex * 0.1);
		}

		int differenceUnitsCount = (int) (maxIndex / differenceOrder)
				- (int) (minIndex / differenceOrder);
		int indicesCount = 0;

		switch (differenceUnitsCount) {
		case 0:
		case 1:
		case 2:
			indicesCount = 3;
			break;
		case 3:
		case 6:
		case 9:
			indicesCount = 4;
			break;
		case 4:
		case 8:
			indicesCount = 5;
			break;
		case 5:
			indicesCount = 6;
			break;
		case 7:
			indicesCount = 5;
			maxIndex += differenceOrder;
			break;
		}

		return new XIndicesValues(minIndex, maxIndex, indicesCount,
				differenceUnitsCount, differenceExp);
	}

	private class XIndicesValues {
		public double startIndexValue;
		public double endIndexValue;
		public int indicesCount;
		public int differenceUnitsCount;
		public int differenceExp;

		public XIndicesValues(double startIndexValue, double endIndexValue,
				int indicesNumber, int differenceUnitsCount, int differenceExp) {
			this.startIndexValue = startIndexValue;
			this.endIndexValue = endIndexValue;
			this.indicesCount = indicesNumber;
			this.differenceUnitsCount = differenceUnitsCount;
			this.differenceExp = differenceExp;
		}
	}

	private final void drawPlot(Graphics2D graphics, GraphsParameters gp,
			double minValueX, double maxValueX, int minValueY, int maxValueY,
			Color color) {

		Color oldColor = graphics.getColor();
		graphics.setColor(color);

		for (ObservablesConnectedComponent obsCC : observables
				.getConnectedComponentList()) {
			double pointX = 0;
			double pointY = 0;
			double normX;
			if (maxValueX == minValueX) {
				normX = 0;
			} else {
				normX = (double) (gp.axisWidth - 2 * gp.firstStripeGap)
						/ (maxValueX - minValueX);
			}
			double normY = (double) (gp.axisHeight - 2 * gp.firstStripeGap)
					/ (maxValueY);

			double prevPointX = gp.minX
					+ (obsCC.getCountList().get(0) - minValueX) * normX
					+ gp.axisFromLeftBound + gp.firstStripeGap;
			double prevPointY = gp.axisFromUpperBound + gp.axisHeight
					- gp.firstStripeGap;

			int i = 0;
			for (Integer dt : obsCC.getCountList()) {

				pointX = gp.minX
						+ (observables.getCountTimeList().get(i) - minValueX)
						* normX + gp.axisFromLeftBound + gp.firstStripeGap;
				pointY = gp.axisFromUpperBound + gp.axisHeight - dt * normY
						- gp.firstStripeGap;

				if (prevPointX != pointX) {
					graphics.drawLine((int) prevPointX, (int) prevPointY,
							(int) pointX, (int) pointY);
				}

				prevPointX = pointX;
				prevPointY = pointY;
				i++;
			}
		}

		graphics.setColor(oldColor);
	}

}
