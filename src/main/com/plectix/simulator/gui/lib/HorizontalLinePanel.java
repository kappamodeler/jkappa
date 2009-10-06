package com.plectix.simulator.gui.lib;

import java.awt.Dimension;

/**
 * Simple panel class to draw a horizontal line. We simply constrain the panel
 * to only be a single pixel tall and let it draw its background color. The
 * "bright" flag passed into the constructor indicates whether this should be a
 * bright or dim line.
 *
 * @author ecemis
 */

@SuppressWarnings("serial")
public class HorizontalLinePanel extends GridBagPanel {
	
    public HorizontalLinePanel(int preferredWidth, boolean bright) {
            super();

            setLayout(null);
            if (bright) {
                    setBackground(UIProperties.getColor("horizontal.line.bright.color"));
            } else {
                    setBackground(UIProperties.getColor("horizontal.line.dark.color"));
            }
            
            setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
            setPreferredSize(new Dimension(preferredWidth, 1));
            setMinimumSize(new Dimension(preferredWidth, 1));
    }

}
