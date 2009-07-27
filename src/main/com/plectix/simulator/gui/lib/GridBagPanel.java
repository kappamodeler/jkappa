package com.plectix.simulator.gui.lib;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * A JPanel subclass with some useful functionality for dealing
 * with GridBagLayouts.
 * 
 * @author ecemis
 */
public class GridBagPanel extends JPanel {
	
	private boolean debug;

	/**
	 * Create a panel with a GridBagLayout.
	 */
	public GridBagPanel() {
		super();
		setLayout(new GridBagLayout());

		// Opaque=true is the JPanel default, but Synth overrides this and
		// makes it opaque=false by default.  We want the default to remain
		// the same.
		setOpaque(true);
	}

	/**
	 * Turn on/off debug mode for this panel.  In debug mode, the panel
	 * boundary is drawn in red, the cell boundaries are drawn in blue,
	 * and the component boundaries are drawn in green.
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;

		for (Component c : getComponents()) {
			if (c instanceof GridBagPanel)
				((GridBagPanel) c).setDebug(debug);
		}
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);

		if (debug) {
			// Draw component boundaries
			g.setColor(Color.GREEN);
			for (Component c : getComponents())
				g.drawRect(c.getX(), c.getY(), c.getWidth() - 1,
						c.getHeight() - 1);

			GridBagLayout layout = (GridBagLayout) getLayout();
			int[][] dim = layout.getLayoutDimensions();
			int[] widths = dim[0];
			int[] heights = dim[1];
			Point origin = layout.getLayoutOrigin();

			// Compute x locations
			int sx = origin.x; // starting x
			int ax[] = new int[widths.length + 1];
			ax[0] = sx;
			for (int i = 0; i < widths.length; i++)
				ax[i + 1] = ax[i] + widths[i];
			int ex = ax[ax.length - 1]; // ending x

			// Compute y locations
			int sy = origin.y; // starting y
			int ay[] = new int[heights.length + 1];
			ay[0] = sy;
			for (int i = 0; i < heights.length; i++)
				ay[i + 1] = ay[i] + heights[i];
			int ey = ay[ay.length - 1]; // ending y

			// Draw cell boundaries
			g.setColor(Color.BLUE);
			for (int x : ax)
				g.drawLine(x, sy, x, ey);
			for (int y : ay)
				g.drawLine(sx, y, ex, y);

			// Draw overall boundary
			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	/**
	 * Override the add() method so we can catch the common error when using
	 * GridBagLayout of forgetting to pass in the GridBagConstraints.
	 * 
	 * @param comp Component to add
	 * @see java.awt.Container#add(java.awt.Component)
	 */
	@Override
	public Component add(Component comp) {
		throw new UnsupportedOperationException("Missing GridBagConstraints");
	}

	/**
	 * Override the add() method so we can catch the common error when using
	 * GridBagLayout of forgetting to set the layout of the container to 
	 * GridBagLayout.
	 * 
	 * @param comp Component to add
	 * @param data Layout data (GridBagConstraints)
	 * @see java.awt.Container#add(java.awt.Component,java.lang.Object)
	 */
	@Override
	public void add(Component comp, Object data) {
		if (data == null || !(data instanceof GridBagConstraints))
			throw new UnsupportedOperationException(
					"Missing GridBagConstraints");

		super.add(comp, data);

		if (debug && comp instanceof GridBagPanel)
			((GridBagPanel) comp).setDebug(debug);
	}

	/**
	 * Shortcut for:
	 * <pre>
	 *       add(Box.createGlue(), gc.incy().fillBoth());
	 * </pre>
	 * @param gc
	 */
	public void addGlue(GridBagConstraintsEx gc, int direction) {
		switch (direction) {
		case GridBagConstraints.BOTH:
			add(Box.createGlue(), gc.incy().fillBoth());
			break;
		case GridBagConstraints.VERTICAL:
			add(Box.createVerticalGlue(), gc.incy().fillVertical());
			break;
		case GridBagConstraints.HORIZONTAL:
			add(Box.createHorizontalGlue(), gc.incx().fillHorizontal());
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * Create an "enhanced" GridBagConstraints object with some handy methods.
	 * 
	 * @return enhanced GridBagConstraints
	 */
	public GridBagConstraintsEx createNewConstraints() {
		return new GridBagConstraintsEx();
	}

	public static class GridBagConstraintsEx extends GridBagConstraints {
		public GridBagConstraintsEx() {
			gridx = 0;
			gridy = 0;
			insets = new Insets(0, 0, 0, 0);
		}

		public GridBagConstraintsEx xy(int gridx, int gridy) {
			this.gridx = gridx;
			this.gridy = gridy;
			return this;
		}

		public GridBagConstraintsEx size(int gridwidth, int gridheight) {
			this.gridwidth = gridwidth;
			this.gridheight = gridheight;
			return this;
		}

		public GridBagConstraintsEx fill(int fill, float weightx, float weighty) {
			this.fill = fill;
			this.weightx = weightx;
			this.weighty = weighty;
			return this;
		}

		public GridBagConstraintsEx fillHorizontal() {
			fill = HORIZONTAL;
			weightx = 1;
			weighty = 0;
			return this;
		}

		public GridBagConstraintsEx fillVertical() {
			fill = VERTICAL;
			weightx = 0;
			weighty = 1;
			return this;
		}

		public GridBagConstraintsEx fillBoth() {
			fill = BOTH;
			weightx = 1;
			weighty = 1;
			return this;
		}

		public GridBagConstraintsEx fillNone() {
			fill = NONE;
			weightx = 0;
			weighty = 0;
			return this;
		}

		public GridBagConstraintsEx insets(int top, int left, int bottom,
				int right) {
			insets = new Insets(top, left, bottom, right);
			return this;
		}

		public GridBagConstraintsEx anchor(int anchor) {
			this.anchor = anchor;
			return this;
		}

		public GridBagConstraintsEx anchorCenter() {
			return anchor(CENTER);
		}

		public GridBagConstraintsEx anchorLeft() {
			return anchor(LINE_START);
		}

		public GridBagConstraintsEx anchorRight() {
			return anchor(LINE_END);
		}

		public GridBagConstraintsEx anchorTopLeft() {
			return anchor(FIRST_LINE_START);
		}

		public GridBagConstraintsEx incx() {
			gridx++;
			return this;
		}

		public GridBagConstraintsEx incy() {
			gridy++;
			return this;
		}

		public GridBagConstraintsEx span(int width, int height) {
			gridwidth = width;
			gridheight = height;
			return this;
		}

		public GridBagConstraintsEx row(int row) {
			gridy = row;
			return this;
		}

		public GridBagConstraintsEx col(int col) {
			gridx = col;
			return this;
		}
	}
}
