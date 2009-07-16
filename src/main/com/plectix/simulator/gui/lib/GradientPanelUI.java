package com.plectix.simulator.gui.lib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

public class GradientPanelUI extends PanelUI {

	private Color colorA, colorB;
	
	public GradientPanelUI(Color a, Color b){
		colorA = a;
		colorB = b;
	}
	/**
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics, javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g,JComponent c) {

		Graphics2D g2 = (Graphics2D) g;

		LinearGradientPaint p;
		p = new LinearGradientPaint(0.0f, 0.0f, 0f, c.getHeight(),
		// new float[] { 0.0f, 0.2f, 1.0f },
		// new Color[] { Color.DARK_GRAY,
		// Color.DARK_GRAY,
		// Color.BLACK
		// });
		new float[] { 0.0f, 1.0f }, new Color[] { colorA, colorB });
		// new Color[] { new Color(241, 90, 34),
		// new Color(76, 28, 10),
		// new Color(38, 14, 5) //new Color(76, 28, 10) //new Color(153, 57, 21)
		// });

		Paint oldPaint = g2.getPaint();
		g2.setPaint(p);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight());
		g2.setPaint(oldPaint);

	}
}
